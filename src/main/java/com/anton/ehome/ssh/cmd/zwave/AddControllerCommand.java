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

import com.anton.ehome.ssh.cmd.annotation.Argument;
import com.anton.ehome.ssh.cmd.annotation.Command;
import com.anton.ehome.ssh.cmd.annotation.Option;
import com.anton.ehome.ssh.cmd.common.ICommand;
import com.anton.ehome.ssh.cmd.common.ICommunicator;
import com.anton.ehome.ssh.cmd.execption.CommandExecutionException;
import com.anton.ehome.zwave.IZWaveManager;
import com.google.inject.Inject;

/**
 * A command for adding Z-Wave controllers to the network.
 */
@Command(group = "z-wave", name = "add-controller", description = "Adds a new controller to the Z-Wave network")
class AddControllerCommand implements ICommand
{
    private final IZWaveManager manager;

    @Option(name = "name", description = "The name of the controller", defaultValue = "default")
    private String name;

    @Argument(description = "The serial port to use")
    private String serialPort;

    @Inject
    AddControllerCommand(IZWaveManager manager)
    {
        this.manager = manager;
    }

    @Override
    public void execute(ICommunicator communicator) throws IOException, CommandExecutionException
    {
        if (isBlank(serialPort))
        {
            throw new CommandExecutionException("You must provide a serial port");
        }

        if (manager.getControllers().stream().anyMatch(controller -> controller.getName().equals(name)))
        {
            throw new CommandExecutionException("The name '" + name + "' is already used by another controller");
        }

        manager.addController(name, serialPort);
        communicator.newLine().write("Successfully added controller!");
    }
}
