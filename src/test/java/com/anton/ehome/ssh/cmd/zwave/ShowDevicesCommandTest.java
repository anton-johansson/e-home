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
import static java.util.Collections.unmodifiableList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.anton.ehome.ssh.cmd.common.ICommunicator;
import com.anton.ehome.ssh.cmd.execption.CommandExecutionException;
import com.anton.ehome.zwave.Device;
import com.anton.ehome.zwave.IZWaveController;
import com.anton.ehome.zwave.IZWaveManager;

/**
 * Unit tests of {@link ShowDevicesCommand}.
 */
public class ShowDevicesCommandTest extends Assert
{
    private ShowDevicesCommand command;
    private @Mock IZWaveManager manager;
    private @Mock ICommunicator communicator;

    @Before
    public void setUp() throws IOException
    {
        MockitoAnnotations.initMocks(this);
        command = new ShowDevicesCommand(manager);
        when(communicator.newLine()).thenReturn(communicator);
        when(communicator.write(anyString())).thenReturn(communicator);
        when(manager.getControllers()).thenAnswer(invocation -> asList(controller("test", 3), controller("default", 2)));
    }

    private IZWaveController controller(String name, int numberOfDevices)
    {
        List<Device> devices = new ArrayList<>();
        for (byte i = 1; i <= numberOfDevices; i++)
        {
            Device device = new Device(i, "deviceType" + i);
            devices.add(device);
        }

        IZWaveController controller = mock(IZWaveController.class);
        when(controller.getName()).thenReturn(name);
        when(controller.getDevices()).thenReturn(unmodifiableList(devices));
        return controller;
    }

    @Test
    public void testExecutingCommand() throws IOException, CommandExecutionException
    {
        command.execute(null, communicator);

        InOrder inOrder = inOrder(communicator);
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("DEVICE        CONTROLLER");
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("deviceType1   default");
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("deviceType2   default");
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("deviceType1   test");
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("deviceType2   test");
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("deviceType3   test");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testExecutingCommandFilteredByController() throws IOException, CommandExecutionException
    {
        command.setControllerName("test");
        command.execute(null, communicator);

        InOrder inOrder = inOrder(communicator);
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("DEVICE        CONTROLLER");
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("deviceType1   test");
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("deviceType2   test");
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("deviceType3   test");
        inOrder.verifyNoMoreInteractions();
    }
}
