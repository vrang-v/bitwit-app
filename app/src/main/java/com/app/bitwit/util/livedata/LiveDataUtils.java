package com.app.bitwit.util.livedata;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import com.app.bitwit.util.StringUtils;
import com.app.bitwit.util.livedata.Consumer3;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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
    
    public static <T> void observeNotNull(final LifecycleOwner lifecycleOwner, final LiveData<T> liveData, final Observer<T> observer) {
        liveData.observe(lifecycleOwner, t -> {
            if (t != null) {
                observer.onChanged(t);
            }
        });
    }
    
    public static void observeHasText(final LifecycleOwner lifecycleOwner, final LiveData<String> liveData, final Observer<String> observer) {
        liveData.observe(lifecycleOwner, s -> {
            if (StringUtils.hasText(s)) {
                observer.onChanged(s);
            }
        });
    }
    
    public static <T extends Collection<?>> void observeNotEmpty(final LifecycleOwner lifecycleOwner, final LiveData<T> liveData, final Observer<T> observer) {
        liveData.observe(lifecycleOwner, t -> {
            if (! t.isEmpty( )) {
                observer.onChanged(t);
            }
        });
    }
    
    public static <T> void observe(final LifecycleOwner lifecycleOwner, final LiveData<T> liveData, final Consumer<T> consumer) {
        liveData.observe(lifecycleOwner, consumer::accept);
    }
    
    public static <T1, T2> void observeAll(final LifecycleOwner lifecycleOwner, final LiveData<T1> liveData1, final LiveData<T2> liveData2, final BiConsumer<T1, T2> consumer) {
        liveData1.observe(lifecycleOwner, t1 -> consumer.accept(t1, liveData2.getValue( )));
        liveData2.observe(lifecycleOwner, t2 -> consumer.accept(liveData1.getValue( ), t2));
    }
    
    public static <T1, T2> void observeAllNotNull(final LifecycleOwner lifecycleOwner, final LiveData<T1> liveData1, final LiveData<T2> liveData2, final BiConsumer<T1, T2> consumer) {
        liveData1.observe(lifecycleOwner, t1 -> {
            if (t1 != null && liveData2.getValue( ) != null) {
                consumer.accept(t1, liveData2.getValue( ));
            }
        });
        
        liveData2.observe(lifecycleOwner, t2 -> {
            if (liveData1.getValue( ) != null && t2 != null) {
                consumer.accept(liveData1.getValue( ), t2);
            }
        });
    }
    
    public static <T1, T2, T3> void observeAllNotNull(final LifecycleOwner lifecycleOwner, final LiveData<T1> liveData1, final LiveData<T2> liveData2, final LiveData<T3> liveData3, final Consumer3<T1, T2, T3> consumer) {
        liveData1.observe(lifecycleOwner, t1 -> {
            if (t1 != null && liveData2.getValue( ) != null && liveData3.getValue( ) != null) {
                consumer.consume(t1, liveData2.getValue( ), liveData3.getValue( ));
            }
        });
        
        liveData2.observe(lifecycleOwner, t2 -> {
            if (liveData1.getValue( ) != null && t2 != null && liveData3.getValue( ) != null) {
                consumer.consume(liveData1.getValue( ), t2, liveData3.getValue( ));
            }
        });
        
        liveData3.observe(lifecycleOwner, t3 -> {
            if (liveData1.getValue( ) != null && liveData2.getValue( ) != null && t3 != null) {
                consumer.consume(liveData1.getValue( ), liveData2.getValue( ), t3);
            }
        });
    }
}