package com.app.bitwit.viewmodel;

import androidx.lifecycle.ViewModel;
import com.app.bitwit.util.Callback;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RxJavaViewModelSupport extends ViewModel {
    
    private final CompositeDisposable compositeDisposable = new CompositeDisposable( );
    
    public void addDisposable(Disposable disposable) {
        compositeDisposable.add(disposable);
    }
    
    public <T> void subscribe(Single<T> single) {
        addDisposable(
                single.subscribeOn(Schedulers.io( ))
                      .observeOn(AndroidSchedulers.mainThread( ))
                      .subscribe((t, throwable) -> { })
        );
    }
    
    public <T> void subscribe(Callback<T> callback, Single<T> single) {
        addDisposable(
                single.subscribeOn(Schedulers.io( ))
                      .observeOn(AndroidSchedulers.mainThread( ))
                      .subscribe(callback.getOnSuccess( ), callback.getOnError( ))
        );
    }
    
    @Override
    protected void onCleared( ) {
        compositeDisposable.clear( );
        super.onCleared( );
    }
}
