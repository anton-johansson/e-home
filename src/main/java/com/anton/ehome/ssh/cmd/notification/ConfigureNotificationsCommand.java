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

import java.io.IOException;
import java.util.stream.Stream;

import com.anton.ehome.conf.IConfigService;
import com.anton.ehome.conf.NotificationConfig;
import com.anton.ehome.notification.NotificationProvider;
import com.anton.ehome.ssh.cmd.annotation.Argument;
import com.anton.ehome.ssh.cmd.annotation.Command;
import com.anton.ehome.ssh.cmd.annotation.Option;
import com.anton.ehome.ssh.cmd.common.ICommand;
import com.anton.ehome.ssh.cmd.common.ICommunicator;
import com.anton.ehome.ssh.cmd.execption.CommandExecutionException;
import com.google.inject.Inject;

/**
 * Configures the notification system.
 */
@Command(group = "notification", name = "configure", description = "Configures the notification system")
class ConfigureNotificationsCommand implements ICommand
{
    private final IConfigService configService;

    @Argument(name = "provider", description = "The provider to use")
    private String provider;

    @Option(name = "token", description = "The token that the provider needs")
    private String token;

    @Option(name = "token2", description = "The second token that the provider needs")
    private String token2;

    @Inject
    ConfigureNotificationsCommand(IConfigService configService)
    {
        this.configService = configService;
    }

    @Override
    public void execute(String user, ICommunicator communicator) throws IOException, CommandExecutionException
    {
        NotificationProvider provider = Stream.of(NotificationProvider.values())
                .filter(notificationProvider -> notificationProvider.name().equals(this.provider))
                .findAny()
                .orElseThrow(() -> new CommandExecutionException("unknown provider '" + this.provider + "'"));

        configService.modify("Configure notification system", user, config ->
        {
            NotificationConfig notificationConfig = new NotificationConfig();
            notificationConfig.setProvider(provider);
            notificationConfig.setToken1(token);
            notificationConfig.setToken2(token2);
            config.setNotificationConfig(notificationConfig);
        });

        communicator.newLine().write("Notification system was successfully set up");
    }
}
