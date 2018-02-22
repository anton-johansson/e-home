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

/**
 * Defines a value in the Z-Wave network that is being monitored.
 */
public class ZWaveMonitoringConfig
{
    private static final int DEFAULT_INTERVAL = 10;

    private byte nodeId;
    private Scale scale;
    private int interval = DEFAULT_INTERVAL;

    public byte getNodeId()
    {
        return nodeId;
    }

    public void setNodeId(byte nodeId)
    {
        this.nodeId = nodeId;
    }

    public Scale getScale()
    {
        return scale;
    }

    public void setScale(Scale scale)
    {
        this.scale = scale;
    }

    public int getInterval()
    {
        return interval;
    }

    public void setInterval(int interval)
    {
        this.interval = interval;
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
