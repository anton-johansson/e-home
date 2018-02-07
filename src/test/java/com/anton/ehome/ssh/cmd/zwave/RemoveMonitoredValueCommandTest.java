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
import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.Times;

import com.anton.ehome.conf.IConfigService;
import com.anton.ehome.ssh.cmd.common.ICommunicator;
import com.anton.ehome.ssh.cmd.execption.CommandExecutionException;
import com.anton.ehome.zwave.Device;
import com.anton.ehome.zwave.IZWaveController;
import com.anton.ehome.zwave.IZWaveManager;

/**
 * Unit tests of {@link RemoveMonitoredValueCommand}.
 */
public class RemoveMonitoredValueCommandTest extends Assert
{
    private RemoveMonitoredValueCommand command;
    private @Mock IZWaveManager manager;
    private @Mock IConfigService configService;
    private @Mock ICommunicator communicator;
    private IZWaveController controller1;

    @Before
    public void setUp() throws IOException
    {
        MockitoAnnotations.initMocks(this);
        command = new RemoveMonitoredValueCommand(manager, configService);
        when(communicator.newLine()).thenReturn(communicator);
        when(communicator.write(anyString())).thenReturn(communicator);

        controller1 = controller(1);
        List<IZWaveController> controllers = asList(controller1, controller(2));
        when(manager.getControllers()).thenReturn(controllers);
    }

    private IZWaveController controller(int id)
    {
        Device device1 = new Device((byte) 1, "deviceType1", null);
        Device device2 = new Device((byte) 2, "deviceType2", null);

        IZWaveController controller = mock(IZWaveController.class);
        when(controller.getName()).thenReturn("controller" + id);
        when(controller.getDevices()).thenReturn(asList(device1, device2));
        if (id == 1)
        {
            when(controller.stopMonitor((byte) 2)).thenReturn(true);
        }
        return controller;
    }

    @Test
    public void testWhenThereAreNoControllers() throws IOException
    {
        when(manager.getControllers()).thenReturn(emptyList());

        try
        {
            command.execute(null, null);
            fail("Expected exception");
        }
        catch (CommandExecutionException e)
        {
            assertEquals("There are no Z-Wave controllers configured", e.getMessage());
        }
    }

    @Test
    public void testWithoutSpecifyingController() throws IOException
    {
        try
        {
            command.execute(null, null);
            fail("Expected exception");
        }
        catch (CommandExecutionException e)
        {
            assertEquals("There are more than one Z-Wave controller configured, please specify a controller", e.getMessage());
        }
    }

    @Test
    public void testSpecifyingInvalidController() throws IOException
    {
        command.setControllerName("non-existing-controller");

        try
        {
            command.execute(null, null);
            fail("Expected exception");
        }
        catch (CommandExecutionException e)
        {
            assertEquals("No controller with the given name was found", e.getMessage());
        }
    }

    @Test
    public void testWithoutSpecifyingDevice() throws IOException
    {
        command.setControllerName("controller1");

        try
        {
            command.execute(null, null);
            fail("Expected exception");
        }
        catch (CommandExecutionException e)
        {
            assertEquals("No device with the given identifier was found", e.getMessage());
        }
    }

    @Test
    public void testWithDeviceThatIsNotMonitored() throws IOException
    {
        command.setControllerName("controller1");
        command.setNodeId((byte) 1);

        try
        {
            command.execute(null, null);
            fail("Expected exception");
        }
        catch (CommandExecutionException e)
        {
            assertEquals("Device is not monitored", e.getMessage());
        }
    }

    @Test
    public void testSuccessfullyStopMonitoringDevice() throws IOException, CommandExecutionException
    {
        command.setControllerName("controller1");
        command.setNodeId((byte) 2);
        command.execute("anton", communicator);

        InOrder inOrder = inOrder(manager, controller1, configService, communicator);
        inOrder.verify(manager).getControllers();
        inOrder.verify(controller1, new Times(2)).getName();
        inOrder.verify(controller1).getDevices();
        inOrder.verify(configService).modify(eq("Stopped monitoring device"), eq("anton"), any());
        inOrder.verify(controller1).stopMonitor((byte) 2);
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("Stopped monitoring device");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testSuccessfullyStopMonitoringDeviceWithoutSpecifyingController() throws IOException, CommandExecutionException
    {
        when(manager.getControllers()).thenReturn(asList(controller1));

        command.setNodeId((byte) 2);
        command.execute("anton", communicator);

        InOrder inOrder = inOrder(manager, controller1, configService, communicator);
        inOrder.verify(manager).getControllers();
        inOrder.verify(controller1).getName();
        inOrder.verify(controller1).getDevices();
        inOrder.verify(configService).modify(eq("Stopped monitoring device"), eq("anton"), any());
        inOrder.verify(controller1).stopMonitor((byte) 2);
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("Stopped monitoring device");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testSuccessfullyMonitoringDeviceWhenThereIsOneControllerButStillSpecifyingController() throws IOException, CommandExecutionException
    {
        when(manager.getControllers()).thenReturn(asList(controller1));

        command.setControllerName("controller1");
        command.setNodeId((byte) 2);
        command.execute("anton", communicator);

        InOrder inOrder = inOrder(manager, controller1, configService, communicator);
        inOrder.verify(manager).getControllers();
        inOrder.verify(controller1, new Times(2)).getName();
        inOrder.verify(controller1).getDevices();
        inOrder.verify(configService).modify(eq("Stopped monitoring device"), eq("anton"), any());
        inOrder.verify(controller1).stopMonitor((byte) 2);
        inOrder.verify(communicator).newLine();
        inOrder.verify(communicator).write("Stopped monitoring device");
        inOrder.verifyNoMoreInteractions();
    }
}
