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

import static com.google.inject.multibindings.MapBinder.newMapBinder;
import static java.util.stream.Collectors.toList;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.anton.ehome.ssh.cmd.annotation.Command;
import com.anton.ehome.ssh.cmd.annotation.Option;
import com.google.inject.AbstractModule;

/**
 * Contains IOC bindings for the SSH commands.
 */
public class CommandModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        command(DisconnectCommand.class);
        command(HelpCommand.class);
        command(VersionCommand.class);
    }

    private <C extends ICommand> void command(Class<C> commandClass)
    {
        Command command = Optional.ofNullable(commandClass.getAnnotation(Command.class))
                .orElseThrow(() -> new RuntimeException(commandClass.getSimpleName() + " is not annotated with @Command"));

        Supplier<ICommand> constructor = binder().getProvider(commandClass)::get;
        List<CommandOptionMetaData> options = Stream.of(commandClass.getDeclaredFields())
                .filter(field -> field.getAnnotation(Option.class) != null)
                .map(field -> getMetaDataForOption(field))
                .collect(toList());

        CommandMetaData metaData = new CommandMetaData(command, constructor, options);
        newMapBinder(binder(), String.class, CommandMetaData.class).addBinding(metaData.getCommandKey()).toInstance(metaData);
    }

    private CommandOptionMetaData getMetaDataForOption(Field field)
    {
        Class<?> type = field.getType();
        boolean multiple = isMultiple(type);
        Function<String, Object> converter = getConverter(field);
        Option option = field.getAnnotation(Option.class);
        return new CommandOptionMetaData(option, field, multiple, converter);
    }

    private boolean isMultiple(Class<?> type)
    {
        return List.class.equals(type);
    }

    private Function<String, Object> getConverter(Field field)
    {
        Option option = field.getAnnotation(Option.class);
        Class<?> type = getActualType(field);
        if (boolean.class.equals(type))
        {
            if (option.acceptsValue())
            {
                throw new RuntimeException("Boolean options cannot accept values");
            }
            if (!Option.UNSPECIFIED.equals(option.defaultValue()))
            {
                throw new RuntimeException("Boolean options cannot have default values");
            }
            return input -> "true".equals(input);
        }
        else
        {
            throw new RuntimeException("Unknown type (" + type + ") for option field: " + field);
        }
    }

    private Class<?> getActualType(Field field)
    {
        Class<?> type = field.getType();
        if (isMultiple(type))
        {
            ParameterizedType genericType = (ParameterizedType) field.getGenericType();
            type = (Class<?>) genericType.getActualTypeArguments()[0];
        }
        return type;
    }
}
