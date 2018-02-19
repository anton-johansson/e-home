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

import static org.mockito.Mockito.inOrder;

import java.io.IOException;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;

import com.anton.ehome.common.AbstractTest;
import com.anton.ehome.common.Uptime;
import com.anton.ehome.ssh.cmd.common.ICommunicator;
import com.anton.ehome.ssh.cmd.execption.CommandExecutionException;

/**
 * Unit tests of {@link UptimeCommand}.
 */
public class UptimeCommandTest extends AbstractTest
{
    private static final long ONE_YEAR = 31_536_000_000L;

    private UptimeCommand command;
    private @Mock ICommunicator communicator;
    private @Mock Uptime uptime;

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        command = new UptimeCommand(uptime);
    }

    @Override
    protected void initMocks() throws Exception
    {
        when(communicator.write(any())).thenReturn(communicator);
        when(communicator.newLine()).thenReturn(communicator);
    }

    @Test
    public void testVeryLongUptimeWhereAllUnitsAreSingular() throws IOException, CommandExecutionException
    {
        long uptimeInMilliseconds = ONE_YEAR
                + 1000 * 60 * 60 * 24
                + 1000 * 60 * 60
                + 1000 * 60
                + 1000;

        when(uptime.getUptimeInMilliseconds()).thenReturn(uptimeInMilliseconds);
        command.execute(null, communicator);

        InOrder inOrder = inOrder(communicator);
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("The server has been running for 1 year, 1 day, 1 hour, 1 minute and 1 second");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testVeryLongUptimeWhereAllUnitsArePlural() throws IOException, CommandExecutionException
    {
        long uptimeInMilliseconds = ONE_YEAR * 2
                + 1000 * 60 * 60 * 24 * 2
                + 1000 * 60 * 60 * 2
                + 1000 * 60 * 2
                + 1000 * 2;

        when(uptime.getUptimeInMilliseconds()).thenReturn(uptimeInMilliseconds);
        command.execute(null, communicator);

        InOrder inOrder = inOrder(communicator);
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("The server has been running for 2 years, 2 days, 2 hours, 2 minutes and 2 seconds");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testNoUptime() throws IOException, CommandExecutionException
    {
        when(uptime.getUptimeInMilliseconds()).thenReturn(0L);
        command.execute(null, communicator);

        InOrder inOrder = inOrder(communicator);
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("The server has been running for 0 seconds");
        inOrder.verifyNoMoreInteractions();
    }
}
