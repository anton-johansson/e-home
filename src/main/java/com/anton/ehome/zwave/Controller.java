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

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.whizzosoftware.wzwave.controller.ZWaveController;
import com.whizzosoftware.wzwave.controller.ZWaveControllerListener;
import com.whizzosoftware.wzwave.controller.netty.NettyZWaveController;
import com.whizzosoftware.wzwave.node.NodeInfo;
import com.whizzosoftware.wzwave.node.ZWaveEndpoint;

/**
 * Default implementation of {@link IZWaveController}.
 */
class Controller implements IZWaveController
{
    private static final Logger LOG = LoggerFactory.getLogger(Controller.class);

    private final String name;
    private final String serialPort;
    private ZWaveController controller;

    Controller(String name, String serialPort)
    {
        this.name = name;
        this.serialPort = serialPort;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public String getSerialPort()
    {
        return serialPort;
    }

    /**
     * Starts the controller.
     */
    public void start()
    {
        LOG.info("Starting Z-Wave controller '{}'", name);

        controller = new NettyZWaveController(serialPort, new File("/home/anton/Documents/z-wave/stores/" + name));
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
        }

        @Override
        public void onZWaveNodeUpdated(ZWaveEndpoint node)
        {
            LOG.trace("#onZWaveNodeUpdated: {}", node);
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
