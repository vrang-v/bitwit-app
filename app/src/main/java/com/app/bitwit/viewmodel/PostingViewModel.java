package com.app.bitwit.viewmodel;

import androidx.lifecycle.MutableLiveData;
import com.app.bitwit.data.repository.AccountRepository;
import com.app.bitwit.data.repository.PostRepository;
import com.app.bitwit.data.source.remote.dto.request.CreatePostRequest;
import com.app.bitwit.domain.Post;
import com.app.bitwit.dto.LoginAccount;
import com.app.bitwit.util.livedata.MutableLiveList;
import com.app.bitwit.util.subscription.SingleSubscription;
import com.app.bitwit.viewmodel.common.RxJavaViewModelSupport;
import com.app.bitwit.viewmodel.common.SnackbarViewModel;
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
    
    private LoginAccount account;
    
    @Inject
    public PostingViewModel(PostRepository postRepository, AccountRepository accountRepository) {
        this.postRepository    = postRepository;
        this.accountRepository = accountRepository;
    }
    
    public SingleSubscription<LoginAccount> loadAccount( ) {
        return subscribe(
                accountRepository
                        .loadAccount( )
                        .doOnSuccess(loginAccount -> this.account = loginAccount)
                        .doOnError(e -> setSnackbar("사용자 정보를 불러오는 도중 문제가 발생했어요"))
        );
    }
    
    public SingleSubscription<Post> createPost( ) {
        var request = new CreatePostRequest( );
        request.setTitle(title.getValue( ));
        request.setContent(content.getValue( ));
        request.setTickers(tags);
        request.setTags(tags);
        return subscribe(
                postRepository.createPost(request)
                              .doOnError(e -> setSnackbar("게시물을 작성하는 도중 문제가 발생했어요"))
        );
    }
    
    public void addTag( ) {
        tags.add(inputTag.getValue( ));
        inputTag.postValue("");
    }
    
    public void removeTag(String tag) {
        tags.remove(tag);
    }
}
