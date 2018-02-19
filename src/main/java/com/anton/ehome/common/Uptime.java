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

/**
 * Measures the uptime.
 */
public class Uptime
{
    private long start;

    /**
     * Indicates the start of the session.
     */
    public void markStart()
    {
        if (start > 0)
        {
            throw new IllegalStateException("The uptime has already been marked");
        }
        start = System.currentTimeMillis();
    }

    /**
     * Gets the current time in milliseconds.
     */
    public long getUptimeInMilliseconds()
    {
        return System.currentTimeMillis() - start;
    }
}
