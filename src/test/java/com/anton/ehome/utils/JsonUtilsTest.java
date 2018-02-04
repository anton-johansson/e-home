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

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests of {@link JsonUtils}.
 */
public class JsonUtilsTest extends Assert
{
    @Test
    public void testWritingPretty()
    {
        TestData data = new TestData();
        data.setKey("some-value");

        String expected = "{\n  \"key\": \"some-value\"\n}";
        String actual = JsonUtils.writePretty(data);

        assertEquals(expected, actual);
    }

    /**
     * Object used for testing.
     */
    @SuppressWarnings("unused")
    private static class TestData
    {
        private String key = "";

        public String getKey()
        {
            return key;
        }

        public void setKey(String key)
        {
            this.key = key;
        }
    }
}
