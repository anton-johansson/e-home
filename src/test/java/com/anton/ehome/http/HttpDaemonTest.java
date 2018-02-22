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
package com.anton.ehome.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.mockito.Mock;

import com.anton.ehome.common.AbstractTest;
import com.anton.ehome.conf.Config;
import com.anton.ehome.conf.IConfigService;
import com.anton.ehome.http.common.IHttpApplication;

/**
 * Unit tests of {@link HttpDaemon}.
 */
public class HttpDaemonTest extends AbstractTest
{
    private @Mock IConfigService configService;
    private HttpDaemon daemon;

    @Override
    public void setUp() throws Exception
    {
        super.setUp();

        IHttpApplication application = new MockApplication();

        Set<IHttpApplication> applications = new HashSet<>();
        applications.add(application);
        daemon = new HttpDaemon(configService, applications);
        boolean success = daemon.start();
        assertTrue("Server did not start", success);
    }

    @Override
    public void tearDown()
    {
        daemon.stop();
    }

    @Override
    protected void initMocks()
    {
        Config config = new Config();
        config.setHttpPort(1337);

        when(configService.getCurrentConfig()).thenReturn(config);
    }

    @Test
    public void testSuccessfulCall() throws Exception
    {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:1337/test").openConnection();
        int responseCode = connection.getResponseCode();
        String body = IOUtils.toString(connection.getInputStream(), "UTF-8");

        assertEquals(200, responseCode);
        assertEquals("Hello world", body);
    }

    @Test
    public void testNotFound() throws Exception
    {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:1337/unknown").openConnection();
        int responseCode = connection.getResponseCode();

        assertEquals(404, responseCode);
    }

    /**
     * Defines a servlet used for tests.
     */
    private static class MockApplication extends HttpServlet implements IHttpApplication
    {
        @Override
        public String getPath()
        {
            return "test";
        }

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
        {
            ServletOutputStream stream = resp.getOutputStream();
            IOUtils.write("Hello world", stream, "UTF-8");
        }
    }
}
