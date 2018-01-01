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

import static java.util.Arrays.asList;

import java.util.List;

import com.anton.ehome.ssh.cmd.common.AbstractCommandModule;
import com.anton.ehome.ssh.cmd.common.ICommand;
import com.anton.ehome.ssh.cmd.zwave.ZWaveCommandModule;

/**
 * Contains IOC bindings for the common SSH commands.
 */
public class CommandModule extends AbstractCommandModule
{
    @Override
    protected void configure()
    {
        super.configure();
        install(new ZWaveCommandModule());
    }

    @Override
    protected List<Class<? extends ICommand>> getCommandClasses()
    {
        return asList(
                DisconnectCommand.class,
                HelpCommand.class,
                VersionCommand.class);
    }
}
