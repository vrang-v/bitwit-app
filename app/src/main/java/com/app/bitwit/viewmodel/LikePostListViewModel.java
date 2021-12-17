package com.app.bitwit.viewmodel;

import com.app.bitwit.data.repository.AccountRepository;
import com.app.bitwit.data.repository.PostRepository;
import com.app.bitwit.domain.Post;
import com.app.bitwit.dto.LoginAccount;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.Single;

import javax.inject.Inject;
import java.util.List;

@HiltViewModel
public class LikePostListViewModel extends PostListViewModel {
    
    private LoginAccount loginAccount;
    
    @Inject
    public LikePostListViewModel(PostRepository postRepository, AccountRepository accountRepository) {
        super(postRepository);
        accountRepository.loadAccount( )
                         .doOnSuccess(account -> this.loginAccount = account)
                         .subscribe( );
    }
    
    @Override
    public int getPageSize( ) {
        return 20;
    }
    
    @Override
    protected Single<List<Post>> loadPosts( ) {
        return postRepository.getPostPageByLikerId(loginAccount.getId( ), postPage, getPageSize( ));
    }
}
