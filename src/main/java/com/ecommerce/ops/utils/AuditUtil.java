package com.ecommerce.ops.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDateTime;

public class AuditUtil {

    private static final Gson gson = new GsonBuilder()
            .serializeNulls()      // include null fields
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .setPrettyPrinting()   // pretty print JSON
            .create();

    public static String toJson(Object obj) {
        if (obj == null) return "{}";
        return gson.toJson(obj);
    }
}
