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

import static com.anton.ehome.utils.JsonUtils.JSON_MAPPER;
import static org.apache.commons.codec.digest.DigestUtils.sha1Hex;

import java.time.Instant;
import java.util.Optional;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBException;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import com.anton.ehome.conf.Config;
import com.google.inject.Inject;

/**
 * {@link InfluxDB} implementation of {@link IConfigDao}.
 */
class ConfigDao extends AbstractDao implements IConfigDao
{
    private static final String CURRENT_CONFIG_QUERY = "SELECT sha, data FROM config ORDER BY DESC LIMIT 1";

    @Inject
    ConfigDao(InfluxDB influx)
    {
        super(influx);
    }

    @Override
    public Config getCurrentConfig()
    {
        ConfigData result = getOrCreateCurrentConfig();
        Config config = JSON_MAPPER.fromJson(result.data, Config.class);
        config.setIdentifier(result.sha);
        return config;
    }

    private ConfigData getOrCreateCurrentConfig()
    {
        Optional<ConfigData> result = selectOne(CURRENT_CONFIG_QUERY, ConfigData.class);
        if (result.isPresent())
        {
            return result.get();
        }
        else
        {
            createDefaultConfig();
            return selectOne(CURRENT_CONFIG_QUERY, ConfigData.class).orElseThrow(() -> new InfluxDBException("Could not create or find default configuration"));
        }
    }

    private void createDefaultConfig()
    {
        String data = JSON_MAPPER.toJson(new Config());
        insert().measurement("config")
                .field("sha", time -> generateSha(time, data), true)
                .field("data", data)
                .execute();
    }

    private String generateSha(long time, String data)
    {
        String value = time + ":" + data;
        return sha1Hex(value);
    }

    /**
     * Defines a configuration stored in the database.
     */
    @Measurement(name = "config")
    public static class ConfigData
    {
        private @Column(name = "time") Instant time;
        private @Column(name = "sha") String sha;
        private @Column(name = "data") String data;
    }
}
