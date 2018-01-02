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

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests of {@link StringUtils}.
 */
public class StringUtilsTest extends Assert
{
    @Test
    public void testGettingMutualStart()
    {
        assertEquals("", StringUtils.getMutualStart(emptyList(), 0));
        assertEquals("", StringUtils.getMutualStart(emptyList(), 5));
        assertEquals("disc", StringUtils.getMutualStart(asList("discard", "disciple", "disconnect", "discount", "discuss"), 0));
        assertEquals("sc", StringUtils.getMutualStart(asList("discard", "disciple", "disconnect", "discount", "discuss"), 2));
        assertEquals("", StringUtils.getMutualStart(asList("discard", "disciple", "disconnect", "discount", "discuss"), 4));
        assertEquals("", StringUtils.getMutualStart(asList("discard", "disciple", "disconnect", "discount", "discuss"), 5));
        assertEquals("", StringUtils.getMutualStart(asList("discard", "disciple", "disconnect", "discount", "discuss"), 1000));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGettingMutualStartWithNegativeStartIndex()
    {
        StringUtils.getMutualStart(emptyList(), -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGettingMutualStartWithNullItems()
    {
        StringUtils.getMutualStart(null, 0);
    }
}
