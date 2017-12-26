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

import org.apache.sshd.server.ExitCallback;

/**
 * Defines a command that can exit the shell.
 */
public interface CanExit
{
    /**
     * Sets the {@link ExitCallback exit callback}.
     *
     * @param exitCallback The exit callback.
     */
    void setExitCallback(ExitCallback exitCallback);
}
