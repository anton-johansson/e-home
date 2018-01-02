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
package com.anton.ehome.ssh;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests of {@link SshUtils}.
 */
public class SshUtilsTest extends Assert
{
    @Test
    public void test_getLeftJumpPosition()
    {
        assertEquals(0, SshUtils.getLeftJumpPosition("", 0));
        assertEquals(0, SshUtils.getLeftJumpPosition("  ", 2));
        assertEquals(0, SshUtils.getLeftJumpPosition("  ", 1));
        assertEquals(0, SshUtils.getLeftJumpPosition("  ", 0));
        assertEquals(11, SshUtils.getLeftJumpPosition("My name is Anton", 16));
        assertEquals(11, SshUtils.getLeftJumpPosition("My name is Anton", 14));
        assertEquals(11, SshUtils.getLeftJumpPosition("My name is Anton", 12));
        assertEquals(8, SshUtils.getLeftJumpPosition("My name is Anton", 11));
        assertEquals(8, SshUtils.getLeftJumpPosition("My name is Anton", 10));
        assertEquals(8, SshUtils.getLeftJumpPosition("My name is Anton", 9));
        assertEquals(0, SshUtils.getLeftJumpPosition("My name is Anton", 3));
        assertEquals(0, SshUtils.getLeftJumpPosition("My name is Anton", 2));
        assertEquals(0, SshUtils.getLeftJumpPosition("My name is Anton", 1));
        assertEquals(0, SshUtils.getLeftJumpPosition("My name is Anton", 0));
        assertEquals(19, SshUtils.getLeftJumpPosition("  Spaces first and after  ", 26));
        assertEquals(19, SshUtils.getLeftJumpPosition("  Spaces first and after  ", 25));
        assertEquals(19, SshUtils.getLeftJumpPosition("  Spaces first and after  ", 24));
        assertEquals(19, SshUtils.getLeftJumpPosition("  Spaces first and after  ", 23));
        assertEquals(2, SshUtils.getLeftJumpPosition("  Spaces first and after  ", 9));
        assertEquals(2, SshUtils.getLeftJumpPosition("  Spaces first and after  ", 8));
        assertEquals(2, SshUtils.getLeftJumpPosition("  Spaces first and after  ", 7));
        assertEquals(2, SshUtils.getLeftJumpPosition("  Spaces first and after  ", 3));
        assertEquals(0, SshUtils.getLeftJumpPosition("  Spaces first and after  ", 2));
        assertEquals(0, SshUtils.getLeftJumpPosition("  Spaces first and after  ", 1));
        assertEquals(0, SshUtils.getLeftJumpPosition("  Spaces first and after  ", 0));
    }

    @Test
    public void test_getRightJumpPosition()
    {
        assertEquals(0, SshUtils.getRightJumpPosition("", 0));
        assertEquals(2, SshUtils.getRightJumpPosition("  ", 2));
        assertEquals(2, SshUtils.getRightJumpPosition("  ", 1));
        assertEquals(2, SshUtils.getRightJumpPosition("  ", 0));
        assertEquals(3, SshUtils.getRightJumpPosition("My name is Anton", 0));
        assertEquals(3, SshUtils.getRightJumpPosition("My name is Anton", 1));
        assertEquals(3, SshUtils.getRightJumpPosition("My name is Anton", 2));
        assertEquals(8, SshUtils.getRightJumpPosition("My name is Anton", 4));
        assertEquals(8, SshUtils.getRightJumpPosition("My name is Anton", 6));
        assertEquals(8, SshUtils.getRightJumpPosition("My name is Anton", 7));
        assertEquals(16, SshUtils.getRightJumpPosition("My name is Anton", 16));
        assertEquals(2, SshUtils.getRightJumpPosition("  Spaces first and after  ", 0));
        assertEquals(2, SshUtils.getRightJumpPosition("  Spaces first and after  ", 1));
        assertEquals(9, SshUtils.getRightJumpPosition("  Spaces first and after  ", 2));
        assertEquals(9, SshUtils.getRightJumpPosition("  Spaces first and after  ", 3));
        assertEquals(15, SshUtils.getRightJumpPosition("  Spaces first and after  ", 9));
        assertEquals(15, SshUtils.getRightJumpPosition("  Spaces first and after  ", 10));
        assertEquals(15, SshUtils.getRightJumpPosition("  Spaces first and after  ", 11));
        assertEquals(26, SshUtils.getRightJumpPosition("  Spaces first and after  ", 23));
        assertEquals(26, SshUtils.getRightJumpPosition("  Spaces first and after  ", 24));
        assertEquals(26, SshUtils.getRightJumpPosition("  Spaces first and after  ", 25));
        assertEquals(26, SshUtils.getRightJumpPosition("  Spaces first and after  ", 26));
    }
}
