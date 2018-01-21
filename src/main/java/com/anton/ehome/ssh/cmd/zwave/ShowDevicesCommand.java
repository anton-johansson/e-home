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
package com.anton.ehome.ssh.cmd.zwave;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.rightPad;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.builder.CompareToBuilder;

import com.anton.ehome.ssh.cmd.annotation.Command;
import com.anton.ehome.ssh.cmd.annotation.Option;
import com.anton.ehome.ssh.cmd.common.ICommand;
import com.anton.ehome.ssh.cmd.common.ICommunicator;
import com.anton.ehome.ssh.cmd.execption.CommandExecutionException;
import com.anton.ehome.utils.VisibleForTesting;
import com.anton.ehome.zwave.Device;
import com.anton.ehome.zwave.IZWaveController;
import com.anton.ehome.zwave.IZWaveManager;
import com.google.inject.Inject;

/**
 * Lists all the connected Z-Wave devices.
 */
@Command(group = "z-wave", name = "devices", description = "Lists all connected Z-Wave devices")
class ShowDevicesCommand implements ICommand
{
    private final IZWaveManager manager;

    @Option(name = "controller", description = "Filters devices by a controller")
    private String controllerName;

    @VisibleForTesting
    void setControllerName(String controllerName)
    {
        this.controllerName = controllerName;
    }

    @Inject
    ShowDevicesCommand(IZWaveManager manager)
    {
        this.manager = manager;
    }

    @Override
    public void execute(String user, ICommunicator communicator) throws IOException, CommandExecutionException
    {
        List<DeviceInfo> devices = manager.getControllers()
                .stream()
                .filter(controller -> isBlank(controllerName) || controller.getName().equals(controllerName))
                .flatMap(controller -> devices(controller))
                .sorted((left, right) -> new CompareToBuilder()
                        .append(left.controllerName, right.controllerName)
                        .append(left.device.getNodeId(), right.device.getNodeId())
                        .toComparison())
                .collect(toList());

        int lengthOfLongestDeviceType = devices.stream()
                .map(device -> device.device.getDeviceType())
                .map(String::length)
                .max(Integer::compareTo)
                .orElse(0);
        lengthOfLongestDeviceType = Integer.max(lengthOfLongestDeviceType, "DEVICE".length());

        communicator.newLine().write("NODE ID" + "   " + rightPad("DEVICE", lengthOfLongestDeviceType) + "   CONTROLLER");
        for (DeviceInfo device : devices)
        {
            communicator.newLine().write(rightPad(String.valueOf(device.device.getNodeId()), "NODE ID".length()) + "   "
                + rightPad(device.device.getDeviceType(), lengthOfLongestDeviceType) + "   " + device.controllerName);
        }
    }

    private Stream<DeviceInfo> devices(IZWaveController controller)
    {
        String name = controller.getName();
        return controller.getDevices()
                .stream()
                .map(device -> new DeviceInfo(name, device));
    }

    /**
     * Holds information about a {@link Device device}.
     */
    private static class DeviceInfo
    {
        private final String controllerName;
        private final Device device;

        private DeviceInfo(String controllerName, Device device)
        {
            this.controllerName = controllerName;
            this.device = device;
        }
    }
}
