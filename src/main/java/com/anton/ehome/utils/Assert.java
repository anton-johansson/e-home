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
package com.anton.ehome.utils;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Provides ways of asserting statements.
 */
public final class Assert
{
    // Prevent instantiation
    private Assert()
    {
    }

    /**
     * Asserts that the given value isn't blank.
     *
     * @param value The value to assert.
     * @param name The name of the value.
     * @return Returns the given value.
     */
    public static String requireNonBlank(String value, String name)
    {
        if (isBlank(value))
        {
            throw new IllegalArgumentException(name + " can't be blank");
        }
        return value;
    }
}
