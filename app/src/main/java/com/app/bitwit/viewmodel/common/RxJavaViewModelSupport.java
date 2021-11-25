package com.app.bitwit.viewmodel.common;

import androidx.lifecycle.ViewModel;
import com.app.bitwit.util.subscription.CompletableSubscription;
import com.app.bitwit.util.subscription.ObservableSubscription;
import com.app.bitwit.util.subscription.SingleSubscription;
import com.app.bitwit.util.subscription.Subscription;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class RxJavaViewModelSupport extends ViewModel {
    
    private final CompositeDisposable compositeDisposable = new CompositeDisposable( );
    
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
