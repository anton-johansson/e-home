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
package com.anton.ehome.zwave;

import static com.google.inject.multibindings.Multibinder.newSetBinder;

import com.anton.ehome.common.IDaemon;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

/**
 * Contains IOC bindings for the Z-Wave module.
 */
public class ZWaveModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(ZWaveDaemon.class).in(Singleton.class);
        bind(IZWaveManager.class).to(ZWaveDaemon.class);
        newSetBinder(binder(), IDaemon.class).addBinding().to(ZWaveDaemon.class);
    }
}
