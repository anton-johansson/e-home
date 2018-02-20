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

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anton.ehome.conf.Config;
import com.anton.ehome.conf.IConfigService;
import com.anton.ehome.notification.common.INotificationHandler;
import com.anton.ehome.utils.JsonUtils;
import com.anton.ehome.utils.VisibleForTesting;
import com.google.inject.Inject;

/**
 * PushOver implementation of {@link INotificationHandler}.
 */
public class PushOverNotificationHandler implements INotificationHandler
{
    private static final Logger LOG = LoggerFactory.getLogger(PushOverNotificationHandler.class);
    private static final int TIMEOUT = 1000;

    private final IConfigService configService;
    private String endpoint = "https://api.pushover.net/1/messages.json";

    @VisibleForTesting
    void setEndpoint(String endpoint)
    {
        this.endpoint = endpoint;
    }

    @Inject
    PushOverNotificationHandler(IConfigService configService)
    {
        this.configService = configService;
    }

    @Override
    public boolean send(String message)
    {
        Config config = configService.getCurrentConfig();
        String token = config.getNotificationConfig().getToken1();
        String user = config.getNotificationConfig().getToken2();

        Message data = new Message();
        data.setToken(token);
        data.setUser(user);
        data.setTitle("E-Home");
        data.setMessage(message);

        try
        {
            HttpURLConnection connection = (HttpURLConnection) new URL(endpoint).openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(TIMEOUT);
            connection.setReadTimeout(TIMEOUT);
            connection.setDoOutput(true);

            JsonUtils.write(data, connection.getOutputStream());
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpStatus.SC_OK)
            {
                LOG.info("Successfully sent notification");
                return true;
            }
            else
            {
                LOG.warn("Received status code {} when sending notification", responseCode);
                return false;
            }
        }
        catch (IOException e)
        {
            LOG.error("Could not send notification", e);
            return false;
        }
    }

    /**
     * Defines a message being sent to PushOver.
     */
    static class Message
    {
        private String token = "";
        private String user = "";
        private String title = "";
        private String message = "";

        public String getToken()
        {
            return token;
        }

        public void setToken(String token)
        {
            this.token = token;
        }

        public String getUser()
        {
            return user;
        }

        public void setUser(String user)
        {
            this.user = user;
        }

        public String getTitle()
        {
            return title;
        }

        public void setTitle(String title)
        {
            this.title = title;
        }

        public String getMessage()
        {
            return message;
        }

        public void setMessage(String message)
        {
            this.message = message;
        }

        @Override
        public int hashCode()
        {
            return reflectionHashCode(this);
        }

        @Override
        public boolean equals(Object that)
        {
            return reflectionEquals(this, that);
        }

        @Override
        public String toString()
        {
            return reflectionToString(this, SHORT_PREFIX_STYLE);
        }
    }
}
