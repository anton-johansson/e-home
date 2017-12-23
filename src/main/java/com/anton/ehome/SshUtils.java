package com.anton.ehome;

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
