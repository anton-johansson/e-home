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

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anton.ehome.dao.IMetricsDao;
import com.whizzosoftware.wzwave.commandclass.ManufacturerSpecificCommandClass;
import com.whizzosoftware.wzwave.commandclass.MeterCommandClass;
import com.whizzosoftware.wzwave.commandclass.MeterCommandClass.MeterReadingValue;
import com.whizzosoftware.wzwave.commandclass.MeterCommandClass.Scale;
import com.whizzosoftware.wzwave.controller.ZWaveController;
import com.whizzosoftware.wzwave.controller.ZWaveControllerListener;
import com.whizzosoftware.wzwave.controller.netty.NettyZWaveController;
import com.whizzosoftware.wzwave.node.NodeInfo;
import com.whizzosoftware.wzwave.node.ZWaveEndpoint;
import com.whizzosoftware.wzwave.node.ZWaveNode;
import com.whizzosoftware.wzwave.node.specific.PCController;

/**
 * Default implementation of {@link IZWaveController}.
 */
class Controller implements IZWaveController
{
    private static final Logger LOG = LoggerFactory.getLogger(Controller.class);

    private final List<Device> devices = new ArrayList<>();
    private final List<Consumer<Device>> deviceAddedListeners = new ArrayList<>();
    private final Set<Byte> monitoredDevices = new HashSet<>();
    private final IMetricsDao metricsDao;
    private final String name;
    private final String serialPort;
    private ZWaveController controller;

    Controller(IMetricsDao metricsDao, String name, String serialPort)
    {
        this.metricsDao = metricsDao;
        this.name = requireNonBlank(name, "name can't be blank");
        this.serialPort = requireNonBlank(serialPort, "serialPort can't be blank");
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getSerialPort()
    {
        return serialPort;
    }

    @Override
    public List<Device> getDevices()
    {
        return unmodifiableList(devices);
    }

    @Override
    public void onDeviceAdded(Consumer<Device> listener)
    {
        deviceAddedListeners.add(listener);
    }

    @Override
    public void startMonitor(byte nodeId)
    {
        monitoredDevices.add(nodeId);
    }

    /**
     * Starts the controller.
     */
    public void start()
    {
        LOG.info("Starting Z-Wave controller '{}'", name);

        File dataDirectory = new File("/home/anton/Documents/z-wave/stores/" + name);
        dataDirectory.mkdirs();

        controller = new NettyZWaveController(serialPort, dataDirectory);
        controller.setListener(new Listener());
        controller.start();
    }

    /**
     * Stops the controller.
     */
    public void stop()
    {
        LOG.info("Stopping Z-Wave controller '{}'", name);

        controller.stop();
        controller = null;
    }

    /**
     * Listens for events from the Z-Wave controller.
     */
    private class Listener implements ZWaveControllerListener
    {
        @Override
        public void onZWaveNodeAdded(ZWaveEndpoint node)
        {
            LOG.trace("#onZWaveNodeAdded: {}", node);

            Device device = new Device(node.getNodeId(), getDeviceType(node), (ZWaveNode) node);
            devices.add(device);
            deviceAddedListeners.forEach(listener -> listener.accept(device));
        }

        private String getDeviceType(ZWaveEndpoint node)
        {
            ManufacturerSpecificCommandClass commandClass = (ManufacturerSpecificCommandClass) node.getCommandClass(ManufacturerSpecificCommandClass.ID);
            if (commandClass != null)
            {
                return commandClass.getProductInfo().getManufacturer() + " " + commandClass.getProductInfo().getName();
            }
            if (PCController.class.equals(node.getClass()))
            {
                return "Controller";
            }
            return "Unknown";
        }

        @Override
        public void onZWaveNodeUpdated(ZWaveEndpoint node)
        {
            LOG.trace("#onZWaveNodeUpdated: {}", node);

            if (monitoredDevices.contains(node.getNodeId()))
            {
                MeterCommandClass meterCommandClass = (MeterCommandClass) node.getCommandClass(MeterCommandClass.ID);
                MeterReadingValue reading = meterCommandClass.getLastValue(Scale.Watts);
                double value = reading.getCurrentValue();
                metricsDao.save(node.getNodeId(), value);
            }
        }

        @Override
        public void onZWaveConnectionFailure(Throwable t)
        {
            LOG.warn("#onZWaveNodeAdded", t);
        }

        @Override
        public void onZWaveControllerInfo(String libraryVersion, Integer homeId, Byte nodeId)
        {
            LOG.trace("#onZWaveControllerInfo: {}, {}, {}", libraryVersion, homeId, nodeId);
        }

        @Override
        public void onZWaveInclusionStarted()
        {
            LOG.trace("#onZWaveInclusionStarted");
        }

        @Override
        public void onZWaveInclusion(NodeInfo nodeInfo, boolean success)
        {
            LOG.trace("#onZWaveInclusion: {}, {}", nodeInfo, success);
        }

        @Override
        public void onZWaveInclusionStopped()
        {
            LOG.trace("#onZWaveInclusionStopped");
        }

        @Override
        public void onZWaveExclusionStarted()
        {
            LOG.trace("#onZWaveExclusionStarted");
        }

        @Override
        public void onZWaveExclusion(NodeInfo nodeInfo, boolean success)
        {
            LOG.trace("#onZWaveExclusion: {}, {}", nodeInfo, success);
        }

        @Override
        public void onZWaveExclusionStopped()
        {
            LOG.trace("#onZWaveExclusionStopped");
        }
    }
}
