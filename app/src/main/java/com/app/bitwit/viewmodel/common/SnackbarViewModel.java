package com.app.bitwit.viewmodel.common;

import androidx.lifecycle.LiveData;
import com.app.bitwit.util.livedata.MutableLiveEvent;

public interface SnackbarViewModel {
    
    MutableLiveEvent<String> MESSAGE = new MutableLiveEvent<>( );
    
    default LiveData<String> getSnackbar( ) {
        return MESSAGE;
    }
    
    default void setSnackbar(String message) {
        MESSAGE.publish(message);
    }
}
