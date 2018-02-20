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
package com.anton.ehome.notification;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anton.ehome.conf.Config;
import com.anton.ehome.conf.IConfigService;
import com.anton.ehome.notification.common.INotificationHandler;
import com.google.inject.Inject;

/**
 * Default implementation of {@link INotificationService}.
 */
class NotificationService implements INotificationService
{
    private static final Logger LOG = LoggerFactory.getLogger(NotificationService.class);

    private final IConfigService configService;
    private final Map<NotificationProvider, INotificationHandler> handlers;

    @Inject
    NotificationService(IConfigService configService, Map<NotificationProvider, INotificationHandler> handlers)
    {
        this.configService = configService;
        this.handlers = handlers;
    }

    @Override
    public boolean send(String message)
    {
        Config config = configService.getCurrentConfig();
        Optional<INotificationHandler> handler = Optional.of(config.getNotificationConfig())
                .map(notificationConfig -> notificationConfig.getProvider())
                .map(provider -> handlers.get(provider));

        if (handler.isPresent())
        {
            LOG.info("Sending message");
            return handler.get().send(message);
        }
        else
        {
            LOG.info("The notification system was not set up, so can't send notification");
            return false;
        }
    }
}
