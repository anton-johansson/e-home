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

import static java.time.ZoneOffset.UTC;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.ZonedDateTime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.stream.JsonWriter;

/**
 * Provides various utilities for handling JSON-formatting.
 */
public class JsonUtils
{
    /** The standard JSON mapper. No pretty printing. */
    public static final Gson JSON_MAPPER = new GsonBuilder()
            .registerTypeAdapter(Instant.class, new InstantSerializer())
            .create();

    private static final Gson PRETTY = new GsonBuilder()
            .registerTypeAdapter(Instant.class, new InstantSerializer())
            .setPrettyPrinting()
            .create();

    /**
     * Writes the given object as minified JSON to the given stream.
     *
     * @param object The object to write.
     * @param stream The output stream.
     */
    public static void write(Object object, OutputStream stream)
    {
        try (OutputStreamWriter out = new OutputStreamWriter(stream, "UTF-8"); JsonWriter writer = new JsonWriter(out))
        {
            JSON_MAPPER.toJson(object, object.getClass(), writer);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

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

    /**
     * Serializes {@link Instant instants} as ISO 8601 formatted strings.
     */
    private static class InstantSerializer implements JsonSerializer<Instant>
    {
        @Override
        public JsonElement serialize(Instant source, Type type, JsonSerializationContext context)
        {
            ZonedDateTime time = source.atZone(UTC);
            return new JsonPrimitive(time.toString());
        }
    }
}
