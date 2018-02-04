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

import static org.apache.commons.lang3.StringUtils.abbreviate;
import static org.apache.commons.lang3.StringUtils.rightPad;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;

import com.anton.ehome.conf.IConfigService;
import com.anton.ehome.domain.ConfigHistory;
import com.anton.ehome.ssh.cmd.annotation.Command;
import com.anton.ehome.ssh.cmd.common.ICommand;
import com.anton.ehome.ssh.cmd.common.ICommunicator;
import com.anton.ehome.ssh.cmd.execption.CommandExecutionException;
import com.google.inject.Inject;

/**
 * Lists all changes for the configuration.
 */
@Command(group = "config", name = "history", description = "Shows the history of the configuration")
class ConfigHistoryCommand implements ICommand
{
    private static final int IDENTIFIER_LENGTH = 40;
    private static final int TIME_LENGTH = 20;
    private static final int USER_LENGTH = 10;
    private static final DateTimeFormatter TIME_FORMAT = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd HH:mm:ss")
            .appendLiteral('Z')
            .toFormatter();

    private final IConfigService configService;

    @Inject
    ConfigHistoryCommand(IConfigService configService)
    {
        this.configService = configService;
    }

    @Override
    public void execute(String user, ICommunicator communicator) throws IOException, CommandExecutionException
    {
        List<ConfigHistory> histories = configService.getHistory();

        communicator
                .newLine()
                .write(rightPad("IDENTIFIER", IDENTIFIER_LENGTH))
                .write("   ")
                .write(rightPad("TIME", TIME_LENGTH))
                .write("   ")
                .write(rightPad("USER", USER_LENGTH))
                .write("   ")
                .write("REASON");

        for (ConfigHistory history : histories)
        {
            communicator
                    .newLine()
                    .write(rightPad(history.getIdentifier(), IDENTIFIER_LENGTH))
                    .write("   ")
                    .write(TIME_FORMAT.format(history.getCreatedAt()))
                    .write("   ")
                    .write(rightPad(abbreviate(history.getUser(), USER_LENGTH), USER_LENGTH))
                    .write("   ")
                    .write(history.getReason());
        }
    }
}
