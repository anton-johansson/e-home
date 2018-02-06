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

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests of {@link DiffUtils}.
 */
public class DiffUtilsTest extends Assert
{
    private static final String LEFT = readFile("/diff-left.txt");
    private static final String RIGHT = readFile("/diff-right.txt");
    private static final String EXPECTED = readFile("/diff-expected.txt");

    @Test
    public void testWithoutDifferences()
    {
        assertFalse(DiffUtils.getDifference(LEFT, LEFT).isPresent());
    }

    @Test
    public void testWithDifferences()
    {
        String actual = DiffUtils.getDifference(LEFT, RIGHT).get();
        assertEquals(EXPECTED, actual);
    }

    private static String readFile(String name)
    {
        try (InputStream stream = DiffUtilsTest.class.getResourceAsStream(name))
        {
            return IOUtils.toString(stream, "UTF-8");
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
