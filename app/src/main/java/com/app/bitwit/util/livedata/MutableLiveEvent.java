package com.app.bitwit.util.livedata;

import androidx.annotation.MainThread;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.concurrent.atomic.AtomicBoolean;

public class MutableLiveEvent<T> extends MutableLiveData<T> {
    
    private final AtomicBoolean pending = new AtomicBoolean(false);
    
    @Override
    @MainThread
    public void observe(LifecycleOwner owner, Observer<? super T> observer) {
        super.observe(owner, t -> {
            if (pending.compareAndSet(true, false)) {
                observer.onChanged(t);
            }
        });
    }
    
    @Override
    @MainThread
    public void setValue(T value) {
        pending.set(true);
        super.setValue(value);
    }
    
    @MainThread
    public void publish(T value) {
        setValue(value);
    }
}
