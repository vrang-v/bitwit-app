package com.app.bitwit.util.livedata;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class LiveDataObserver<T> extends MutableLiveData<T> {
    
    private LiveData<T> source;
    
    private final Observer<T> observer = this::postValue;
    
    public LiveDataObserver(LiveData<T> liveData) {
        source = liveData;
        this.source.observeForever(observer);
    }
    
    public void changeSource(LiveData<T> liveData) {
        if (this.source != null) {
            this.source.removeObserver(observer);
        }
        this.source = liveData;
        this.source.observeForever(observer);
    }
    
    public void dispose( ) {
        this.source.removeObserver(observer);
    }
}
