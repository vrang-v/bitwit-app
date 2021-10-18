package com.app.bitwit.util;

import io.reactivex.rxjava3.functions.Consumer;
import lombok.Data;

@Data
public class Callback<T> {
    
    private Consumer<T> onSuccess = t -> { };
    
    private Consumer<Throwable> onError = e -> { };
    
    public static <T> Callback<T> callback() {
        Callback<T> callback = new Callback<>( );
        return callback;
    }
    
    public static <T> Callback<T> callback(Consumer<T> onSuccess, Consumer<Throwable> onError) {
        Callback<T> callback = new Callback<>( );
        if (onSuccess != null) {
            callback.onSuccess = onSuccess;
        }
        if (onError != null) {
            callback.onError = onError;
        }
        return callback;
    }
    
    public Callback<T> doOnSuccess(Consumer<T> onSuccess) {
        this.onSuccess = onSuccess;
        return this;
    }
    
    public Callback<T> doOnError(Consumer<Throwable> onError) {
        this.onError = onError;
        return this;
    }
}
