package com.app.bitwit.util;

import androidx.lifecycle.LiveData;
import com.app.bitwit.viewmodel.MutableLiveEvent;

public interface SnackbarViewModel {
    
    MutableLiveEvent<String> MESSAGE = new MutableLiveEvent<>( );
    
    default LiveData<String> getSnackbar( ) {
        return MESSAGE;
    }
    
    default void setSnackbar(String message) {
        MESSAGE.publish(message);
    }
}
