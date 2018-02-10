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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.anton.ehome.conf.Config;
import com.anton.ehome.conf.IConfigService;
import com.anton.ehome.conf.ZWaveConfig;
import com.anton.ehome.ssh.cmd.common.ICommunicator;
import com.anton.ehome.ssh.cmd.execption.CommandExecutionException;

/**
 * Unit tests of {@link ShowCurrentConfigCommand}.
 */
public class ShowCurrentConfigCommandTest extends Assert
{
    private ShowCurrentConfigCommand command;
    private @Mock IConfigService configService;
    private @Mock ICommunicator communicator;

    @Before
    public void setUp() throws IOException
    {
        MockitoAnnotations.initMocks(this);
        command = new ShowCurrentConfigCommand(configService);

        when(configService.getCurrentConfig()).thenReturn(config());
        when(communicator.newLine()).thenReturn(communicator);
        when(communicator.write(anyString())).thenReturn(communicator);
    }

    private Config config()
    {
        ZWaveConfig zWaveConfig = new ZWaveConfig();
        zWaveConfig.setName("default");
        zWaveConfig.setSerialPort("/dev/ttyACM0");

        Config config = new Config();
        config.setZwaveConfigs(asList(zWaveConfig));
        return config;
    }

    @Test
    public void testShowingCurrentCommand() throws IOException, CommandExecutionException
    {
        command.execute(null, communicator);

        InOrder inOrder = inOrder(configService, communicator);
        inOrder.verify(configService).getCurrentConfig();
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("{");
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("  \"httpPort\": 8080,");
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("  \"zwaveConfigs\": [");
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("    {");
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("      \"name\": \"default\",");
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("      \"serialPort\": \"/dev/ttyACM0\",");
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("      \"monitoringValues\": []");
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("    }");
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("  ],");
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("  \"charts\": []");
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("}");
        inOrder.verifyNoMoreInteractions();
    }
}
