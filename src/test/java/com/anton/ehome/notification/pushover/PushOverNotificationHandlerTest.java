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
package com.anton.ehome.notification.pushover;

import static org.mockserver.model.HttpStatusCode.BAD_REQUEST_400;
import static org.mockserver.model.HttpStatusCode.OK_200;

import org.junit.Test;
import org.mockito.Mock;
import org.mockserver.model.Header;

import com.anton.ehome.common.AbstractWebTest;
import com.anton.ehome.conf.Config;
import com.anton.ehome.conf.IConfigService;

/**
 * Unit tests of {@link PushOverNotificationHandler}.
 */
public class PushOverNotificationHandlerTest extends AbstractWebTest
{
    private @Mock IConfigService configService;
    private PushOverNotificationHandler handler;

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        handler = new PushOverNotificationHandler(configService);
        handler.setEndpoint("http://localhost:" + port);
    }

    @Override
    protected void initMocks() throws Exception
    {
        Config config = new Config();
        config.getNotificationConfig().setToken1("api-key");
        config.getNotificationConfig().setToken2("user-key");
        when(configService.getCurrentConfig()).thenReturn(config);
        when(request().withHeader(new Header("Content-Type", "application/json")).withBody("{\"token\":\"api-key\",\"user\":\"user-key\",\"title\":\"E-Home\",\"message\":\"Hello world!\"}")).respond(response(OK_200));
        when(request().withHeader(new Header("Content-Type", "application/json")).withBody("{\"token\":\"api-key\",\"user\":\"user-key\",\"title\":\"E-Home\",\"message\":\"Illegal!\"}")).respond(response(BAD_REQUEST_400));
    }

    @Test
    public void testSuccessfullySendingNotification()
    {
        boolean result = handler.send("Hello world!");
        assertTrue(result);
    }

    @Test
    public void testSendingNotificationWithoutSuccess()
    {
        boolean result = handler.send("Illegal");
        assertFalse(result);
    }

    @Test
    public void testSendingNotificationWithoutAnswer()
    {
        handler.setEndpoint("http://localhost:" + (port + 1));
        boolean result = handler.send("Any");
        assertFalse(result);
    }
}
