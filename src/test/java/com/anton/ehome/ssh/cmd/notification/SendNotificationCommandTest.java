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
package com.anton.ehome.ssh.cmd.notification;

import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.mockito.Mock;

import com.anton.ehome.notification.INotificationService;
import com.anton.ehome.ssh.cmd.common.AbstractCommandTest;
import com.anton.ehome.ssh.cmd.execption.CommandExecutionException;

/**
 * Unit tests of {@link SendNotificationCommand}.
 */
public class SendNotificationCommandTest extends AbstractCommandTest<SendNotificationCommand>
{
    private @Mock INotificationService notificationService;

    @Override
    protected void initMocks() throws Exception
    {
        when(notificationService.send("Successful message")).thenReturn(true);
    }

    @Override
    protected SendNotificationCommand create()
    {
        return new SendNotificationCommand(notificationService);
    }

    @Test
    public void testSuccessfullySendingMessage() throws Exception
    {
        command.setMessage("Successful message");
        execute();
        verify(notificationService).send("Successful message");
    }

    @Test
    public void testFailingWhenSendingMessage() throws Exception
    {
        command.setMessage("Incorrect message");
        try
        {
            execute();
        }
        catch (CommandExecutionException e)
        {
            assertEquals("notification system is not set up", e.getMessage());
        }
    }

    @Test
    public void testSendingBlankMessage() throws Exception
    {
        command.setMessage("");
        try
        {
            execute();
        }
        catch (CommandExecutionException e)
        {
            assertEquals("'message' can't be blank", e.getMessage());
        }
    }
}
