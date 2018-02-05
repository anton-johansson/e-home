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

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Field;
import java.util.function.Function;

import com.anton.ehome.ssh.cmd.annotation.Argument;

/**
 * Defines meta data for a command argument.
 */
public class CommandArgumentMetaData
{
    private final Argument argument;
    private final Field field;
    private final Function<String, Object> argumentConverter;

    CommandArgumentMetaData(Argument argument, Field field, Function<String, Object> argumentConverter)
    {
        this.argument = requireNonNull(argument, "argument can't be null");
        this.field = requireNonNull(field, "field can't be null");
        this.argumentConverter = requireNonNull(argumentConverter, "argumentConverter can't be null");
    }

    public Argument getArgument()
    {
        return argument;
    }

    public Field getField()
    {
        return field;
    }

    public Function<String, Object> getArgumentConverter()
    {
        return argumentConverter;
    }
}
