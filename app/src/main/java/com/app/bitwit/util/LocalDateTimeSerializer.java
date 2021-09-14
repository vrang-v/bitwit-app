package com.app.bitwit.util;

import android.annotation.SuppressLint;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import static java.time.temporal.ChronoField.MILLI_OF_SECOND;

public class LocalDateTimeSerializer implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
    
    @SuppressLint("NewApi")
    private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder( )
            .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
            .appendFraction(MILLI_OF_SECOND, 0, 9, true)
            .toFormatter( );
    
    @SuppressLint("NewApi")
    @Override
    public JsonElement serialize(LocalDateTime localDateTime, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(FORMATTER.format(localDateTime));
    }
    
    @SuppressLint("NewApi")
    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
    throws JsonParseException {
        return LocalDateTime.parse(json.getAsString( ), FORMATTER);
    }
}
