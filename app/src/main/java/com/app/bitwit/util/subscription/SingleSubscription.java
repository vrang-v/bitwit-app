package com.app.bitwit.util.subscription;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.DisposableContainer;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.Setter;
import lombok.var;

import java.util.concurrent.CompletableFuture;

public class SingleSubscription<T> implements Subscription<T> {
    
    private final DisposableContainer disposableContainer;
    
    private Single<T> single;
    
    @Setter
    private Scheduler subscribingScheduler = Schedulers.io( );
    @Setter
    private Scheduler observingScheduler   = AndroidSchedulers.mainThread( );
    
    private Consumer<T>                 onSuccess = t -> { };
    private Consumer<? super Throwable> onError   = e -> { };
    
    public SingleSubscription(Single<T> single, DisposableContainer disposableContainer) {
        this.single              = single;
        this.disposableContainer = disposableContainer;
    }
    
    @Override
    public SingleSubscription<T> onSuccess(Consumer<T> onSuccess) {
        this.onSuccess = onSuccess;
        return this;
    }
    
    @Override
    public SingleSubscription<T> onError(Consumer<? super Throwable> onError) {
        this.onError = onError;
        return this;
    }
    
    @Override
    public <R> Subscription<R> then(Function<T, Subscription<R>> mapper) {
        var result = new CompletableFuture<T>( );
        try {
            single = single.doOnSuccess(result::complete);
            subscribe( );
            return mapper.apply(result.get( ));
        }
        catch (Throwable ignored) { }
        return null;
    }
    
    @Override
    public void subscribe( ) {
        disposableContainer.add(
                single.subscribeOn(subscribingScheduler)
                      .observeOn(observingScheduler)
                      .subscribe(onSuccess, onError)
        );
    }
}
