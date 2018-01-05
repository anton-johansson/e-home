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
import java.util.function.Consumer;

/**
 * Defines a single Z-Wave controller.
 */
public interface IZWaveController
{
    /**
     * Gets the name of this configured controller.
     *
     * @return Returns the name.
     */
    String getName();

    /**
     * Gets the serial port that this controller communicates through.
     *
     * @return Returns the serial port.
     */
    String getSerialPort();

    /**
     * Gets the devices connected to this controller.
     *
     * @return Returns the conneted devices.
     */
    List<Device> getDevices();

    /**
     * Adds a listener for when devices are added to the controller.
     *
     * @param listener The listener to add.
     */
    void onDeviceAdded(Consumer<Device> listener);
}
