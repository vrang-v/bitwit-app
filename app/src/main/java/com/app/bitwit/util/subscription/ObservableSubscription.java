package com.app.bitwit.util.subscription;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.DisposableContainer;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.Setter;

import java.util.concurrent.CompletableFuture;

public class ObservableSubscription<T> implements Subscription<Void> {
    
    private final DisposableContainer disposableContainer;
    
    private Observable<T> observable;
    
    @Setter
    private Scheduler subscribingScheduler = Schedulers.io( );
    @Setter
    private Scheduler observingScheduler   = AndroidSchedulers.mainThread( );
    
    private Consumer<T>                 onNext     = t -> { };
    private Consumer<? super Throwable> onError    = e -> { };
    private Action                      onComplete = ( ) -> { };
    
    public ObservableSubscription(Observable<T> observable, DisposableContainer disposableContainer) {
        this.observable          = observable;
        this.disposableContainer = disposableContainer;
    }
    
    public ObservableSubscription<T> onNext(Consumer<T> onSuccess) {
        this.onNext = onSuccess;
        return this;
    }
    
    @Override
    public ObservableSubscription<T> onError(Consumer<? super Throwable> onError) {
        this.onError = onError;
        return this;
    }
    
    @Override
    public Subscription<Void> onComplete(Action onComplete) {
        this.onComplete = onComplete;
        return this;
    }
    
    @Override
    public <R> Subscription<R> then(Function<Void, Subscription<R>> mapper) {
        CompletableFuture<Void> complete = new CompletableFuture<>( );
        try {
            observable = observable.doOnComplete(( ) -> complete.complete(null));
            subscribe( );
            return mapper.apply(complete.get( ));
        }
        catch (Throwable ignored) { }
        return null;
    }
    
    @Override
    public void subscribe( ) {
        disposableContainer.add(
                observable.subscribeOn(subscribingScheduler)
                          .observeOn(observingScheduler)
                          .subscribe(onNext, onError, onComplete)
        );
    }
}
