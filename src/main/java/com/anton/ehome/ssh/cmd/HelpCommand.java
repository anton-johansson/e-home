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
package com.anton.ehome.ssh.cmd;

import static org.apache.commons.lang3.StringUtils.rightPad;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.anton.ehome.ssh.cmd.annotation.Command;
import com.anton.ehome.ssh.cmd.common.CommandMetaData;
import com.anton.ehome.ssh.cmd.common.ICommand;
import com.anton.ehome.ssh.cmd.common.ICommunicator;
import com.google.inject.Inject;

/**
 * A command for getting some help navigating the shell.
 */
@Command(name = "help", description = "Shows available commands")
class HelpCommand implements ICommand
{
    private final Map<String, CommandMetaData> commands;

    @Inject
    HelpCommand(Map<String, CommandMetaData> commands)
    {
        this.commands = commands;
    }

    @Override
    public void execute(String user, ICommunicator communicator) throws IOException
    {
        List<String> commandKeys = new ArrayList<>(commands.keySet());
        Collections.sort(commandKeys);
        int lengthOfLongestCommandKey = commandKeys.stream().max(Comparator.comparing(String::length)).orElse("").length();

        for (String commandKey : commandKeys)
        {
            CommandMetaData metaData = commands.get(commandKey);

            communicator
                    .newLine()
                    .write(rightPad(commandKey, lengthOfLongestCommandKey))
                    .write("   ")
                    .write(metaData.getDescription());
        }
    }
}
