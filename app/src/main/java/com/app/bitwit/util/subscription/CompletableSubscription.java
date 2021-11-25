package com.app.bitwit.util.subscription;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.DisposableContainer;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.Setter;

import java.util.concurrent.CompletableFuture;

public class CompletableSubscription implements Subscription<Void> {
    
    private final DisposableContainer disposableContainer;
    
    private Completable completable;
    
    @Setter
    private Scheduler subscribingScheduler = Schedulers.io( );
    @Setter
    private Scheduler observingScheduler   = AndroidSchedulers.mainThread( );
    
    private Action                      onComplete = ( ) -> { };
    private Consumer<? super Throwable> onError    = e -> { };
    
    public CompletableSubscription(Completable completable, DisposableContainer disposableContainer) {
        this.completable         = completable;
        this.disposableContainer = disposableContainer;
    }
    
    @Override
    public Subscription<Void> onComplete(Action onComplete) {
        this.onComplete = onComplete;
        return this;
    }
    
    @Override
    public Subscription<Void> onError(Consumer<? super Throwable> onError) {
        this.onError = onError;
        return this;
    }
    
    @Override
    public <R> Subscription<R> then(Function<Void, Subscription<R>> mapper) {
        CompletableFuture<Void> complete = new CompletableFuture<>( );
        try {
            completable = completable.doOnComplete(( ) -> complete.complete(null));
            subscribe( );
            return mapper.apply(complete.get( ));
        }
        catch (Throwable ignored) { }
        return null;
    }
    
    @Override
    public void subscribe( ) {
        disposableContainer.add(
                completable
                        .subscribeOn(subscribingScheduler)
                        .observeOn(observingScheduler)
                        .subscribe(onComplete, onError)
        );
    }
}
