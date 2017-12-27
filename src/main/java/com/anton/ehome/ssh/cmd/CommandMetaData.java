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
package com.anton.ehome.ssh.cmd;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.List;
import java.util.function.Supplier;

import com.anton.ehome.ssh.cmd.annotation.Command;

/**
 * Provides meta-data for a specific {@link Command command}.
 */
public class CommandMetaData
{
    private final String group;
    private final String name;
    private final String description;
    private final Supplier<ICommand> constructor;
    private final List<CommandOptionMetaData> options;

    CommandMetaData(Command command, Supplier<ICommand> constructor, List<CommandOptionMetaData> options)
    {
        this.options = options;
        this.group = command.group();
        this.name = command.name();
        this.description = command.description();
        this.constructor = constructor;
    }

    public String getGroup()
    {
        return group;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public Supplier<ICommand> getConstructor()
    {
        return constructor;
    }

    public List<CommandOptionMetaData> getOptions()
    {
        return options;
    }

    /**
     * Gets the full command key.
     */
    public String getCommandKey()
    {
        if (isBlank(group))
        {
            return name;
        }
        else
        {
            return group + ":" + name;
        }
    }
}
