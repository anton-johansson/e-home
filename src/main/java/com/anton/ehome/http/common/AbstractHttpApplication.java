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
package com.anton.ehome.http.common;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.http.HttpStatus;

/**
 * Abstract skeleton for HTTP applications.
 */
public abstract class AbstractHttpApplication extends HttpServlet implements IHttpApplication
{
    private final String path;

    protected AbstractHttpApplication(String path)
    {
        // TODO: verify that path is [a-z-] (lower-case plus dash)
        this.path = path;
    }

    @Override
    public final String getPath()
    {
        return path;
    }

    /**
     * Gets the actual request path of the given request.
     *
     * @param request The request to get path from.
     * @return Returns the actual path.
     */
    protected final String getActualPath(HttpServletRequest request)
    {
        String path = "/" + this.path;
        String requestURI = request.getRequestURI();
        if (!requestURI.startsWith(path))
        {
            throw new IllegalStateException("This HTTP application does not handle paths like '" + requestURI + "'");
        }
        int startIndex = path.length();
        return requestURI.substring(startIndex);
    }

    /**
     * Performs a proxy for a given resource path.
     *
     * @param resourcePath The path of the resource to proxy.
     * @param response The response to send to.
     */
    protected final void proxyToResource(String resourcePath, HttpServletResponse response) throws IOException
    {
        try (InputStream stream = AbstractHttpApplication.class.getResourceAsStream(resourcePath))
        {
            if (stream == null)
            {
                response.sendError(HttpStatus.NOT_FOUND_404);
            }
            else
            {
                IOUtils.copy(stream, response.getOutputStream());
            }
        }
    }
}
