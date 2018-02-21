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
package com.anton.ehome.notification;

import static com.anton.ehome.notification.NotificationProvider.PUSHOVER;
import static com.google.inject.multibindings.MapBinder.newMapBinder;

import com.anton.ehome.notification.common.INotificationHandler;
import com.anton.ehome.notification.pushover.PushOverNotificationHandler;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.MapBinder;

/**
 * Contains IOC bindings for the notification module.
 */
public class NotificationModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(INotificationService.class).to(NotificationService.class).in(Singleton.class);

        MapBinder<NotificationProvider, INotificationHandler> mapbinder = newMapBinder(binder(), NotificationProvider.class, INotificationHandler.class);
        mapbinder.addBinding(PUSHOVER).to(PushOverNotificationHandler.class);
    }
}
