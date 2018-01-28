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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.Times;

import com.anton.ehome.conf.IConfigService;
import com.anton.ehome.ssh.cmd.common.ICommunicator;
import com.anton.ehome.ssh.cmd.execption.CommandExecutionException;
import com.anton.ehome.zwave.Device;
import com.anton.ehome.zwave.IZWaveController;
import com.anton.ehome.zwave.IZWaveManager;
import com.whizzosoftware.wzwave.commandclass.MeterCommandClass;
import com.whizzosoftware.wzwave.node.ZWaveNode;

/**
 * Unit tests of {@link AddMonitoredValueCommand}.
 */
public class AddMonitoredValueCommandTest extends Assert
{
    private AddMonitoredValueCommand command;
    private @Mock IZWaveManager manager;
    private @Mock IConfigService configService;
    private @Mock ICommunicator communicator;
    private IZWaveController controller1;

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
        command = new AddMonitoredValueCommand(manager, configService);

        controller1 = controller(1);
        List<IZWaveController> controllers = asList(controller1, controller(2));
        when(manager.getControllers()).thenReturn(controllers);
    }

    private IZWaveController controller(int id)
    {
        ZWaveNode node1 = mock(ZWaveNode.class);
        ZWaveNode node2 = mock(ZWaveNode.class);

        when(node2.hasCommandClass(MeterCommandClass.ID)).thenReturn(true);

        Device device1 = new Device((byte) 1, "deviceType1", node1);
        Device device2 = new Device((byte) 2, "deviceType2", node2);

        IZWaveController controller = mock(IZWaveController.class);
        when(controller.getName()).thenReturn("controller" + id);
        when(controller.getDevices()).thenReturn(asList(device1, device2));
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
    public void testSpecifyingDeviceThatCannotBeMonitored() throws IOException
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
            assertEquals("The given node cannot report monitoring values", e.getMessage());
        }
    }

    @Test
    public void testSuccessfullyMonitoringDevice() throws IOException, CommandExecutionException
    {
        command.setControllerName("controller1");
        command.setNodeId((byte) 2);
        command.execute("anton", communicator);

        verify(manager).getControllers();
        verify(controller1, new Times(2)).getName();
        verify(controller1).getDevices();
        verify(configService).modify(eq("Monitor device"), eq("anton"), any());
        verify(controller1).startMonitor((byte) 2);
        verify(communicator).write("Started monitoring device");
        verifyNoMoreInteractions(manager, controller1, configService);
    }

    @Test
    public void testSuccessfullyMonitoringDeviceWithoutSpecifyingController() throws IOException, CommandExecutionException
    {
        when(manager.getControllers()).thenReturn(asList(controller1));

        command.setNodeId((byte) 2);
        command.execute("anton", communicator);

        verify(manager).getControllers();
        verify(controller1).getName();
        verify(controller1).getDevices();
        verify(configService).modify(eq("Monitor device"), eq("anton"), any());
        verify(controller1).startMonitor((byte) 2);
        verify(communicator).write("Started monitoring device");
        verifyNoMoreInteractions(manager, controller1, configService);
    }

    @Test
    public void testSuccessfullyMonitoringDeviceWhenThereIsOneControllerButStillSpecifyingController() throws IOException, CommandExecutionException
    {
        when(manager.getControllers()).thenReturn(asList(controller1));

        command.setControllerName("controller1");
        command.setNodeId((byte) 2);
        command.execute("anton", communicator);

        verify(manager).getControllers();
        verify(controller1, new Times(2)).getName();
        verify(controller1).getDevices();
        verify(configService).modify(eq("Monitor device"), eq("anton"), any());
        verify(controller1).startMonitor((byte) 2);
        verify(communicator).write("Started monitoring device");
        verifyNoMoreInteractions(manager, controller1, configService);
    }
}
