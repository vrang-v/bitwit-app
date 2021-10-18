package com.app.bitwit.viewmodel;

import androidx.lifecycle.MutableLiveData;
import com.app.bitwit.data.repository.AccountRepository;
import com.app.bitwit.data.repository.PostRepository;
import com.app.bitwit.data.source.remote.dto.request.CreatePostRequest;
import com.app.bitwit.domain.Post;
import com.app.bitwit.util.Callback;
import com.app.bitwit.util.MutableLiveList;
import com.app.bitwit.util.SnackbarViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import lombok.Getter;
import lombok.var;

import javax.inject.Inject;

@Getter
@HiltViewModel
public class PostingViewModel extends RxJavaViewModelSupport implements SnackbarViewModel {
    
    private final PostRepository    postRepository;
    private final AccountRepository accountRepository;
    
    private final MutableLiveData<String> title   = new MutableLiveData<>( );
    private final MutableLiveData<String> content = new MutableLiveData<>( );
    private final MutableLiveData<String> info    = new MutableLiveData<>( );
    private final MutableLiveData<String> length  = new MutableLiveData<>( );
    
    private final MutableLiveList<String> tags     = new MutableLiveList<>( );
    private final MutableLiveData<String> inputTag = new MutableLiveData<>( );
    
    @Inject
    public PostingViewModel(PostRepository postRepository, AccountRepository accountRepository) {
        this.postRepository    = postRepository;
        this.accountRepository = accountRepository;
    }
    
    public void createPost(Callback<Post> callback) {
        var request = new CreatePostRequest( );
        request.setTitle(title.getValue( ));
        request.setContent(content.getValue( ));
        request.setTickers(tags);
        subscribe(callback, postRepository.createPost(request));
    }
    
    public void addTag( ) {
        tags.add(inputTag.getValue( ));
        inputTag.postValue("");
    }
}
