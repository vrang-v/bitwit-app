package com.app.bitwit.util;

import androidx.lifecycle.LiveData;

public interface SnackbarViewModel {
    
    MutableLiveEvent<String> MESSAGE = new MutableLiveEvent<>( );
    
    default LiveData<String> getSnackbar( ) {
        return MESSAGE;
    }
    
    default void setSnackbar(String message) {
        MESSAGE.publish(message);
    }
}
