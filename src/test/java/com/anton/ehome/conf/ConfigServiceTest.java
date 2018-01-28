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
package com.anton.ehome.conf;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.anton.ehome.dao.IConfigDao;

/**
 * Unit tests of {@link ConfigService}.
 */
public class ConfigServiceTest extends Assert
{
    private ConfigService service;
    private @Mock IConfigDao dao;

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
        when(dao.getCurrentConfig()).thenReturn(new Config());
        service = new ConfigService(dao);
    }

    @Test
    public void testGettingConfig()
    {
        Config actual = service.get();
        Config expected = new Config();

        assertEquals(expected, actual);
    }

    @Test
    public void testModify()
    {
        service.modify("some reason", "some-user", config ->
        {
            ZWaveConfig zWaveConfig = new ZWaveConfig();
            zWaveConfig.setName("my-controller");
            config.setZwaveConfigs(asList(zWaveConfig));
        });

        ZWaveConfig zWaveConfig = new ZWaveConfig();
        zWaveConfig.setName("my-controller");

        Config config = new Config();
        config.setZwaveConfigs(asList(zWaveConfig));

        verify(dao).persist("some reason", "some-user", config);
    }
}
