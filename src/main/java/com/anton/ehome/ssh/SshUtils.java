/**
 * Copyright 2018 Anton Johansson
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

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isWhitespace;

/**
 * Provides utilities for working with the SSH shell.
 */
final class SshUtils
{
    // Prevent instantiation.
    private SshUtils()
    {
    }

    /**
     * Gets the new cursor position after a left jump.
     *
     * @param text The current text.
     * @param cursorLocation The current cursor location.
     * @return The new cursor location.
     */
    static int getLeftJumpPosition(String text, int cursorLocation)
    {
        String leftHandSide = text.substring(0, cursorLocation);
        if (isBlank(leftHandSide))
        {
            return 0;
        }

        int index;
        boolean foundCharacter = false;
        for (index = cursorLocation; index >= 0; index--)
        {
            if (index == 0)
            {
                break;
            }

            char character = leftHandSide.charAt(index - 1);
            if (!foundCharacter && !isWhitespace(String.valueOf(character)))
            {
                foundCharacter = true;
            }
            else if (foundCharacter && isWhitespace(String.valueOf(character)))
            {
                break;
            }
        }
        return index;
    }

    /**
     * Gets the new cursor position after a right jump.
     *
     * @param text The current text.
     * @param cursorLocation The current cursor location.
     * @return The new cursor location.
     */
    static int getRightJumpPosition(String text, int cursorLocation)
    {
        String rightHandSide = text.substring(cursorLocation);
        if (isBlank(rightHandSide))
        {
            return text.length();
        }

        int index;
        boolean foundWhitespace = false;
        for (index = 0; index < rightHandSide.length(); index++)
        {
            char character = rightHandSide.charAt(index);
            if (!foundWhitespace && isWhitespace(String.valueOf(character)))
            {
                foundWhitespace = true;
            }
            else if (foundWhitespace && !isWhitespace(String.valueOf(character)))
            {
                break;
            }
        }
        return cursorLocation + index;
    }
}
