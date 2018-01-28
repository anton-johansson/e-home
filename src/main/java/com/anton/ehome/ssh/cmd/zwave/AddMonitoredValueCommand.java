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

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anton.ehome.conf.IConfigService;
import com.anton.ehome.conf.ZWaveConfig;
import com.anton.ehome.conf.ZWaveMonitoringConfig;
import com.anton.ehome.ssh.cmd.annotation.Argument;
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
import com.whizzosoftware.wzwave.commandclass.MeterCommandClass;
import com.whizzosoftware.wzwave.node.ZWaveNode;

/**
 * A command for initiating a monitored value in the Z-Wave network.
 */
@Command(group = "z-wave", name = "add-monitored-value", description = "Starts monitoring a value in the Z-Wave network")
class AddMonitoredValueCommand implements ICommand
{
    private static final Logger LOG = LoggerFactory.getLogger(AddMonitoredValueCommand.class);

    private final IZWaveManager manager;
    private final IConfigService configService;

    @Option(name = "controller", description = "The name of the controller that the node belongs to (not required if there is only one controller)", defaultValue = "")
    private String controllerName;

    @Argument(description = "The identifier of the node within the given controller")
    private byte nodeId;

    @Inject
    AddMonitoredValueCommand(IZWaveManager manager, IConfigService configService)
    {
        this.manager = manager;
        this.configService = configService;
    }

    @VisibleForTesting
    void setControllerName(String controllerName)
    {
        this.controllerName = controllerName;
    }

    @VisibleForTesting
    void setNodeId(byte nodeId)
    {
        this.nodeId = nodeId;
    }

    @Override
    public void execute(String user, ICommunicator communicator) throws IOException, CommandExecutionException
    {
        IZWaveController controller = getController();
        LOG.debug("Using controller with name: {}", controller.getName());

        Device device = getDevice(controller);
        LOG.debug("Using device: {}", device.getDeviceType());

        ZWaveNode node = device.getNode();
        if (!node.hasCommandClass(MeterCommandClass.ID))
        {
            throw new CommandExecutionException("The given node cannot report monitoring values");
        }

        configService.modify("Monitor device", user, config ->
        {
            ZWaveConfig zWaveConfig = config.getZwaveConfigs()
                    .stream()
                    .filter(c -> c.getName().equals(controller.getName()))
                    .findAny()
                    .get();

            ZWaveMonitoringConfig monitoringValue = new ZWaveMonitoringConfig();
            monitoringValue.setNodeId(nodeId);

            zWaveConfig.getMonitoringValues().add(monitoringValue);
        });

        controller.startMonitor(nodeId);
        communicator.write("Started monitoring device");
    }

    private IZWaveController getController() throws CommandExecutionException
    {
        List<? extends IZWaveController> controllers = manager.getControllers();
        if (controllers.isEmpty())
        {
            throw new CommandExecutionException("There are no Z-Wave controllers configured");
        }
        if (controllers.size() > 1 && isBlank(controllerName))
        {
            throw new CommandExecutionException("There are more than one Z-Wave controller configured, please specify a controller");
        }
        if (controllers.size() == 1 && isBlank(controllerName))
        {
            return controllers.get(0);
        }
        return controllers.stream()
                .filter(controller -> controllerName.equals(controller.getName()))
                .findAny()
                .orElseThrow(() -> new CommandExecutionException("No controller with the given name was found"));
    }

    private Device getDevice(IZWaveController controller) throws CommandExecutionException
    {
        return controller.getDevices()
                .stream()
                .filter(device -> nodeId == device.getNodeId())
                .findAny()
                .orElseThrow(() -> new CommandExecutionException("No device with the given identifier was found"));
    }
}
