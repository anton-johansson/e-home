/**
 * Copyright 2017 Anton Johansson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.anton.ehome.metrics;

import static com.anton.ehome.utils.ThreadUtils.sleepQuietly;
import static java.lang.System.currentTimeMillis;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anton.ehome.common.IDaemon;
import com.anton.ehome.conf.Config;
import com.anton.ehome.conf.IConfigService;
import com.anton.ehome.conf.ZWaveConfig;
import com.anton.ehome.conf.ZWaveMonitoringConfig;
import com.anton.ehome.dao.IMetricsDao;
import com.anton.ehome.zwave.Device;
import com.anton.ehome.zwave.IZWaveController;
import com.anton.ehome.zwave.IZWaveManager;
import com.google.inject.Inject;
import com.whizzosoftware.wzwave.commandclass.MeterCommandClass;
import com.whizzosoftware.wzwave.commandclass.MeterCommandClass.MeterReadingValue;

/**
 * Polls devices for new metrics and stores in the database.
 */
class MetricsDaemon implements IDaemon
{
    private static final Logger LOG = LoggerFactory.getLogger(MetricsDaemon.class);
    private static final int MILLISECONDS_PER_SECOND = 1000;
    private static final int INTERVAL = 1000;

    private final Map<Object, Long> timestamps = new HashMap<>();
    private final MetricsLoop loop = new MetricsLoop();
    private final Thread thread = new Thread(loop);
    private final IZWaveManager zwaveManager;
    private final IConfigService configService;
    private final IMetricsDao metricsDao;
    private boolean isDone;

    @Inject
    MetricsDaemon(IZWaveManager zwaveManager, IConfigService configService, IMetricsDao metricsDao)
    {
        this.zwaveManager = zwaveManager;
        this.configService = configService;
        this.metricsDao = metricsDao;
    }

    @Override
    public boolean start()
    {
        LOG.info("Starting the metrics daemon");
        thread.start();
        return true;
    }

    @Override
    public void stop()
    {
        LOG.info("Stopping the metrics daemon");
        isDone = true;
    }

    /**
     * Handles the loop.
     */
    private class MetricsLoop implements Runnable
    {
        @Override
        public void run()
        {
            while (!isDone)
            {
                LOG.trace("Running new iteration");
                Config config = configService.getCurrentConfig();

                try
                {
                    handleZWave(config);
                }
                catch (Exception e)
                {
                    LOG.warn("Exception occurred in loop", e);
                }
                finally
                {
                    sleepQuietly(INTERVAL);
                }
            }
        }

        private void handleZWave(Config config)
        {
            List<IZWaveController> controllers = zwaveManager.getControllers();
            for (IZWaveController controller : controllers)
            {
                ZWaveConfig zwaveConfig = config.getZwaveConfigs()
                        .stream()
                        .filter(c -> c.getName().equals(controller.getName()))
                        .findAny()
                        .orElse(null);

                if (zwaveConfig == null)
                {
                    LOG.info("Could not find any configuration for controller '{}'", controller.getName());
                    continue;
                }

                for (ZWaveMonitoringConfig monitoringConfig : zwaveConfig.getMonitoringValues())
                {
                    byte nodeId = monitoringConfig.getNodeId();
                    Device device = controller.getDevices()
                            .stream()
                            .filter(d -> d.getNodeId() == nodeId)
                            .findAny()
                            .orElse(null);

                    if (device == null)
                    {
                        LOG.info("Could not find a Z-Wave device with nodeId {} in controller '{}'", nodeId, controller.getName());
                        continue;
                    }

                    ZWaveDeviceKey key = new ZWaveDeviceKey(controller.getName(), nodeId);
                    long lastTimestamp = timestamps.getOrDefault(key, 0L);
                    if (currentTimeMillis() - lastTimestamp < monitoringConfig.getInterval() * MILLISECONDS_PER_SECOND)
                    {
                        LOG.trace("Not yet ready to poll Z-Wave device {} in controller", nodeId, controller.getName());
                        continue;
                    }

                    MeterCommandClass commandClass = (MeterCommandClass) device.getNode().getCommandClass(MeterCommandClass.ID);
                    MeterReadingValue reading = commandClass.getLastValue(monitoringConfig.getScale().getZWaveEquivalent());
                    if (reading == null)
                    {
                        LOG.info("No reading was found for Z-Wave device {} in controller '{}' for scale {}", nodeId, controller.getName(), monitoringConfig.getScale());
                        continue;
                    }

                    double value = reading.getCurrentValue();
                    LOG.info("Storing metric for Z-Wave device {} in controller '{}': {}", nodeId, controller.getName(), value);
                    metricsDao.save(nodeId, value);
                    timestamps.put(key, currentTimeMillis());
                }
            }
        }
    }

    /**
     * A key of a Z-Wave device.
     */
    private static class ZWaveDeviceKey
    {
        private final String controllerName;
        private final byte nodeId;

        private ZWaveDeviceKey(String controllerName, byte nodeId)
        {
            this.controllerName = controllerName;
            this.nodeId = nodeId;
        }

        @Override
        public int hashCode()
        {
            return java.util.Objects.hash(controllerName, nodeId);
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj == null || obj.getClass() != getClass())
            {
                return false;
            }
            if (obj == this)
            {
                return true;
            }

            ZWaveDeviceKey that = (ZWaveDeviceKey) obj;
            return new EqualsBuilder()
                    .append(this.controllerName, that.controllerName)
                    .append(this.nodeId, that.nodeId)
                    .isEquals();
        }

        @Override
        public String toString()
        {
            return reflectionToString(this, SHORT_PREFIX_STYLE);
        }
    }
}
