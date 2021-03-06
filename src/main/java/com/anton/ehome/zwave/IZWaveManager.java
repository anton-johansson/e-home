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

import java.util.List;

/**
 * Manages the configured Z-Wave controllers.
 */
public interface IZWaveManager
{
    /**
     * Gets all configured {@link IZWaveController controllers}.
     *
     * @return Returns the controllers.
     */
    List<IZWaveController> getControllers();

    /**
     * Adds a controller to the list of {@link IZWaveController controllers}.
     *
     * @param name The name of the controller.
     * @param serialPort The serial port to use.
     */
    void addController(String name, String serialPort);

    /**
     * Removes a controller from the list of {@link IZWaveController controllers}.
     *
     * @param name The name of the controller to remove.
     */
    void removeController(String name);
}
