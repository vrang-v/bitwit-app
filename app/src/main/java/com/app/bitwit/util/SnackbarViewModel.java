package com.app.bitwit.util;

import androidx.lifecycle.LiveData;
import com.app.bitwit.viewmodel.MutableLiveEvent;

public interface SnackbarViewModel {
    
    MutableLiveEvent<String> SNACKBAR = new MutableLiveEvent<>( );
    
    default LiveData<String> getSnackbar( ) {
        return SNACKBAR;
    }
    
    default void setSnackbar(String message) {
        SNACKBAR.publish(message);
    }
}
