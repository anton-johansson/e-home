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
package com.anton.ehome.utils;

import java.util.Collection;

/**
 * Provides utility methods for simplifying life when working with {@link String strings}.
 */
public final class StringUtils
{
    // Prevent instantiation
    private StringUtils()
    {
    }

    /**
     * Gets the mutual starts of a given collection of {@link CharSequence strings} and a given starting index.
     * <p>
     * <b>Example with <code>startIndex</code> 2 and the following <code>items</code>:</b>
     * </p>
     * 
     * <pre>
     * discard
     * disciple
     * disconnect
     * discount
     * discuss
     * </pre>
     * <p>
     * The above example would return <code>"disc"</code>.
     * </p>
     *
     * @param items The items to look for mutual starts in.
     * @param startIndex The index to start looking for mutual parts at.
     * @return Returns the mutual start; or an empty {@link String string} if no mutual start are found.
     */
    public static String getMutualStart(Collection<? extends CharSequence> items, int startIndex)
    {
        if (items == null)
        {
            throw new IllegalArgumentException("items can't be null");
        }

        if (startIndex < 0)
        {
            throw new IllegalArgumentException("startIndex must be greater than or equal to zero");
        }

        if (items.isEmpty())
        {
            return "";
        }

        StringBuilder mutualStart = new StringBuilder();
        int index = startIndex;
        boolean endReached = false;

        while (!endReached)
        {
            char mutualCharacter = 0;
            for (CharSequence item : items)
            {
                if (item.length() <= index)
                {
                    endReached = true;
                    break;
                }

                if (mutualCharacter == 0)
                {
                    mutualCharacter = item.charAt(index);
                }
                else if (mutualCharacter != item.charAt(index))
                {
                    endReached = true;
                    break;
                }
            }

            if (!endReached)
            {
                mutualStart.append(mutualCharacter);
                index++;
            }
        }

        return mutualStart.toString();
    }
}
