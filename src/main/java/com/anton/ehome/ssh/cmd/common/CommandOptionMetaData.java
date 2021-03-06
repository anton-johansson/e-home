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
package com.anton.ehome.ssh.cmd.common;

import java.lang.reflect.Field;
import java.util.function.Function;

import com.anton.ehome.ssh.cmd.annotation.Command;
import com.anton.ehome.ssh.cmd.annotation.Option;

/**
 * Provids meta data about a specific {@link Option option} of a {@link Command command}.
 */
public class CommandOptionMetaData
{
    private final Field field;
    private final String name;
    private final String description;
    private final boolean acceptsValue;
    private final String defaultValue;
    private final boolean multiple;
    private final Function<String, Object> converter;

    CommandOptionMetaData(Option option, Field field, boolean multiple, Function<String, Object> converter)
    {
        this.field = field;
        this.name = option.name();
        this.description = option.description();
        this.acceptsValue = !boolean.class.equals(field.getType());
        this.defaultValue = option.defaultValue();
        this.multiple = multiple;
        this.converter = converter;
    }

    public Field getField()
    {
        return field;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public boolean isAcceptsValue()
    {
        return acceptsValue;
    }

    public String getDefaultValue()
    {
        return defaultValue;
    }

    public boolean isMultiple()
    {
        return multiple;
    }

    public Function<String, Object> getConverter()
    {
        return converter;
    }

    public boolean isDefaultValueSpecified()
    {
        return !Option.UNSPECIFIED.equals(defaultValue);
    }
}
