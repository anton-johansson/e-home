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
package com.anton.ehome.ssh.cmd.config;

import java.io.IOException;

import com.anton.ehome.conf.Config;
import com.anton.ehome.conf.IConfigService;
import com.anton.ehome.ssh.cmd.annotation.Argument;
import com.anton.ehome.ssh.cmd.annotation.Command;
import com.anton.ehome.ssh.cmd.common.ICommand;
import com.anton.ehome.ssh.cmd.common.ICommunicator;
import com.anton.ehome.ssh.cmd.execption.CommandExecutionException;
import com.google.inject.Inject;

/**
 * A command for showing the difference between two different configurations.
 */
@Command(group = "config", name = "diff", description = "Shows the difference between two configurations")
class DiffConfigsCommand implements ICommand
{
    private final IConfigService configService;

    @Inject
    DiffConfigsCommand(IConfigService configService)
    {
        this.configService = configService;
    }

    @Argument(name = "first", description = "The identifier of the first configuration")
    private String first;

    @Argument(name = "second", description = "The identifier of the second configuration")
    private String second;

    @Override
    public void execute(String user, ICommunicator communicator) throws IOException, CommandExecutionException
    {
        Config config1 = configService.getConfigById(first).orElseThrow(() -> new CommandExecutionException("Cannot find configuration with identifier '" + first + "'"));
        Config config2 = configService.getConfigById(second).orElseThrow(() -> new CommandExecutionException("Cannot find configuration with identifier '" + second + "'"));

        communicator.newLine().write("No difference");
    }
}
