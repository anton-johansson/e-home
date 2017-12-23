package com.anton.ehome.dao;

import org.influxdb.InfluxDB;

/**
 * Provides utilities for {@link InfluxDB}.
 */
final class InfluxUtils
{
    /** The name of the Influx database used for the E-Home server application. */
    static final String DATABASE_NAME = "e-home";

    // Prevent instantiation
    private InfluxUtils()
    {
    }
}
