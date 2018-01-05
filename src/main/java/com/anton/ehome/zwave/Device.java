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
package com.anton.ehome.zwave;

/**
 * Defines a single Z-Wave device.
 */
public class Device
{
    private final byte nodeId;
    private final String deviceType;

    public Device(byte nodeId, String deviceType)
    {
        this.nodeId = nodeId;
        this.deviceType = deviceType;
    }

    public byte getNodeId()
    {
        return nodeId;
    }

    public String getDeviceType()
    {
        return deviceType;
    }
}
