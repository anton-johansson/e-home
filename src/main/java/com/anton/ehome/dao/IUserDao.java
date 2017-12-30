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

import java.util.List;

/**
 * Provides persistence operations for managing users.
 */
public interface IUserDao
{
    /**
     * The size of the command history, in memory.
     * <p>
     * More commands than this are actually stored in the database, and will be removed by it's expiration time, but for performance reasons, we only
     * keep this number of commands in memory.
     * </p>
     */
    int COMMAND_HISTORY_SIZE = 100;

    /**
     * Gets the command history for a given user.
     *
     * @param user
     * @return
     */
    List<String> getCommandHistory(String user);

    /**
     * Adds a command to the command history.
     *
     * @param user The user that executed the command.
     * @param command The command that was executed.
     */
    void addCommand(String user, String command);
}
