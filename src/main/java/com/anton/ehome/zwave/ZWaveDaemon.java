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
package com.anton.ehome.zwave;

import static com.anton.ehome.utils.Assert.requireNonBlank;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anton.ehome.common.IDaemon;
import com.anton.ehome.conf.Config;
import com.anton.ehome.conf.IConfigService;
import com.anton.ehome.conf.ZWaveConfig;
import com.anton.ehome.conf.ZWaveMonitoringConfig;
import com.google.inject.Inject;

/**
 * A daemon that manages the Z-Wave controllers.
 */
class ZWaveDaemon implements IDaemon, IZWaveManager
{
    private static final Logger LOG = LoggerFactory.getLogger(ZWaveDaemon.class);

    private final List<Controller> controllers = new ArrayList<>();
    private final IConfigService configService;

    @Inject
    ZWaveDaemon(IConfigService configService)
    {
        this.configService = configService;
    }

    @Override
    public boolean start()
    {
        Config config = configService.getCurrentConfig();
        for (ZWaveConfig zwave : config.getZwaveConfigs())
        {
            String name = zwave.getName();
            String serialPort = zwave.getSerialPort();
            addSerialPortToRXTX(serialPort);

            Set<Byte> monitoredDevices = zwave.getMonitoringValues()
                    .stream()
                    .map(ZWaveMonitoringConfig::getNodeId)
                    .collect(toSet());

            Controller controller = new Controller(name, serialPort, monitoredDevices);
            controller.start();
            controllers.add(controller);
        }
        return true;
    }

    @Override
    public void stop()
    {
        while (!controllers.isEmpty())
        {
            Controller controller = controllers.get(0);
            controller.stop();
            controllers.remove(0);
        }
    }

    private void addSerialPortToRXTX(String serialPort)
    {
        String value = System.getProperty("gnu.io.rxtx.SerialPorts", "");
        Set<String> serialPorts = Stream.of(value.split(","))
                .map(StringUtils::trim)
                .filter(StringUtils::isNotEmpty)
                .collect(toSet());

        serialPorts.add(serialPort);
        value = serialPorts.stream()
                .collect(joining(","));

        System.setProperty("gnu.io.rxtx.SerialPorts", value);
    }

    @Override
    public List<IZWaveController> getControllers()
    {
        return unmodifiableList(controllers);
    }

    @Override
    public void addController(String name, String serialPort)
    {
        addSerialPortToRXTX(serialPort);

        Controller controller = new Controller(name, serialPort, new HashSet<>());
        controller.start();
        controllers.add(controller);

        LOG.info("Successfully added controller");
    }

    @Override
    public void removeController(String name)
    {
        requireNonBlank(name, "name can't be blank");
        Controller controller = controllers.stream()
                .filter(c -> name.equals(c.getName()))
                .findAny()
                .get();
        controller.stop();
        controllers.remove(controller);

        LOG.info("Successfully removed controller");
    }
}
