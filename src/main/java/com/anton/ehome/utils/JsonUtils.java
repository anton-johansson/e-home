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
}
