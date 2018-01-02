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

import java.io.IOException;

import com.anton.ehome.ssh.cmd.annotation.Command;
import com.anton.ehome.ssh.cmd.common.ICommand;
import com.anton.ehome.ssh.cmd.common.ICommunicator;
import com.anton.ehome.ssh.cmd.execption.DisconnectException;

/**
 * A command that disconnects the client from the SSH server.
 */
@Command(name = "disconnect", description = "Disconnects from the SSH server session")
class DisconnectCommand implements ICommand
{
    @Override
    public void execute(ICommunicator communicator) throws IOException
    {
        communicator.newLine().write("Good-bye!").newLine();
        throw new DisconnectException();
    }
}
