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

import static com.anton.ehome.utils.DiffUtils.getDifference;
import static java.util.Arrays.asList;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.anton.ehome.conf.Config;
import com.anton.ehome.conf.IConfigService;
import com.anton.ehome.ssh.cmd.annotation.Argument;
import com.anton.ehome.ssh.cmd.annotation.Command;
import com.anton.ehome.ssh.cmd.common.ICommand;
import com.anton.ehome.ssh.cmd.common.ICommunicator;
import com.anton.ehome.ssh.cmd.execption.CommandExecutionException;
import com.anton.ehome.utils.JsonUtils;
import com.anton.ehome.utils.VisibleForTesting;
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
    private String first = "";

    @Argument(name = "second", description = "The identifier of the second configuration")
    private String second = "";

    @VisibleForTesting
    void setFirst(String first)
    {
        this.first = first;
    }

    @VisibleForTesting
    void setSecond(String second)
    {
        this.second = second;
    }

    @Override
    public void execute(String user, ICommunicator communicator) throws IOException, CommandExecutionException
    {
        if (first.equals(second))
        {
            throw new CommandExecutionException("You can't check the same configuration");
        }

        Config config1 = configService.getConfigById(first).orElseThrow(() -> new CommandExecutionException("Cannot find configuration with identifier '" + first + "'"));
        Config config2 = configService.getConfigById(second).orElseThrow(() -> new CommandExecutionException("Cannot find configuration with identifier '" + second + "'"));

        String output1 = JsonUtils.writePretty(config1);
        String output2 = JsonUtils.writePretty(config2);

        Optional<String> optionalDiff = getDifference(output1, output2);
        if (optionalDiff.isPresent())
        {
            communicator.newLine();
            String diff = optionalDiff.get();
            List<String> lines = asList(diff.split("\\r?\\n"));
            for (String line : lines)
            {
                String prefix = getPrefix(line);
                String suffix = getSuffix(line);
                communicator.newLine().write(prefix + line + suffix);
            }
        }
        else
        {
            communicator.newLine().write("The configurations are identical");
        }
    }

    private String getPrefix(String line)
    {
        if (line.startsWith("+"))
        {
            return "\u001B[32m";
        }
        else if (line.startsWith("-"))
        {
            return "\u001B[31m";
        }
        else
        {
            return "";
        }
    }

    private String getSuffix(String line)
    {
        if (line.startsWith("+") || line.startsWith("-"))
        {
            return "\u001B[0m";
        }
        else
        {
            return "";
        }
    }
}
