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
package com.anton.ehome.ssh.cmd.charts;

import static org.apache.commons.lang3.StringUtils.rightPad;

import java.io.IOException;
import java.util.List;

import com.anton.ehome.conf.Chart;
import com.anton.ehome.conf.IConfigService;
import com.anton.ehome.ssh.cmd.annotation.Command;
import com.anton.ehome.ssh.cmd.common.ICommand;
import com.anton.ehome.ssh.cmd.common.ICommunicator;
import com.anton.ehome.ssh.cmd.execption.CommandExecutionException;
import com.google.inject.Inject;

/**
 * Lists the configured charts.
 */
@Command(group = "charts", name = "list", description = "Lists the configured charts")
public class ListChartsCommand implements ICommand
{
    private final IConfigService configService;

    @Inject
    ListChartsCommand(IConfigService configService)
    {
        this.configService = configService;
    }

    @Override
    public void execute(String user, ICommunicator communicator) throws IOException, CommandExecutionException
    {
        List<Chart> charts = configService.getCurrentConfig().getCharts();

        int lengthOfLongestName = charts.stream()
                .map(Chart::getName)
                .map(String::length)
                .max(Integer::compareTo)
                .orElse(0);

        lengthOfLongestName = Integer.max(lengthOfLongestName, "NAME".length());

        communicator.newLine().write(rightPad("NAME", lengthOfLongestName) + "   TITLE");
        for (Chart device : charts)
        {
            communicator.newLine().write(rightPad(device.getName(), lengthOfLongestName) + "   " + device.getTitle());
        }
    }
}
