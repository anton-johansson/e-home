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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Provides various utilities for handling JSON-formatting.
 */
public class JsonUtils
{
    /** The standard JSON mapper. No pretty printing. */
    public static final Gson JSON_MAPPER = new GsonBuilder().create();
    private static final Gson PRETTY = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    /**
     * Writes the given object as prettified JSON.
     *
     * @param object The object to write.
     * @return Returns the prettified JSON.
     */
    public static String writePretty(Object object)
    {
        return PRETTY.toJson(object);
    }

    /**
     * Reads JSON into an object.
     *
     * @param json The JSON to read.
     * @param clazz The class of the object to read it into.
     * @return Returns the object.
     */
    public static <T> T read(String json, Class<T> clazz)
    {
        return JSON_MAPPER.fromJson(json, clazz);
    }
}
