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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Provides various utilities for handling JSON-formatting.
 */
public class JsonUtils
{
    // CSOFF
    public static void main(String[] args)
    {
        System.out.println(0x21 & 0x1F); // type
        System.out.println((0x34 >> 3) & 0x03); // scale
        System.out.println((0x34 >> 5) & 0x07); // precision
        System.out.println(0x34 & 0x07); // size
    }
    // CSON

    /** The standard JSON mapper. No pretty printing. */
    public static final Gson JSON_MAPPER = new GsonBuilder().create();
}
