package com.app.bitwit.viewmodel;

import com.app.bitwit.data.repository.AccountRepository;
import com.app.bitwit.data.repository.PostRepository;
import com.app.bitwit.domain.Post;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.Single;

import javax.inject.Inject;
import java.util.List;

@HiltViewModel
public class MyPostListViewModel extends PostListViewModel {
    
    private final AccountRepository accountRepository;
    
    @Inject
    public MyPostListViewModel(PostRepository postRepository, AccountRepository accountRepository) {
        super(postRepository);
        this.accountRepository = accountRepository;
    }
    
    @Override
    public int getPageSize( ) {
        return 20;
    }
    
    @Override
    protected Single<List<Post>> loadPosts( ) {
        return accountRepository.loadAccount( )
                                .flatMap(account -> postRepository.getRecentlyPostPageByWriterName(
                                        account.getName( ), postPage, getPageSize( )
                                ));
    }
}
