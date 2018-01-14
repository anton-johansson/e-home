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
package com.anton.ehome.ssh.cmd;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

import java.io.IOException;

import org.apache.sshd.server.SshServer;

import com.anton.ehome.ssh.cmd.annotation.Command;
import com.anton.ehome.ssh.cmd.annotation.Option;
import com.anton.ehome.ssh.cmd.common.ICommand;
import com.anton.ehome.ssh.cmd.common.ICommunicator;

/**
 * A command that shows the current version of the E-Home server application.
 */
@Command(name = "version", description = "Prints the version of the E-Home server application")
class VersionCommand implements ICommand
{
    private static final String VERSION = defaultIfBlank(VersionCommand.class.getPackage().getImplementationVersion(), "Development");

    @Option(name = "verbose", description = "Whether or not to display version information about dependencies and used libraries")
    private boolean verbose;

    @Override
    public void execute(String user, ICommunicator communicator) throws IOException
    {
        if (verbose)
        {
            communicator
                    .newLine()
                    .write("E-Home version: " + VERSION)
                    .newLine()
                    .write("Apache mina-sshd version: " + SshServer.class.getPackage().getImplementationVersion());
        }
        else
        {
            communicator.newLine().write(VERSION);
        }
    }
}
