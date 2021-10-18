package com.app.bitwit.util;

import android.annotation.SuppressLint;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.Instant;

public class InstantSerializer implements JsonSerializer<Instant>, JsonDeserializer<Instant> {
    
    @Override
    public JsonElement serialize(Instant src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString( ));
    }
    
    @SuppressLint("NewApi")
    @Override
    public Instant deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
    throws JsonParseException {
        return Instant.parse(json.getAsString( ));
    }
    
}
