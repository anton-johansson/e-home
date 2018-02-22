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

/**
 * Defines scales for monitoring values.
 */
public enum Scale
{
    WATTS(com.whizzosoftware.wzwave.commandclass.MeterCommandClass.Scale.Watts);

    private final com.whizzosoftware.wzwave.commandclass.MeterCommandClass.Scale zWaveEquivalent;

    Scale(com.whizzosoftware.wzwave.commandclass.MeterCommandClass.Scale zWaveEquivalent)
    {
        this.zWaveEquivalent = zWaveEquivalent;
    }

    /**
     * Gets the Z-Wave equivalent enumeration value.
     */
    public com.whizzosoftware.wzwave.commandclass.MeterCommandClass.Scale getZWaveEquivalent()
    {
        return zWaveEquivalent;
    }
}
