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

import static com.anton.ehome.notification.NotificationProvider.PUSHOVER;
import static org.mockito.Mockito.inOrder;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;

import com.anton.ehome.common.AbstractTest;
import com.anton.ehome.conf.Config;
import com.anton.ehome.conf.IConfigService;
import com.anton.ehome.notification.common.INotificationHandler;

/**
 * Unit tests of {@link NotificationService}.
 */
public class NotificationServiceTest extends AbstractTest
{
    private @Mock IConfigService configService;
    private @Mock INotificationHandler handler;
    private NotificationService service;

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        Map<NotificationProvider, INotificationHandler> handlers = new HashMap<>();
        handlers.put(PUSHOVER, handler);
        service = new NotificationService(configService, handlers);
    }

    @Override
    protected void initMocks() throws Exception
    {
        when(configService.getCurrentConfig()).thenReturn(config(PUSHOVER));
        when(handler.send("hello world")).thenReturn(true);
    }

    private Config config(NotificationProvider provider)
    {
        Config config = new Config();
        config.getNotificationConfig().setProvider(provider);
        return config;
    }

    @Test
    public void testSendingSuccessfully()
    {
        boolean result = service.send("hello world");
        assertTrue(result);

        InOrder inOrder = inOrder(configService, handler);
        inOrder.verify(configService).getCurrentConfig();
        inOrder.verify(handler).send("hello world");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testSendingWithUnknownProvider()
    {
        when(configService.getCurrentConfig()).thenReturn(config(null));

        boolean result = service.send("hello world");
        assertFalse(result);

        InOrder inOrder = inOrder(configService, handler);
        inOrder.verify(configService).getCurrentConfig();
        inOrder.verifyNoMoreInteractions();
    }
}
