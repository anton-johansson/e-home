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

import java.io.IOException;

import com.anton.ehome.conf.IConfigService;
import com.anton.ehome.conf.ZWaveConfig;
import com.anton.ehome.ssh.cmd.annotation.Argument;
import com.anton.ehome.ssh.cmd.annotation.Command;
import com.anton.ehome.ssh.cmd.common.ICommand;
import com.anton.ehome.ssh.cmd.common.ICommunicator;
import com.anton.ehome.ssh.cmd.execption.CommandExecutionException;
import com.anton.ehome.utils.VisibleForTesting;
import com.anton.ehome.zwave.IZWaveManager;
import com.google.inject.Inject;

/**
 * A command for removing controllers from the Z-Wave network.
 */
@Command(group = "z-wave", name = "remove-controller", description = "Removes a controller from the Z-Wave network")
class RemoveControllerCommand implements ICommand
{
    private final IZWaveManager manager;
    private final IConfigService configService;

    @Argument(description = "The name of the controller to remove")
    private String name;

    @VisibleForTesting
    void setName(String name)
    {
        this.name = name;
    }

    @Inject
    RemoveControllerCommand(IZWaveManager manager, IConfigService configService)
    {
        this.manager = manager;
        this.configService = configService;
    }

    @Override
    public void execute(String user, ICommunicator communicator) throws IOException, CommandExecutionException
    {
        ZWaveConfig zWaveConfig = configService.getCurrentConfig()
                .getZwaveConfigs()
                .stream()
                .filter(config -> name.equals(config.getName()))
                .findAny()
                .orElseThrow(() -> new CommandExecutionException("No controller named '" + name + "' could be found"));

        manager.removeController(zWaveConfig.getName());
        configService.modify("Removed Z-Wave controller", user, config ->
        {
            config.getZwaveConfigs().remove(zWaveConfig);
        });

        communicator.newLine().write("Controller removed successfully");
    }
}
