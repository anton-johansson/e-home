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
package com.anton.ehome;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anton.ehome.common.IDaemon;
import com.google.inject.Inject;

/**
 * Defines the daemon running all necessary software.
 */
class Daemon
{
    private static final Logger LOG = LoggerFactory.getLogger(Daemon.class);

    private final Set<IDaemon> daemons;

    @Inject
    Daemon(Set<IDaemon> daemons)
    {
        this.daemons = daemons;
    }

    /**
     * Starts the daemons.
     */
    void start()
    {
        Set<IDaemon> startedDaemons = new HashSet<>();

        LOG.info("Starting daemons...");
        for (IDaemon daemon : daemons)
        {
            if (daemon.start())
            {
                LOG.info("Successfully started daemon: {}", daemon.getClass().getName());
                startedDaemons.add(daemon);
            }
            else
            {
                LOG.warn("Could not start daemon: {}", daemon.getClass().getName());
                startedDaemons.forEach(IDaemon::stop);
                return;
            }
        }
        LOG.info("All daemons started");
    }

    /**
     * Stops the daemons.
     */
    void stop()
    {
        LOG.info("Stopping daemons...");
        daemons.forEach(IDaemon::stop);
        LOG.info("All daemons successfully stopped");
    }
}
