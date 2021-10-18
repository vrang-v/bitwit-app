package com.app.bitwit.data.source.storage;

import android.content.SharedPreferences;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import java.util.Optional;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class LocalStorage {
    
    private final Gson gson;
    
    private final SharedPreferences sharedPreferences;
    
    public LocalStorage save(String key, String value) {
        sharedPreferences.edit( ).putString(key, value).apply( );
        return this;
    }
    
    public <T> LocalStorage save(String key, T value) {
        String json = gson.toJson(value);
        sharedPreferences.edit( ).putString(key, json).apply( );
        return this;
    }
    
    public Optional<String> load(String key) {
        return Optional.ofNullable(sharedPreferences.getString(key, null));
    }
    
    public <T> Optional<T> load(String key, Class<T> type) {
        String json = sharedPreferences.getString(key, null);
        return json == null ? Optional.empty( ) : Optional.of(gson.fromJson(json, type));
    }
}
