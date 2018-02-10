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
package com.anton.ehome.ssh.cmd.charts;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;

import java.io.IOException;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;

import com.anton.ehome.common.AbstractTest;
import com.anton.ehome.conf.Chart;
import com.anton.ehome.conf.Config;
import com.anton.ehome.conf.IConfigService;
import com.anton.ehome.ssh.cmd.common.ICommunicator;
import com.anton.ehome.ssh.cmd.execption.CommandExecutionException;

/**
 * Unit tests of {@link ListChartsCommand}.
 */
public class ListChartsCommandTest extends AbstractTest
{
    private ListChartsCommand command;
    private @Mock IConfigService configService;
    private @Mock ICommunicator communicator;

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        command = new ListChartsCommand(configService);
    }

    @Override
    protected void initMocks() throws Exception
    {
        Chart chart1 = new Chart();
        chart1.setName("electrics");
        chart1.setTitle("Electric usage");

        Chart chart2 = new Chart();
        chart2.setName("tempratures");
        chart2.setTitle("Temprature readings");

        Config config = new Config();
        config.setCharts(asList(chart1, chart2));

        when(communicator.newLine()).thenReturn(communicator);
        when(communicator.write(anyString())).thenReturn(communicator);
        when(configService.getCurrentConfig()).thenReturn(config);
    }

    @Test
    public void testExecuting() throws IOException, CommandExecutionException
    {
        command.execute(null, communicator);

        InOrder inOrder = inOrder(communicator);
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("NAME          TITLE");
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("electrics     Electric usage");
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("tempratures   Temprature readings");
        inOrder.verifyNoMoreInteractions();
    }
}
