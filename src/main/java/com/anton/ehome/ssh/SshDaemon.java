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
package com.anton.ehome.ssh;

import static java.util.Arrays.asList;

import java.io.IOException;

import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.UserAuthNoneFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hosts the SSH server.
 */
public class SshDaemon
{
    private static final Logger LOG = LoggerFactory.getLogger(SshDaemon.class);
    private static final int DEFAULT_PORT = 8022;

    private SshServer server;

    /**
     * Starts the SSH daemon.
     *
     * @return Returns whether or not this daemon successfully started.
     */
    public boolean start()
    {
        server = SshServer.setUpDefaultServer();
        server.setPort(DEFAULT_PORT);
        server.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
        server.setUserAuthFactories(asList(new UserAuthNoneFactory()));
        server.setShellFactory(() -> new EHomeShell());

        try
        {
            server.start();
            return true;
        }
        catch (IOException e)
        {
            LOG.error("Could not start SSH daemon", e);
            return false;
        }
    }

    /**
     * Stops the SSH daemon.
     */
    public void stop()
    {
        if (server != null)
        {
            try
            {
                server.stop();
            }
            catch (IOException e)
            {
                LOG.error("Error occurred while stopping the SSH daemon", e);
            }
            server = null;
        }
    }
}
