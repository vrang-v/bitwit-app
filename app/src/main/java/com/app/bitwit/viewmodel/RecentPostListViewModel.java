package com.app.bitwit.viewmodel;

import com.app.bitwit.data.repository.PostRepository;
import com.app.bitwit.domain.Post;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.Single;
import lombok.Getter;

import javax.inject.Inject;
import java.util.List;

@Getter
@HiltViewModel
public class RecentPostListViewModel extends PostListViewModel {
    
    @Inject
    protected RecentPostListViewModel(PostRepository postRepository) {
        super(postRepository);
    }
    
    @Override
    public int getPageSize( ) {
        return 20;
    }
    
    @Override
    protected Single<List<Post>> loadPosts( ) {
        return postRepository.getMostRecentlyPostPage(postPage, getPageSize( ));
    }
}
