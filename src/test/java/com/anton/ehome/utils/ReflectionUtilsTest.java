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

import java.lang.reflect.Field;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests of {@link ReflectionUtils}.
 */
public class ReflectionUtilsTest extends Assert
{
    @Test
    public void testWritingField() throws Exception
    {
        Field field = ReflectionUtilsTestObject.class.getDeclaredField("value");
        ReflectionUtilsTestObject target = new ReflectionUtilsTestObject();

        assertEquals(0, target.value);
        ReflectionUtils.writeField(field, target, 123);
        assertEquals(123, target.value);
    }

    @Test(expected = RuntimeException.class)
    public void testWritingFieldForIncorrectTarget() throws Exception
    {
        Field field = ReflectionUtilsTestObject.class.getDeclaredField("value");
        ReflectionUtils.writeField(field, new Object(), 123);
    }

    /**
     * Class used for verifying the logics of {@link ReflectionUtils}.
     */
    private static class ReflectionUtilsTestObject
    {
        private int value;
    }
}
