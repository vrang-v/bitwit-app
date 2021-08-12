package com.app.bitwit.viewmodel;

import androidx.lifecycle.MutableLiveData;
import com.app.bitwit.data.repository.VoteRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;
import lombok.Getter;

import javax.inject.Inject;

@Getter
@HiltViewModel
public class SearchActivityViewModel extends DisposableViewModel {
    
    private final VoteRepository voteRepository;
    
    private final MutableLiveData<String> searchWord = new MutableLiveData<>( );
    
    @Inject
    public SearchActivityViewModel(VoteRepository voteRepository) {
        this.voteRepository = voteRepository;
    }
    
    public void search(String searchWord) {
        //TODO voteRepository.search(searchWord);
    }
}
