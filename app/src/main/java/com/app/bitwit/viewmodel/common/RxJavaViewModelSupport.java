package com.app.bitwit.viewmodel.common;

import androidx.lifecycle.ViewModel;
import com.app.bitwit.util.Callback;
import com.app.bitwit.util.subscription.CompletableSubscription;
import com.app.bitwit.util.subscription.ObservableSubscription;
import com.app.bitwit.util.subscription.SingleSubscription;
import com.app.bitwit.util.subscription.Subscription;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RxJavaViewModelSupport extends ViewModel {
    
    private final CompositeDisposable compositeDisposable = new CompositeDisposable( );
    
    @Deprecated
    public void addDisposable(Disposable disposable) {
        compositeDisposable.add(disposable);
    }
    
    @Deprecated
    public <T> void subscribeOld(Single<T> single) {
        addDisposable(
                single.subscribeOn(Schedulers.io( ))
                      .observeOn(AndroidSchedulers.mainThread( ))
                      .subscribe((t, throwable) -> { })
        );
    }
    
    @Deprecated
    public <T> void subscribeOld(Callback<T> callback, Single<T> single) {
        if (callback == null) {
            subscribeOld(single);
            return;
        }
        addDisposable(
                single.subscribeOn(Schedulers.io( ))
                      .observeOn(AndroidSchedulers.mainThread( ))
                      .subscribe(callback.getOnSuccess( ), callback.getOnError( ))
        );
    }
    
    public <T> ObservableSubscription<T> subscribe(Observable<T> observable) {
        return new ObservableSubscription<>(observable, compositeDisposable);
    }
    
    public <T> SingleSubscription<T> subscribe(Single<T> single) {
        return new SingleSubscription<>(single, compositeDisposable);
    }
    
    public CompletableSubscription subscribe(Completable completable) {
        return new CompletableSubscription(completable, compositeDisposable);
    }
    
    public <T> Subscription<T> unsubscribe( ) {
        return ( ) -> { };
    }
    
    @Override
    protected void onCleared( ) {
        super.onCleared( );
        compositeDisposable.clear( );
    }
}
