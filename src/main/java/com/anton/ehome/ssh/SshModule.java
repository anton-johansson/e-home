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
package com.anton.ehome.ssh;

import org.apache.sshd.server.Command;

import com.anton.ehome.ssh.cmd.CommandModule;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

/**
 * Contains IOC bindings for the SSH module.
 */
public class SshModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(IDaemon.class).to(SshDaemon.class).in(Singleton.class);
        bind(WelcomeTextProvider.class).in(Singleton.class);
        bind(Command.class).to(EHomeShell.class);

        install(new CommandModule());
    }
}
