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
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.Times;

import com.anton.ehome.conf.Config;
import com.anton.ehome.conf.IConfigService;
import com.anton.ehome.conf.ZWaveConfig;
import com.anton.ehome.ssh.cmd.common.ICommunicator;
import com.anton.ehome.ssh.cmd.execption.CommandExecutionException;

/**
 * Unit tests of {@link DiffConfigsCommand}.
 */
public class DiffConfigsCommandTest extends Assert
{
    private DiffConfigsCommand command;
    private @Mock IConfigService configService;
    private @Mock ICommunicator communicator;

    @Before
    public void setUp() throws IOException
    {
        MockitoAnnotations.initMocks(this);
        command = new DiffConfigsCommand(configService);

        when(communicator.newLine()).thenReturn(communicator);
        when(communicator.write(anyString())).thenReturn(communicator);
        when(configService.getConfigById("config1")).thenReturn(Optional.of(config("config1")));
        when(configService.getConfigById("config2")).thenReturn(Optional.of(config("config2")));
        when(configService.getConfigById("same-as-config1")).thenReturn(Optional.of(config("config1")));
    }

    @Test
    public void testDiffingTheSameConfig() throws IOException
    {
        command.setFirst("config1");
        command.setSecond("config1");

        try
        {
            command.execute(null, communicator);
            fail("Expected exception");
        }
        catch (CommandExecutionException e)
        {
            assertEquals("You can't check the same configuration", e.getMessage());
        }
    }

    @Test
    public void testWhenFirstIsNotFound() throws IOException
    {
        command.setFirst("config666");
        command.setSecond("config2");

        try
        {
            command.execute(null, communicator);
            fail("Expected exception");
        }
        catch (CommandExecutionException e)
        {
            assertEquals("Cannot find configuration with identifier 'config666'", e.getMessage());
        }
    }

    @Test
    public void testWhenSecondIsNotFound() throws IOException
    {
        command.setFirst("config1");
        command.setSecond("config666");

        try
        {
            command.execute(null, communicator);
            fail("Expected exception");
        }
        catch (CommandExecutionException e)
        {
            assertEquals("Cannot find configuration with identifier 'config666'", e.getMessage());
        }
    }

    @Test
    public void testSuccessfulDiff() throws IOException, CommandExecutionException
    {
        command.setFirst("config1");
        command.setSecond("config2");
        command.execute(null, communicator);

        InOrder inOrder = inOrder(communicator, configService);
        inOrder.verify(configService).getConfigById("config1");
        inOrder.verify(configService).getConfigById("config2");
        inOrder.verify(communicator, new Times(2)).newLine();
        inOrder.verify(communicator).write(" {");
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("   \"httpPort\": 8080,");
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("   \"zwaveConfigs\": [");
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("     {");
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("\u001B[31m-      \"name\": \"config1\",\u001B[0m");
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("\u001B[32m+      \"name\": \"config2\",\u001B[0m");
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("       \"serialPort\": \"\",");
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("       \"monitoringValues\": []");
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("     }");
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("   ],");
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("   \"charts\": [],");
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("   \"notificationConfig\": {");
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("     \"token1\": \"\",");
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("     \"token2\": \"\"");
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("   }");
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write(" }");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testSuccessfulDiffButWithoutChanges() throws IOException, CommandExecutionException
    {
        command.setFirst("config1");
        command.setSecond("same-as-config1");
        command.execute(null, communicator);

        InOrder inOrder = inOrder(communicator, configService);
        inOrder.verify(configService).getConfigById("config1");
        inOrder.verify(configService).getConfigById("same-as-config1");
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("The configurations are identical");
        inOrder.verifyNoMoreInteractions();
    }

    private Config config(String zwaveName)
    {
        ZWaveConfig zwaveConfig = new ZWaveConfig();
        zwaveConfig.setName(zwaveName);

        Config config = new Config();
        config.setZwaveConfigs(asList(zwaveConfig));
        return config;
    }
}
