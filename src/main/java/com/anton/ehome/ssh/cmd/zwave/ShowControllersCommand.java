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
package com.anton.ehome.ssh.cmd.zwave;

import static org.apache.commons.lang3.StringUtils.rightPad;

import java.io.IOException;
import java.util.List;

import com.anton.ehome.ssh.cmd.annotation.Command;
import com.anton.ehome.ssh.cmd.common.ICommand;
import com.anton.ehome.ssh.cmd.common.ICommunicator;
import com.anton.ehome.zwave.IZWaveController;
import com.anton.ehome.zwave.IZWaveManager;
import com.google.inject.Inject;

/**
 * Lists all the configured Z-Wave controllers.
 */
@Command(group = "z-wave", name = "controllers", description = "Lists all configured Z-Wave controllers")
class ShowControllersCommand implements ICommand
{
    private final IZWaveManager manager;

    @Inject
    ShowControllersCommand(IZWaveManager manager)
    {
        this.manager = manager;
    }

    @Override
    public void execute(String user, ICommunicator communicator) throws IOException
    {
        List<? extends IZWaveController> controllers = manager.getControllers();
        int lengthOfLongestName = controllers.stream()
                .map(IZWaveController::getName)
                .map(String::length)
                .max(Integer::compareTo)
                .orElse(0);

        communicator.newLine().write(rightPad("NAME", lengthOfLongestName) + "   SERIAL PORT");
        for (IZWaveController controller : controllers)
        {
            String name = controller.getName();
            String serialPort = controller.getSerialPort();
            communicator.newLine().write(rightPad(name, lengthOfLongestName) + "   " + serialPort);
        }
    }
}
