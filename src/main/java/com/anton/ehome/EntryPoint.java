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

import static java.util.Arrays.asList;

import java.io.IOException;
import java.util.Scanner;

import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.UserAuthNoneFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;

/**
 * Contains the applications main entry-point.
 */
public class EntryPoint
{
    private static final int DEFAULT_PORT = 8022;

    /**
     * The main entry-point.
     */
    public static void main(String[] args) throws IOException
    {
        SshServer ssh = SshServer.setUpDefaultServer();
        ssh.setPort(DEFAULT_PORT);
        ssh.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
        ssh.setUserAuthFactories(asList(new UserAuthNoneFactory()));
        ssh.setShellFactory(() -> new EHomeShell());
        ssh.start();

        System.out.println("Started!");
        try (Scanner scanner = new Scanner(System.in))
        {
            scanner.nextLine();
        }
        ssh.stop();
    }
}
