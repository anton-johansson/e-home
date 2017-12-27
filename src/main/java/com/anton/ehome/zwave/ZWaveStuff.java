/**
 * Copyright 2018 Anton Johansson
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

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

import java.io.File;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.whizzosoftware.wzwave.commandclass.ManufacturerSpecificCommandClass;
import com.whizzosoftware.wzwave.commandclass.MeterCommandClass;
import com.whizzosoftware.wzwave.commandclass.MeterCommandClass.MeterReadingValue;
import com.whizzosoftware.wzwave.commandclass.MeterCommandClass.Scale;
import com.whizzosoftware.wzwave.controller.ZWaveController;
import com.whizzosoftware.wzwave.controller.ZWaveControllerListener;
import com.whizzosoftware.wzwave.controller.netty.NettyZWaveController;
import com.whizzosoftware.wzwave.node.NodeInfo;
import com.whizzosoftware.wzwave.node.ZWaveEndpoint;
import com.whizzosoftware.wzwave.product.ProductInfo;

/**
 * Contains the applications main entry-point.
 */
public class ZWaveStuff
{
    /**
     * The main entry-point.
     */
    public static void main(String[] args)
    {
        final String serialPort = "/dev/ttyACM0";
        addSerialPortTlRXTX(serialPort);

        ZWaveController controller = new NettyZWaveController(serialPort, new File("/home/anton/Documents/zwave/node-store"));
        controller.setListener(new Listener());
        controller.start();

        try (Scanner scanner = new Scanner(System.in))
        {
            scanner.nextLine();
        }

        controller.stop();
    }

    private static void addSerialPortTlRXTX(String serialPort)
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

    /**
     * Listens for node events.
     */
    private static class Listener implements ZWaveControllerListener
    {
        @Override
        public void onZWaveNodeAdded(ZWaveEndpoint node)
        {
            System.out.println("#onZWaveNodeAdded");
            System.out.println(node);

            ManufacturerSpecificCommandClass commandClass = (ManufacturerSpecificCommandClass) node.getCommandClass(ManufacturerSpecificCommandClass.ID);
            if (commandClass != null)
            {
                ProductInfo productInfo = commandClass.getProductInfo();
                String manufacturer = productInfo.getManufacturer();
                String name = productInfo.getName();

                System.out.println("NodeId " + node.getNodeId() + " is a '" + name + "' manufactured by '" + manufacturer + "'");
            }
        }

        @Override
        public void onZWaveNodeUpdated(ZWaveEndpoint node)
        {
            System.out.println("#onZWaveNodeUpdated");
            System.out.println(node);

            if (node.getNodeId() == 2)
            {
                MeterCommandClass commandClass = (MeterCommandClass) node.getCommandClass(MeterCommandClass.ID);
                MeterReadingValue value = commandClass.getLastValue(Scale.Watts);
                System.out.println("Watts is " + value.getCurrentValue() + ", previously " + value.getPreviousValue() + " (a change of " + value.getDelta() + ")");
            }
        }

        @Override
        public void onZWaveConnectionFailure(Throwable t)
        {
            System.out.println("#onZWaveConnectionFailure");
            t.printStackTrace();
        }

        @Override
        public void onZWaveControllerInfo(String libraryVersion, Integer homeId, Byte nodeId)
        {
            System.out.println("#onZWaveControllerInfo");
            System.out.println(libraryVersion);
            System.out.println(homeId);
            System.out.println(nodeId);
        }

        @Override
        public void onZWaveInclusionStarted()
        {
            System.out.println("#onZWaveInclusionStarted");
        }

        @Override
        public void onZWaveInclusion(NodeInfo nodeInfo, boolean success)
        {
            System.out.println("#onZWaveInclusion");
            System.out.println(nodeInfo);
            System.out.println(success);
        }

        @Override
        public void onZWaveInclusionStopped()
        {
            System.out.println("#onZWaveInclusionStopped");
        }

        @Override
        public void onZWaveExclusionStarted()
        {
            System.out.println("#onZWaveExclusionStarted");
        }

        @Override
        public void onZWaveExclusion(NodeInfo nodeInfo, boolean success)
        {
            System.out.println("#onZWaveExclusion");
            System.out.println(nodeInfo);
            System.out.println(success);
        }

        @Override
        public void onZWaveExclusionStopped()
        {
            System.out.println("#onZWaveExclusionStopped");
        }
    }
}
