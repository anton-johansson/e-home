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

import static com.anton.ehome.utils.JsonUtils.JSON_MAPPER;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.codec.digest.DigestUtils.sha1Hex;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBException;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import com.anton.ehome.conf.Config;
import com.anton.ehome.domain.ConfigHistory;
import com.google.inject.Inject;

/**
 * {@link InfluxDB} implementation of {@link IConfigDao}.
 */
class ConfigDao extends AbstractDao implements IConfigDao
{
    private static final String CURRENT_CONFIG_QUERY = "SELECT sha, data FROM config ORDER BY DESC LIMIT 1";
    private static final String HISTORY_QUERY = "SELECT sha, data, \"user\", reason FROM config ORDER BY DESC";

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
        return config;
    }

    @Override
    public void persist(String reason, String user, Config config)
    {
        String data = JSON_MAPPER.toJson(config);
        insert().measurement("config")
                .field("sha", time -> generateSha(time, data), true)
                .field("reason", reason)
                .field("user", user, true)
                .field("data", data)
                .execute();
    }

    @Override
    public List<ConfigHistory> getHistory()
    {
        return selectMany(HISTORY_QUERY, ConfigData.class)
                .stream()
                .map(data ->
                {
                    ConfigHistory history = new ConfigHistory();
                    history.setIdentifier(data.sha);
                    history.setCreatedAt(ZonedDateTime.ofInstant(data.time, ZoneOffset.UTC));
                    history.setUser(trimToEmpty(data.user));
                    history.setReason(trimToEmpty(data.reason));
                    return history;
                })
                .collect(toList());
    }

    @Override
    public Optional<Config> getConfigById(String identifier)
    {
        return Optional.empty();
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
        private @Column(name = "user") String user;
        private @Column(name = "reason") String reason;
    }
}
