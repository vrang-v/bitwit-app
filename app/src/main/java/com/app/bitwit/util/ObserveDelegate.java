package com.app.bitwit.util;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

public class ObserveDelegate<T> {
    
    private final Observer<T> observer;
    
    private LiveData<T> liveData;
    
    public ObserveDelegate(MutableLiveData<T> mutableLiveData) {
        this.observer = mutableLiveData::postValue;
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
