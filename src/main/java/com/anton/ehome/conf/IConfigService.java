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

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.anton.ehome.domain.ConfigHistory;

/**
 * Provides utility for managing the configuration.
 */
public interface IConfigService
{
    /**
     * Modifies the configuration.
     *
     * @param reason The reason of the modification.
     * @param user The user that modified the configuration.
     * @param consumer Consumes the configuration before persisting it to the database.
     */
    void modify(String reason, String user, Consumer<Config> consumer);

    /**
     * Gets the current configuration.
     *
     * @return Returns the current configuration.
     */
    Config getCurrentConfig();

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
