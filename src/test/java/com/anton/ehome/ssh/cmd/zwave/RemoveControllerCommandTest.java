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
package com.anton.ehome.ssh.cmd.zwave;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;

import com.anton.ehome.conf.Config;
import com.anton.ehome.conf.IConfigService;
import com.anton.ehome.conf.ZWaveConfig;
import com.anton.ehome.ssh.cmd.common.ICommunicator;
import com.anton.ehome.ssh.cmd.execption.CommandExecutionException;
import com.anton.ehome.zwave.IZWaveManager;

/**
 * Unit tests of {@link RemoveControllerCommand}.
 */
public class RemoveControllerCommandTest extends Assert
{
    private final Config config = createConfig();

    private RemoveControllerCommand command;
    private @Mock IZWaveManager manager;
    private @Mock IConfigService configService;
    private @Mock ICommunicator communicator;

    @Before
    public void setUp() throws IOException
    {
        initMocks(this);
        command = new RemoveControllerCommand(manager, configService);

        when(configService.getCurrentConfig()).thenReturn(config);
        when(communicator.newLine()).thenReturn(communicator);
        when(communicator.write(anyString())).thenReturn(communicator);
    }

    private Config createConfig()
    {
        ZWaveConfig zWaveConfig = new ZWaveConfig();
        zWaveConfig.setName("test-controller");

        Config config = new Config();
        config.setZwaveConfigs(asList(zWaveConfig));
        return config;
    }

    @Test
    public void testExecutingCommand() throws IOException, CommandExecutionException
    {
        command.setName("test-controller");
        command.execute("anton", communicator);

        InOrder inOrder = inOrder(manager, configService, communicator);
        inOrder.verify(configService).getCurrentConfig();
        inOrder.verify(manager).removeController("test-controller");
        inOrder.verify(configService).modify(eq("Removed Z-Wave controller"), eq("anton"), any());
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("Controller removed successfully");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testExecutingCommandWithIncorrectName() throws IOException
    {
        command.setName("non-existing-controller");

        try
        {
            command.execute("anton", communicator);
            fail("Expected exception");
        }
        catch (CommandExecutionException e)
        {
            assertEquals("No controller named 'non-existing-controller' could be found", e.getMessage());
        }
    }
}
