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

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.anton.ehome.conf.IConfigService;
import com.anton.ehome.domain.ConfigHistory;
import com.anton.ehome.ssh.cmd.common.ICommunicator;
import com.anton.ehome.ssh.cmd.execption.CommandExecutionException;

/**
 * Unit tests of {@link ConfigHistoryCommand}.
 */
public class ConfigHistoryCommandTest extends Assert
{
    private ConfigHistoryCommand command;
    private @Mock IConfigService configService;
    private @Mock ICommunicator communicator;

    @Before
    public void setUp() throws IOException
    {
        MockitoAnnotations.initMocks(this);
        command = new ConfigHistoryCommand(configService);

        when(configService.getHistory()).thenReturn(history());
        when(communicator.newLine()).thenReturn(communicator);
        when(communicator.write(anyString())).thenReturn(communicator);
    }

    private List<ConfigHistory> history()
    {
        ConfigHistory history1 = new ConfigHistory();
        history1.setIdentifier("identifier1");
        history1.setCreatedAt(ZonedDateTime.of(2018, 2, 4, 21, 01, 41, 0, ZoneOffset.UTC));
        history1.setUser("anton");
        history1.setReason("Initial configuration");

        ConfigHistory history2 = new ConfigHistory();
        history2.setIdentifier("identifier2");
        history2.setCreatedAt(ZonedDateTime.of(2018, 2, 4, 21, 15, 13, 0, ZoneOffset.UTC));
        history2.setUser("someone-with-a-long-user-name");
        history2.setReason("Some change");

        return asList(history1, history2);
    }

    @Test
    public void testShowingHistory() throws IOException, CommandExecutionException
    {
        command.execute(null, communicator);

        InOrder inOrder = inOrder(configService, communicator);
        inOrder.verify(configService).getHistory();
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("IDENTIFIER                              ");
        inOrder.verify(communicator).write("   ");
        inOrder.verify(communicator).write("TIME                ");
        inOrder.verify(communicator).write("   ");
        inOrder.verify(communicator).write("USER      ");
        inOrder.verify(communicator).write("   ");
        inOrder.verify(communicator).write("REASON");
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("identifier1                             ");
        inOrder.verify(communicator).write("   ");
        inOrder.verify(communicator).write("2018-02-04 21:01:41Z");
        inOrder.verify(communicator).write("   ");
        inOrder.verify(communicator).write("anton     ");
        inOrder.verify(communicator).write("   ");
        inOrder.verify(communicator).write("Initial configuration");
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("identifier2                             ");
        inOrder.verify(communicator).write("   ");
        inOrder.verify(communicator).write("2018-02-04 21:15:13Z");
        inOrder.verify(communicator).write("   ");
        inOrder.verify(communicator).write("someone...");
        inOrder.verify(communicator).write("   ");
        inOrder.verify(communicator).write("Some change");
        inOrder.verifyNoMoreInteractions();
    }
}
