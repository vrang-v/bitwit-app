package com.app.bitwit.util.subscription;

import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;

@FunctionalInterface
public interface Subscription<T> {
    
    static <T> Subscription<T> empty( ) {
        return ( ) -> { };
    }
    
    void subscribe( );
    
    default Subscription<T> onSuccess(Consumer<T> onSuccess) {
        return this;
    }
    
    default Subscription<T> onError(Consumer<? super Throwable> onError) {
        return this;
    }
    
    default Subscription<T> onComplete(Action onComplete) {
        return this;
    }
    
    default <R> Subscription<R> then(Function<T, Subscription<R>> mapper) {
        try {
            return mapper.apply(null);
        }
        catch (Throwable ignored) { }
        return null;
    }
}
