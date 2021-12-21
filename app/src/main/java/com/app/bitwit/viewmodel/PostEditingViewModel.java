package com.app.bitwit.viewmodel;

import androidx.lifecycle.MutableLiveData;
import com.app.bitwit.data.repository.AccountRepository;
import com.app.bitwit.data.repository.PostRepository;
import com.app.bitwit.data.source.remote.dto.request.UpdatePostRequest;
import com.app.bitwit.domain.Post;
import com.app.bitwit.domain.Stock;
import com.app.bitwit.dto.LoginAccount;
import com.app.bitwit.util.livedata.MutableLiveList;
import com.app.bitwit.util.subscription.SingleSubscription;
import com.app.bitwit.util.subscription.Subscription;
import com.app.bitwit.viewmodel.common.RxJavaViewModelSupport;
import com.app.bitwit.viewmodel.common.SnackbarViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import lombok.Getter;
import lombok.var;

import javax.inject.Inject;
import java.util.stream.Collectors;

@Getter
@HiltViewModel
public class PostEditingViewModel extends RxJavaViewModelSupport implements SnackbarViewModel {
    
    private final PostRepository    postRepository;
    private final AccountRepository accountRepository;
    
    private final MutableLiveData<String> title   = new MutableLiveData<>( );
    private final MutableLiveData<String> content = new MutableLiveData<>( );
    private final MutableLiveData<String> info    = new MutableLiveData<>( );
    private final MutableLiveData<String> length  = new MutableLiveData<>( );
    
    private final MutableLiveList<String> tags     = new MutableLiveList<>( );
    private final MutableLiveData<String> inputTag = new MutableLiveData<>( );
    
    private Long         postId;
    private LoginAccount account;
    
    @Inject
    public PostEditingViewModel(PostRepository postRepository, AccountRepository accountRepository) {
        this.postRepository    = postRepository;
        this.accountRepository = accountRepository;
    }
    
    public Subscription<LoginAccount> loadAccount( ) {
        return subscribe(
                accountRepository
                        .loadAccount( )
                        .doOnSuccess(loginAccount -> this.account = loginAccount)
                        .doOnError(e -> setSnackbar("사용자 정보를 불러오는 도중 문제가 발생했어요"))
        );
    }
    
    public Subscription<Post> loadPost( ) {
        return subscribe(
                postRepository.getPost(postId)
                              .doOnSuccess(post -> {
                                  title.postValue(post.getTitle( ));
                                  content.postValue(post.getContent( ));
                                  tags.postValue(
                                          post.getStocks( )
                                              .stream( )
                                              .map(Stock::getTicker)
                                              .collect(Collectors.toList( )));
                              })
                              .doOnError(e -> setSnackbar("게시물을 불러오는 도중 문제가 발생했어요"))
        );
    }
    
    public SingleSubscription<Post> updatePost( ) {
        var request = new UpdatePostRequest( );
        request.setPostId(postId);
        request.setTitle(title.getValue( ));
        request.setContent(content.getValue( ));
        request.setTickers(tags);
        return subscribe(
                postRepository.updatePost(request)
                              .doOnError(e -> setSnackbar("게시물을 수정하는 도중 문제가 발생했어요"))
        );
    }
    
    public void setPostId(Long postId) {
        this.postId = postId;
    }
    
    public void addTag( ) {
        tags.add(inputTag.getValue( ));
        inputTag.postValue("");
    }
}
