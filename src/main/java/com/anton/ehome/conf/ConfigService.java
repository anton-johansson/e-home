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

import static com.anton.ehome.utils.Assert.requireNonBlank;
import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

import com.anton.ehome.dao.IConfigDao;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Default implementation of {@link IConfigService}.
 */
class ConfigService implements IConfigService, Provider<Config>
{
    private final IConfigDao dao;
    private final Config config;

    @Inject
    ConfigService(IConfigDao dao)
    {
        this.dao = dao;
        this.config = dao.getCurrentConfig();
    }

    @Override
    public void modify(String reason, String user, Consumer<Config> consumer)
    {
        requireNonBlank(reason, "reason can't be blank");
        requireNonBlank(user, "user can't be blank");
        requireNonNull(consumer, "consumer can't be null");

        consumer.accept(config);
        dao.persist(reason, user, config);
    }

    @Override
    public Config getCurrentConfig()
    {
        return config;
    }

    @Override
    public Config get()
    {
        return config;
    }
}
