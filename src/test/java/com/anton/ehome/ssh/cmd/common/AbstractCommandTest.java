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

import org.mockito.Mock;

import com.anton.ehome.common.AbstractTest;

/**
 * Abstract skeleton for command tests.
 *
 * @param <C> The type of the command.
 */
public abstract class AbstractCommandTest<C extends ICommand> extends AbstractTest
{
    protected @Mock ICommunicator communicator;
    protected C command;

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        command = create();
        when(communicator.newLine()).thenReturn(communicator);
        when(communicator.write(any())).thenReturn(communicator);
    }

    /**
     * Executes the command.
     */
    protected final void execute() throws Exception
    {
        command.execute("anton", communicator);
    }

    /**
     * Creates the command.
     */
    protected abstract C create();
}
