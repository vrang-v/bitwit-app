package com.app.bitwit.viewmodel;

import com.app.bitwit.data.repository.AccountRepository;
import com.app.bitwit.data.repository.PostRepository;
import com.app.bitwit.domain.Comment;
import com.app.bitwit.dto.LoginAccount;
import com.app.bitwit.dto.Page;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.Single;

import javax.inject.Inject;

@HiltViewModel
public class MyCommentListViewModel extends CommentListViewModel {
    
    private LoginAccount loginAccount;
    
    @Inject
    public MyCommentListViewModel(PostRepository postRepository, AccountRepository accountRepository) {
        super(postRepository);
        accountRepository.loadAccount( )
                         .doOnSuccess(account -> loginAccount = account)
                         .subscribe( );
    }
    
    @Override
    public int getPageSize( ) {
        return 20;
    }
    
    @Override
    protected Single<Page<Comment>> loadCommentPage( ) {
        return postRepository.getCommentPageByWriterId(loginAccount.getId( ), page, getPageSize( ));
    }
}
