package com.app.bitwit.util;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

public class MutableObserver<T> {
    
    private final Observer<T> observer;
    
    private LiveData<T> liveData;
    
    public MutableObserver(Observer<T> observer) {
        this.observer = observer;
    }
    
    public void observe(LiveData<T> liveData) {
        if (this.liveData != null) {
            this.liveData.removeObserver(observer);
        }
        this.liveData = liveData;
        this.liveData.observeForever(observer);
    }
    
    public void dispose( ) {
        this.liveData.removeObserver(observer);
    }
}
