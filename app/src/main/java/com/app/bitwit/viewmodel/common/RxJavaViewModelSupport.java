package com.app.bitwit.viewmodel.common;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import com.app.bitwit.util.subscription.CompletableSubscription;
import com.app.bitwit.util.subscription.ObservableSubscription;
import com.app.bitwit.util.subscription.SingleSubscription;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

import java.util.ArrayList;
import java.util.List;

public class RxJavaViewModelSupport extends ViewModel {
    
    private final CompositeDisposable compositeDisposable = new CompositeDisposable( );
    
    private final List<Runnable> onClearedRunnableList = new ArrayList<>( );
    
    public <T> ObservableSubscription<T> subscribe(Observable<T> observable) {
        return new ObservableSubscription<>(observable, compositeDisposable);
    }
    
    public <T> SingleSubscription<T> subscribe(Single<T> single) {
        return new SingleSubscription<>(single, compositeDisposable);
    }
    
    public CompletableSubscription subscribe(Completable completable) {
        return new CompletableSubscription(completable, compositeDisposable);
    }
    
    public <T> void observe(LiveData<T> liveData, Observer<T> observer) {
        liveData.observeForever(observer);
        onClearedRunnableList.add(( ) -> liveData.removeObserver(observer));
    }
    
    @Override
    protected void onCleared( ) {
        super.onCleared( );
        compositeDisposable.clear( );
        onClearedRunnableList.forEach(Runnable::run);
    }
}
