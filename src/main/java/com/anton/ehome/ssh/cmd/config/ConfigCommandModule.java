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
package com.anton.ehome.ssh.cmd.config;

import static java.util.Arrays.asList;

import java.util.List;

import com.anton.ehome.ssh.cmd.common.AbstractCommandModule;
import com.anton.ehome.ssh.cmd.common.ICommand;

/**
 * Provides IOC bindings for the configuration commands.
 */
public class ConfigCommandModule extends AbstractCommandModule
{
    @Override
    protected List<Class<? extends ICommand>> getCommandClasses()
    {
        return asList(
                ConfigHistoryCommand.class,
                DiffConfigsCommand.class,
                ShowCurrentConfigCommand.class);
    }
}
