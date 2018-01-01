/**
 * Copyright 2018 Anton Johansson
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.anton.ehome.ssh.cmd.common.ICommunicator;
import com.anton.ehome.zwave.IZWaveController;
import com.anton.ehome.zwave.IZWaveManager;

/**
 * Unit tests of {@link ShowControllersCommand}.
 */
public class ShowControllersCommandTest extends Assert
{
    private ShowControllersCommand command;
    private @Mock IZWaveManager manager;
    private @Mock ICommunicator communicator;

    @Before
    public void setUp() throws IOException
    {
        MockitoAnnotations.initMocks(this);
        command = new ShowControllersCommand(manager);
        when(communicator.newLine()).thenReturn(communicator);
        when(communicator.write(anyString())).thenReturn(communicator);
        when(manager.getControllers()).thenAnswer(invocation -> asList(controller("default", "/dev/ttyACM0"), controller("test", "/dev/ttyUSB2")));
    }

    private IZWaveController controller(String name, String serialPort)
    {
        IZWaveController controller = mock(IZWaveController.class);
        when(controller.getName()).thenReturn(name);
        when(controller.getSerialPort()).thenReturn(serialPort);
        return controller;
    }

    @Test
    public void testExecutingCommand() throws IOException
    {
        command.execute(communicator);

        InOrder inOrder = inOrder(communicator);
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("NAME      SERIAL PORT");
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("default   /dev/ttyACM0");
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("test      /dev/ttyUSB2");
        inOrder.verifyNoMoreInteractions();
    }
}
