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
package com.anton.ehome.ssh.cmd.common;

import java.io.IOException;

/**
 * Communicates with the client over SSH.
 */
public interface ICommunicator
{
    /**
     * Writes data to the client.
     *
     * @param output The data to write.
     * @return Returns the communicator itself, used for chaining.
     */
    ICommunicator write(String output) throws IOException;

    /**
     * Writes a newline to the client.
     *
     * @return Returns the communicator itself, used for chaining.
     */
    ICommunicator newLine() throws IOException;
}
