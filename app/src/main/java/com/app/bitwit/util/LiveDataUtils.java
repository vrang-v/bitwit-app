package com.app.bitwit.util;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LiveDataUtils {
    
    public static <T> void observeUntilNotNull(final LiveData<T> liveData, final Observer<T> observer) {
        liveData.observeForever(new Observer<T>( ) {
            @Override
            public void onChanged(T t) {
                if (t != null) { liveData.removeObserver(this); }
                observer.onChanged(t);
            }
        });
    }
    
    public static <T extends Collection<?>> void observeUntilNotEmpty(final LiveData<T> liveData, final Observer<T> observer) {
        liveData.observeForever(new Observer<T>( ) {
            @Override
            public void onChanged(T t) {
                if (! t.isEmpty( )) { liveData.removeObserver(this); }
                observer.onChanged(t);
            }
        });
    }
    
    public static <T> void observeNotNull(final LiveData<T> liveData, final LifecycleOwner lifecycleOwner, final Observer<T> observer) {
        liveData.observe(lifecycleOwner, t -> {
            if (t != null) {
                observer.onChanged(t);
            }
        });
    }
    
    public static <T extends Collection<?>> void observeNotEmpty(final LiveData<T> liveData, final LifecycleOwner lifecycleOwner, final Observer<T> observer) {
        liveData.observe(lifecycleOwner, t -> {
            if (! t.isEmpty( )) {
                observer.onChanged(t);
            }
        });
    }
}