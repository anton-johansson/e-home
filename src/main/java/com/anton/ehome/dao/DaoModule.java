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
package com.anton.ehome.dao;

import static com.anton.ehome.dao.InfluxUtils.DATABASE_NAME;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Pong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * Contains IOC bindings for the data access object module.
 */
public class DaoModule extends AbstractModule
{
    private static final Logger LOG = LoggerFactory.getLogger(DaoModule.class);

    @Override
    protected void configure()
    {
        bindDao(IConfigDao.class, ConfigDao.class);
        bindDao(IMetricsDao.class, MetricsDao.class);
        bindDao(IUserDao.class, UserDao.class);
    }

    private <Int, Impl extends Int> void bindDao(Class<Int> interFace, Class<Impl> implementation)
    {
        bind(interFace).to(implementation).in(Singleton.class);
    }

    /**
     * Provides the {@link InfluxDB} instance.
     */
    @Provides
    private InfluxDB influx()
    {
        InfluxDB influx = InfluxDBFactory.connect("http://127.0.0.1:8086", "root", "root");

        Pong pong = influx.ping();
        LOG.info("InfluxDB version: {}", pong.getVersion());

        LOG.debug("Making sure that database '{}' exists", DATABASE_NAME);
        influx.createDatabase(DATABASE_NAME);

        LOG.debug("Making sure retention policies exists");
        InfluxUtils.createRetentionPolicies(influx);

        return influx;
    }
}
