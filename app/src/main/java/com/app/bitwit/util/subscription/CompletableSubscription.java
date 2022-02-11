package com.app.bitwit.util.subscription;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.DisposableContainer;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Supplier;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class CompletableSubscription implements Subscription {
    
    private final DisposableContainer disposableContainer;
    
    @Getter(AccessLevel.PACKAGE)
    private final Completable completable;
    
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
    
    public static CompletableSubscription empty( ) {
        return new CompletableSubscription(null, null);
    }
    
    public CompletableSubscription onComplete(Action onComplete) {
        this.onComplete = onComplete;
        return this;
    }
    
    public CompletableSubscription onError(Consumer<? super Throwable> onError) {
        this.onError = onError;
        return this;
    }
    
    public CompletableSubscription then(Supplier<CompletableSubscription> next) throws Throwable {
        if (completable == null) {
            return CompletableSubscription.empty( );
        }
        return new CompletableSubscription(
                completable.doOnComplete(onComplete)
                           .doOnError(onError)
                           .andThen(next.get( ).getCompletable( )),
                disposableContainer
        );
    }
    
    public <R> ObservableSubscription<R> thenObservable(Supplier<ObservableSubscription<R>> next) throws Throwable {
        if (completable == null) {
            return ObservableSubscription.empty( );
        }
        return new ObservableSubscription<>(
                completable.doOnComplete(onComplete)
                           .doOnError(onError)
                           .andThen(next.get( ).getObservable( )),
                disposableContainer
        );
    }
    
    public <R> SingleSubscription<R> thenSingle(Supplier<SingleSubscription<R>> next) throws Throwable {
        if (completable == null) {
            return SingleSubscription.empty( );
        }
        return new SingleSubscription<>(
                completable.doOnComplete(onComplete)
                           .doOnError(onError)
                           .andThen(next.get( ).getSingle( )),
                disposableContainer
        );
    }
    
    @Override
    public void subscribe( ) {
        if (completable == null) {
            return;
        }
        disposableContainer.add(
                completable
                        .subscribeOn(subscribingScheduler)
                        .observeOn(observingScheduler)
                        .subscribe(onComplete, onError)
        );
    }
}
