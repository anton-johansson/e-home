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

import java.util.Set;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.ServletMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anton.ehome.common.IDaemon;
import com.anton.ehome.conf.Config;
import com.anton.ehome.conf.IConfigService;
import com.anton.ehome.http.common.IHttpApplication;
import com.google.inject.Inject;

/**
 * Hosts a web server as a {@link IDaemon daemon}.
 */
class HttpDaemon implements IDaemon
{
    private static final Logger LOG = LoggerFactory.getLogger(HttpDaemon.class);

    private final IConfigService configService;
    private final Set<IHttpApplication> applications;
    private Server server;

    @Inject
    HttpDaemon(IConfigService configService, Set<IHttpApplication> applications)
    {
        this.configService = configService;
        this.applications = applications;
    }

    @Override
    public boolean start()
    {
        ServletHandler handler = getHandler();

        LOG.info("Starting the HTTP daemon");
        Config config = configService.getCurrentConfig();
        int port = config.getHttpPort();
        server = new Server(port);
        server.setHandler(handler);
        try
        {
            server.start();
            LOG.info("Successfully started HTTP daemon");
            return true;
        }
        catch (Exception e)
        {
            LOG.error("Could not start HTTP daemon", e);
            return false;
        }
    }

    private ServletHandler getHandler()
    {
        ServletHandler handler = new ServletHandler();
        for (IHttpApplication application : applications)
        {
            ServletHolder holder = new ServletHolder();
            holder.setServlet(application);
            holder.setName(application.getPath());

            ServletMapping mapping = new ServletMapping();
            mapping.setPathSpec("/" + application.getPath() + "/*");
            mapping.setServletName(application.getPath());

            LOG.info("Adding servlet: {}", application.getPath());
            handler.addServlet(holder);
            handler.addServletMapping(mapping);
        }
        return handler;
    }

    @Override
    public void stop()
    {
        LOG.info("Stopping the HTTP daemon");
        if (server != null)
        {
            try
            {
                server.stop();
                LOG.info("Successfully stopped HTTP daemon");
            }
            catch (Exception e)
            {
                LOG.error("Could not stop HTTP daemon", e);
            }
            server = null;
        }
    }
}
