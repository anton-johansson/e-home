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

import java.io.IOException;

import com.anton.ehome.ssh.cmd.execption.CommandExecutionException;

/**
 * Defines an executable command.
 */
public interface ICommand
{
    /**
     * Executes the command.
     *
     * @param user The user that executes the command.
     * @param communicator Communicates with the client over SSH.
     */
    void execute(String user, ICommunicator communicator) throws IOException, CommandExecutionException;
}
