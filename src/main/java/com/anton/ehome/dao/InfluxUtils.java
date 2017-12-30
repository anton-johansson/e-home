/**
 * Copyright 2018 Anton Johansson
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
package com.anton.ehome.dao;

import org.influxdb.InfluxDB;

/**
 * Provides utilities for {@link InfluxDB}.
 */
final class InfluxUtils
{
    /** The name of the Influx database used for the E-Home server application. */
    static final String DATABASE_NAME = "e-home";

    /** A retention policy that expire after one hour. */
    static final String RETENTION_POLICY_ONE_HOUR = "one-hour";

    // Prevent instantiation
    private InfluxUtils()
    {
    }

    /**
     * Creates required retention policies.
     *
     * @param influx The {@link InfluxDB} instance.
     */
    static void createRetentionPolicies(InfluxDB influx)
    {
        influx.createRetentionPolicy(RETENTION_POLICY_ONE_HOUR, DATABASE_NAME, "60m", 1, true);
    }
}
