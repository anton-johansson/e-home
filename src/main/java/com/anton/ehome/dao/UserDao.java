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

import static com.anton.ehome.dao.InfluxUtils.RETENTION_POLICY_ONE_HOUR;
import static java.util.stream.Collectors.toList;

import java.util.List;

import org.influxdb.InfluxDB;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import com.google.inject.Inject;

/**
 * Default implementation of {@link IUserDao}.
 */
class UserDao extends AbstractDao implements IUserDao
{
    @Inject
    UserDao(InfluxDB influx)
    {
        super(influx);
    }

    @Override
    public List<String> getCommandHistory(String user)
    {
        return selectMany("SELECT command FROM \"command-history\" WHERE \"user\" = '" + user + "' ORDER BY time DESC LIMIT " + COMMAND_HISTORY_SIZE, CommandHistory.class)
                .stream()
                .map(history -> history.command)
                .collect(toList());
    }

    @Override
    public void addCommand(String user, String command)
    {
        insert().measurement("command-history")
                .retentionPolicy(RETENTION_POLICY_ONE_HOUR)
                .field("user", user, true)
                .field("command", command)
                .execute();
    }

    /**
     * Defines history of executed SSH commands stored in the database.
     */
    @Measurement(name = "command-history")
    public static class CommandHistory
    {
        private @Column(name = "command") String command;
    }
}
