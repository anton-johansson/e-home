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

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.IOException;

import com.anton.ehome.notification.INotificationService;
import com.anton.ehome.ssh.cmd.annotation.Argument;
import com.anton.ehome.ssh.cmd.annotation.Command;
import com.anton.ehome.ssh.cmd.common.ICommand;
import com.anton.ehome.ssh.cmd.common.ICommunicator;
import com.anton.ehome.ssh.cmd.execption.CommandExecutionException;
import com.anton.ehome.utils.VisibleForTesting;
import com.google.inject.Inject;

/**
 * Sends a notification.
 */
@Command(group = "notification", name = "send", description = "Sends a notification")
class SendNotificationCommand implements ICommand
{
    private final INotificationService notificationService;

    @Argument(name = "message", description = "The message to send")
    private String message;

    @VisibleForTesting
    void setMessage(String message)
    {
        this.message = message;
    }

    @Inject
    SendNotificationCommand(INotificationService notificationService)
    {
        this.notificationService = notificationService;
    }

    @Override
    public void execute(String user, ICommunicator communicator) throws IOException, CommandExecutionException
    {
        if (isBlank(message))
        {
            throw new CommandExecutionException("'message' can't be blank");
        }

        if (notificationService.send(message))
        {
            communicator.newLine().write("Notification sent!");
        }
        else
        {
            throw new CommandExecutionException("notification system is not set up");
        }
    }
}
