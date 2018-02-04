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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anton.ehome.conf.Config;
import com.anton.ehome.conf.IConfigService;
import com.anton.ehome.ssh.cmd.annotation.Command;
import com.anton.ehome.ssh.cmd.common.ICommand;
import com.anton.ehome.ssh.cmd.common.ICommunicator;
import com.anton.ehome.ssh.cmd.execption.CommandExecutionException;
import com.anton.ehome.utils.JsonUtils;
import com.google.inject.Inject;

/**
 * A command that shows the current configuration.
 */
@Command(group = "config", name = "show-current", description = "Shows thhe current configuration")
class ShowCurrentConfigCommand implements ICommand
{
    private static final Logger LOG = LoggerFactory.getLogger(ShowCurrentConfigCommand.class);

    private final IConfigService configService;

    @Inject
    ShowCurrentConfigCommand(IConfigService configService)
    {
        this.configService = configService;
    }

    @Override
    public void execute(String user, ICommunicator communicator) throws IOException, CommandExecutionException
    {
        Config config = configService.getCurrentConfig();
        String configData = JsonUtils.writePretty(config);
        LOG.debug(configData);

        String[] lines = configData.split("\n");
        for (String line : lines)
        {
            communicator.newLine().write(line);
        }
    }
}
