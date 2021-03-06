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
package com.anton.ehome.conf;

import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import java.util.List;

/**
 * Holds configuration for the application.
 */
public class Config
{
    private static final int DEFAULT_HTTP_PORT = 8080;

    private int httpPort = DEFAULT_HTTP_PORT;
    private List<ZWaveConfig> zwaveConfigs = emptyList();
    private List<Chart> charts = emptyList();
    private NotificationConfig notificationConfig = new NotificationConfig();

    public int getHttpPort()
    {
        return httpPort;
    }

    public void setHttpPort(int httpPort)
    {
        this.httpPort = httpPort;
    }

    public List<ZWaveConfig> getZwaveConfigs()
    {
        return zwaveConfigs;
    }

    public void setZwaveConfigs(List<ZWaveConfig> zwaveConfigs)
    {
        this.zwaveConfigs = zwaveConfigs;
    }

    public List<Chart> getCharts()
    {
        return charts;
    }

    public void setCharts(List<Chart> charts)
    {
        this.charts = charts;
    }

    public NotificationConfig getNotificationConfig()
    {
        return notificationConfig;
    }

    public void setNotificationConfig(NotificationConfig notificationConfig)
    {
        this.notificationConfig = notificationConfig;
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
