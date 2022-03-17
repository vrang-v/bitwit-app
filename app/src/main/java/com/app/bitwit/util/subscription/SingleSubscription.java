package com.app.bitwit.util.subscription;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.DisposableContainer;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class SingleSubscription<T> implements Subscription {
    
    private final DisposableContainer disposableContainer;
    
    @Getter(AccessLevel.PACKAGE)
    private final Single<T> single;
    
    @Setter
    private Scheduler subscribingScheduler = Schedulers.io( );
    @Setter
    private Scheduler observingScheduler   = AndroidSchedulers.mainThread( );
    
    private Consumer<T>                 onSuccess = t -> { };
    private Consumer<? super Throwable> onError   = e -> { };
    
    public SingleSubscription(Single<T> single, DisposableContainer disposableContainer) {
        this.single              = single == null ? null : single.observeOn(observingScheduler);
        this.disposableContainer = disposableContainer;
    }
    
    public static <T> SingleSubscription<T> empty( ) {
        return new SingleSubscription<>(null, null);
    }
    
    public SingleSubscription<T> onSuccess(Consumer<T> onSuccess) {
        this.onSuccess = onSuccess;
        return this;
    }
    
    public SingleSubscription<T> onError(Consumer<? super Throwable> onError) {
        this.onError = onError;
        return this;
    }
    
    public <R> SingleSubscription<R> then(Function<T, SingleSubscription<R>> mapper) {
        if (single == null) {
            return SingleSubscription.empty( );
        }
        return new SingleSubscription<>(
                single.doOnSuccess(onSuccess)
                      .doOnError(onError)
                      .flatMap(t -> mapper.apply(t).getSingle( )),
                disposableContainer
        );
    }
    
    public <R> ObservableSubscription<R> thenObservable(Function<T, ObservableSubscription<R>> mapper) {
        if (single == null) {
            return ObservableSubscription.empty( );
        }
        return new ObservableSubscription<>(
                single.doOnSuccess(onSuccess)
                      .doOnError(onError)
                      .flatMapObservable(t -> mapper.apply(t).getObservable( )),
                disposableContainer
        );
    }
    
    public CompletableSubscription thenCompletable(Function<T, CompletableSubscription> mapper) {
        if (single == null) {
            return CompletableSubscription.empty( );
        }
        return new CompletableSubscription(
                single.doOnSuccess(onSuccess)
                      .doOnError(onError)
                      .flatMapCompletable(t -> mapper.apply(t).getCompletable( )),
                disposableContainer
        );
    }
    
    @Override
    public void subscribe( ) {
        if (single == null) {
            return;
        }
        disposableContainer.add(
                single.subscribeOn(subscribingScheduler)
                      .subscribe(onSuccess, onError)
        );
    }
}
