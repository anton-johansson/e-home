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

import java.io.IOException;

import org.apache.sshd.server.ExitCallback;

/**
 * A command that disconnects the client from the SSH server.
 */
class DisconnectCommand implements ICommand, CanExit
{
    private ExitCallback exitCallback;

    @Override
    public void setExitCallback(ExitCallback exitCallback)
    {
        this.exitCallback = exitCallback;
    }

    @Override
    public void execute(ICommunicator communicator) throws IOException
    {
        communicator.newLine().write("Good-bye!").newLine();
        exitCallback.onExit(0);
    }
}
