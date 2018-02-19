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

import com.anton.ehome.common.Uptime;
import com.anton.ehome.ssh.cmd.annotation.Command;
import com.anton.ehome.ssh.cmd.common.ICommand;
import com.anton.ehome.ssh.cmd.common.ICommunicator;
import com.anton.ehome.ssh.cmd.execption.CommandExecutionException;
import com.google.inject.Inject;

/**
 * Gets the servers current uptime.
 */
@Command(name = "uptime", description = "Gets the servers current uptime")
class UptimeCommand implements ICommand
{
    private static final int MILLISECONDS_PER_SECOND = 1000;
    private static final int SECONDS_PER_MINUTE = 60;
    private static final int MINUTES_PER_HOUR = 60;
    private static final int HOURS_PER_DAY = 24;
    private static final int DAYS_PER_YEAR = 365;

    private final Uptime uptime;

    @Inject
    UptimeCommand(Uptime uptime)
    {
        this.uptime = uptime;
    }

    @Override
    public void execute(String user, ICommunicator communicator) throws IOException, CommandExecutionException
    {
        long milliseconds = uptime.getUptimeInMilliseconds();
        long totalSeconds = milliseconds / MILLISECONDS_PER_SECOND;
        long totalMinutes = totalSeconds / SECONDS_PER_MINUTE;
        long totalHours = totalMinutes / MINUTES_PER_HOUR;
        long totalDays = totalHours / HOURS_PER_DAY;
        long years = totalDays / DAYS_PER_YEAR;

        long seconds = totalSeconds % SECONDS_PER_MINUTE;
        long minutes = totalMinutes % MINUTES_PER_HOUR;
        long hours = totalHours % HOURS_PER_DAY;
        long days = totalDays % DAYS_PER_YEAR;

        StringBuilder output = new StringBuilder("The server has been running for");
        appendYears(years, output);
        appendDays(totalDays, days, output);
        appendHours(totalHours, hours, output);
        appendMinutes(totalMinutes, minutes, output);
        appendSeconds(totalSeconds, seconds, output);
        communicator.newLine().write(output.toString());
    }

    private void appendSeconds(long totalSeconds, long seconds, StringBuilder output)
    {
        if (totalSeconds >= SECONDS_PER_MINUTE)
        {
            output.append(" and");
        }
        output.append(" ").append(seconds).append(" second");
        if (seconds != 1)
        {
            output.append("s");
        }
    }

    private void appendMinutes(long totalMinutes, long minutes, StringBuilder output)
    {
        if (totalMinutes > 0)
        {
            output.append(" ").append(minutes).append(" minute");
            if (minutes != 1)
            {
                output.append("s");
            }
        }
    }

    private void appendHours(long totalHours, long hours, StringBuilder output)
    {
        if (totalHours > 0)
        {
            output.append(" ").append(hours).append(" hour");
            if (hours != 1)
            {
                output.append("s");
            }
            output.append(",");
        }
    }

    private void appendDays(long totalDays, long days, StringBuilder output)
    {
        if (totalDays > 0)
        {
            output.append(" ").append(days).append(" day");
            if (days != 1)
            {
                output.append("s");
            }
            output.append(",");
        }
    }

    private void appendYears(long years, StringBuilder output)
    {
        if (years > 0)
        {
            output.append(" ").append(years).append(" year");
            if (years != 1)
            {
                output.append("s");
            }
            output.append(",");
        }
    }
}
