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

import static com.anton.ehome.json.JsonUtils.JSON_MAPPER;

import java.time.Instant;

import org.influxdb.InfluxDB;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import com.anton.ehome.conf.Config;

/**
 * {@link InfluxDB} implementation of {@link IConfigDao}.
 */
class ConfigDao extends AbstractDao implements IConfigDao
{
    ConfigDao(InfluxDB influx)
    {
        super(influx);
    }

    @Override
    public Config getCurrentConfig()
    {
        ConfigData config = selectOne("SELECT data FROM config ORDER BY DESC LIMIT 1", ConfigData.class)
                .orElseGet(() -> createDefaultConfig());

        return JSON_MAPPER.fromJson(config.data, Config.class);
    }

    private ConfigData createDefaultConfig()
    {
        ConfigData config = new ConfigData();
        config.data = JSON_MAPPER.toJson(new Config());
        insert().measurement("config")
                .field("data", config.data)
                .execute();
        return config;
    }

    /**
     * Defines a configuration stored in the database.
     */
    @Measurement(name = "config")
    public static class ConfigData
    {
        private @Column(name = "time") Instant time;
        private @Column(name = "data") String data;
    }
}
