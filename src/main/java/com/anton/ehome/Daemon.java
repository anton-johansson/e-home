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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anton.ehome.conf.Config;
import com.anton.ehome.ssh.IDaemon;
import com.google.inject.Inject;

/**
 * Defines the daemon running all necessary software.
 */
class Daemon
{
    private static final Logger LOG = LoggerFactory.getLogger(Daemon.class);

    private final Config config;
    private final IDaemon ssh;

    @Inject
    Daemon(Config config, IDaemon ssh)
    {
        this.config = config;
        this.ssh = ssh;
    }

    /**
     * Starts the daemon.
     */
    void start()
    {
        LOG.info("Starting daemons...");
        if (ssh.start())
        {
            LOG.info("Daemons successfully started!");
        }
        else
        {
            LOG.warn("Could not start all daemons");
        }
    }

    /**
     * Stops the daemon.
     */
    void stop()
    {
        LOG.info("Stopping daemons...");
        ssh.stop();
        LOG.info("Daemons successfully stopped");
    }
}
