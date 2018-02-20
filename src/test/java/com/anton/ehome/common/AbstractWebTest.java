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
package com.anton.ehome.common;

import org.mockserver.client.server.ForwardChainExpectation;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.HttpStatusCode;

/**
 * Abstract skeleton for tests that requires a mocked web server.
 */
public abstract class AbstractWebTest extends AbstractTest
{
    protected final int port = 1337;
    private ClientAndServer server;

    @Override
    public void setUp() throws Exception
    {
        server = ClientAndServer.startClientAndServer(port);
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception
    {
        super.tearDown();
        server.stop();
    }

    /**
     * Mocks a web request.
     */
    protected final ForwardChainExpectation when(HttpRequest request)
    {
        return server.when(request);
    }

    /**
     * Starts building a request.
     */
    protected final HttpRequest request()
    {
        return HttpRequest.request();
    }

    /**
     * Starts building a response.
     */
    protected final HttpResponse response(HttpStatusCode statusCode)
    {
        return HttpResponse.response().withStatusCode(statusCode.code());
    }
}
