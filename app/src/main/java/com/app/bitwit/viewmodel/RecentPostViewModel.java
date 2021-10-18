package com.app.bitwit.viewmodel;

import com.app.bitwit.data.repository.PostRepository;
import com.app.bitwit.domain.Like;
import com.app.bitwit.domain.Post;
import com.app.bitwit.util.Callback;
import com.app.bitwit.util.MutableLiveList;
import com.app.bitwit.util.SnackbarViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import lombok.Getter;
import lombok.var;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@HiltViewModel
public class RecentPostViewModel extends RxJavaViewModelSupport implements SnackbarViewModel {
    
    private static final int PAGE_SIZE = 15;
    
    private final MutableLiveList<Post> posts = new MutableLiveList<>( );
    
    private final PostRepository postRepository;
    
    private int pageNumber = 0;
    
    @Inject
    public RecentPostViewModel(PostRepository postRepository) {
        this.postRepository = postRepository;
    }
    
    public void loadRecentPostNextPage(Callback<List<Post>> callback) {
        subscribe(callback,
                postRepository.getMostRecentlyPostPage(pageNumber, PAGE_SIZE)
                              .doOnSuccess(x -> pageNumber += 1)
                              .doOnSuccess(posts::addAll)
        );
    }
    
    public void refreshPage(Callback<List<Post>> callback) {
        pageNumber = 0;
        subscribe(callback,
                postRepository.getMostRecentlyPostPage(pageNumber, PAGE_SIZE)
                              .doOnSuccess(x -> pageNumber += 1)
                              .doOnSuccess(posts::postValue)
        );
    }
    
    public void refreshPost(Long postId, Callback<Post> callback) {
        subscribe(callback, postRepository.getPost(postId));
    }
    
    public void like(long postId, Callback<Like> callback) {
        subscribe(callback, postRepository.like(postId));
    }
    
    public void unlike(long postId, Callback<Like> callback) {
        subscribe(callback, postRepository.unlike(postId));
    }
    
    public void removePost(Long postId) {
        var removed = posts.stream( )
                           .filter(post -> post.getId( ).equals(postId))
                           .collect(Collectors.toList( ));
        posts.removeAll(removed);
    }
}
