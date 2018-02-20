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

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import com.anton.ehome.notification.NotificationProvider;

/**
 * Contains configuration for sending notifications.
 */
public class NotificationConfig
{
    private NotificationProvider provider;
    private String token1 = "";
    private String token2 = "";

    public NotificationProvider getProvider()
    {
        return provider;
    }

    public void setProvider(NotificationProvider provider)
    {
        this.provider = provider;
    }

    public String getToken1()
    {
        return token1;
    }

    public void setToken1(String token1)
    {
        this.token1 = token1;
    }

    public String getToken2()
    {
        return token2;
    }

    public void setToken2(String token2)
    {
        this.token2 = token2;
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
