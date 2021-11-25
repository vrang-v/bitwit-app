package com.app.bitwit.viewmodel;

import androidx.lifecycle.MutableLiveData;
import com.app.bitwit.util.SnackbarViewModel;
import com.app.bitwit.viewmodel.common.RxJavaViewModelSupport;
import dagger.hilt.android.lifecycle.HiltViewModel;
import lombok.Getter;

import javax.inject.Inject;

@Getter
@HiltViewModel
public class PostSearchViewModel extends RxJavaViewModelSupport implements SnackbarViewModel {
    
    
    private final MutableLiveData<String> searchWord = new MutableLiveData<>( );
    
    @Inject
    public PostSearchViewModel( ) {
    }
}
