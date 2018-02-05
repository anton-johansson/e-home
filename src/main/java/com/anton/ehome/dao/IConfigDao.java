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

import java.util.List;
import java.util.Optional;

import com.anton.ehome.conf.Config;
import com.anton.ehome.domain.ConfigHistory;

/**
 * Provides persistence operations for managing the configuration.
 */
public interface IConfigDao
{
    /**
     * Gets the current configuration.
     *
     * @return Returns the current configuration stored in the database.
     */
    Config getCurrentConfig();

    /**
     * Persists the given configuration as the current one.
     *
     * @param reason The reason of the change.
     * @param user The user that made the change.
     * @param config The changed configuration.
     */
    void persist(String reason, String user, Config config);

    /**
     * Gets history.
     */
    List<ConfigHistory> getHistory();

    /**
     * Gets a configuration by its identifier.
     *
     * @param identifier The identifier of the configuration.
     * @return Returns the configuration, if it is found.
     */
    Optional<Config> getConfigById(String identifier);
}
