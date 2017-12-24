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

import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anton.ehome.conf.Config;
import com.anton.ehome.conf.ConfigModule;
import com.anton.ehome.dao.DaoModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Contains the applications main entry-point.
 */
public class EntryPoint
{
    private static final Logger LOG = LoggerFactory.getLogger(EntryPoint.class);

    /**
     * The main entry-point.
     */
    public static void main(String[] args)
    {
        LOG.debug("Creating Guice injector");
        List<Module> modules = getModules();
        modules.forEach(module -> LOG.debug("Using module: {}", module.getClass().getName()));
        Injector injector = Guice.createInjector(modules);

        Config config = injector.getInstance(Config.class);

        Daemon daemon = new Daemon(config);
        daemon.start();

        LOG.info("Press [Enter] to stop the daemons");
        try (Scanner scanner = new Scanner(System.in))
        {
            scanner.nextLine();
        }
        daemon.stop();

        LOG.info("Exiting...");
    }

    private static List<Module> getModules()
    {
        return asList(
                new ConfigModule(),
                new DaoModule());
    }
}
