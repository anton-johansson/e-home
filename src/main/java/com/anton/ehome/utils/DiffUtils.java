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

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.difflib.algorithm.DiffException;
import com.github.difflib.patch.Delta;
import com.github.difflib.patch.Patch;

/**
 * Provides utilities for comparing data.
 */
public final class DiffUtils
{
    private static final Logger LOG = LoggerFactory.getLogger(DiffUtils.class);

    // Prevent instantiation
    private DiffUtils()
    {
    }

    /**
     * Gets a unified difference output from two different files.
     *
     * @param left The left data.
     * @param right The right data.
     * @return Returns the unidifed difference, if there is any difference.
     */
    public static Optional<String> getDifference(String left, String right)
    {
        Patch<String> patch = getPatch(left, right);
        if (patch == null)
        {
            return Optional.empty();
        }

        List<Delta<String>> deltas = patch.getDeltas();
        if (deltas.isEmpty())
        {
            return Optional.empty();
        }

        List<String> lines = asList(left.split("\\r?\\n"));
        int line = 0;
        StringBuilder output = new StringBuilder();

        for (Delta<String> delta : deltas)
        {
            int position = delta.getOriginal().getPosition();
            while (line < position)
            {
                output.append(" ").append(lines.get(line++)).append("\n");
            }
            for (String change : delta.getOriginal().getLines())
            {
                output.append("-").append(change).append("\n");
            }
            for (String change : delta.getRevised().getLines())
            {
                output.append("+").append(change).append("\n");
            }
            line += delta.getOriginal().size();
        }
        while (line < lines.size())
        {
            output.append(" ").append(lines.get(line++)).append("\n");
        }
        return Optional.of(output.toString());
    }

    private static Patch<String> getPatch(String left, String right)
    {
        try
        {
            return com.github.difflib.DiffUtils.diff(left, right);
        }
        catch (DiffException e)
        {
            LOG.warn("Could not get diff", e);
            return null;
        }
    }
}
