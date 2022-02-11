package com.app.bitwit.util.subscription;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.DisposableContainer;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class ObservableSubscription<T> implements Subscription {
    
    private final DisposableContainer disposableContainer;
    
    @Getter(AccessLevel.PACKAGE)
    private final Observable<T> observable;
    
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
    
    public static <T> ObservableSubscription<T> empty( ) {
        return new ObservableSubscription<>(null, null);
    }
    
    public ObservableSubscription<T> onNext(Consumer<T> onSuccess) {
        this.onNext = onSuccess;
        return this;
    }
    
    public ObservableSubscription<T> onError(Consumer<? super Throwable> onError) {
        this.onError = onError;
        return this;
    }
    
    public ObservableSubscription<T> onComplete(Action onComplete) {
        this.onComplete = onComplete;
        return this;
    }
    
    public <R> ObservableSubscription<R> thenObservable(Function<T, ObservableSubscription<R>> mapper) {
        if (observable == null) {
            return ObservableSubscription.empty( );
        }
        return new ObservableSubscription<>(
                observable.doOnNext(onNext)
                          .doOnComplete(onComplete)
                          .doOnError(onError)
                          .flatMap(t -> mapper.apply(t).getObservable( )),
                disposableContainer
        );
    }
    
    public <R> ObservableSubscription<R> thenSingle(Function<T, SingleSubscription<R>> mapper) {
        if (observable == null) {
            return ObservableSubscription.empty( );
        }
        return new ObservableSubscription<>(
                observable.doOnNext(onNext)
                          .doOnComplete(onComplete)
                          .doOnError(onError)
                          .flatMapSingle(t -> mapper.apply(t).getSingle( )),
                disposableContainer
        );
    }
    
    public CompletableSubscription thenCompletable(Function<T, CompletableSubscription> mapper) {
        if (observable == null) {
            return CompletableSubscription.empty( );
        }
        return new CompletableSubscription(
                observable.doOnNext(onNext)
                          .doOnComplete(onComplete)
                          .doOnError(onError)
                          .flatMapCompletable(t -> mapper.apply(t).getCompletable( )),
                disposableContainer
        );
    }
    
    @Override
    public void subscribe( ) {
        if (observable == null) {
            return;
        }
        disposableContainer.add(
                observable.subscribeOn(subscribingScheduler)
                          .observeOn(observingScheduler)
                          .subscribe(onNext, onError, onComplete)
        );
    }
}
