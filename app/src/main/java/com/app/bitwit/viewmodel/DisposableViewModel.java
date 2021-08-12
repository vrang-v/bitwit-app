package com.app.bitwit.viewmodel;

import androidx.lifecycle.ViewModel;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public class DisposableViewModel extends ViewModel {
    
    private final CompositeDisposable compositeDisposable = new CompositeDisposable( );
    
    public void addDisposable(Disposable disposable) {
        compositeDisposable.add(disposable);
    }
    
    @Override
    protected void onCleared( ) {
        compositeDisposable.clear( );
        super.onCleared( );
    }
}
