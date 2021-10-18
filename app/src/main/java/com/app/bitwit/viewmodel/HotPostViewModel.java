package com.app.bitwit.viewmodel;

import android.telecom.Call;
import com.app.bitwit.data.repository.PostRepository;
import com.app.bitwit.domain.Like;
import com.app.bitwit.domain.Post;
import com.app.bitwit.util.Callback;
import com.app.bitwit.util.MutableLiveList;
import com.app.bitwit.util.SnackbarViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import lombok.Getter;

import javax.inject.Inject;
import java.util.List;

@Getter
@HiltViewModel
public class HotPostViewModel extends RxJavaViewModelSupport implements SnackbarViewModel {
    
    private static final int PAGE_SIZE = 15;
    
    private final MutableLiveList<Post> posts = new MutableLiveList<>( );
    private final PostRepository        postRepository;
    
    private int postsPage = 0;
    
    @Inject
    public HotPostViewModel(PostRepository postRepository) {
        this.postRepository = postRepository;
    }
    
    public void refreshPost(Long postId, Callback<Post> callback) {
        subscribe(callback, postRepository.getPost(postId));
    }
    
    public void loadMostViewedPostNextPage(Callback<List<Post>> callback) {
        subscribe(callback,
                postRepository.getMostViewedPostPage(postsPage, PAGE_SIZE)
                              .doOnSuccess(x -> postsPage += 1)
                              .doOnSuccess(posts::addAll)
        );
    }
    
    public void refreshPage(Callback<List<Post>> callback) {
        postsPage = 0;
        subscribe(callback,
                postRepository.getMostViewedPostPage(postsPage, PAGE_SIZE)
                              .doOnSuccess(x -> postsPage += 1)
                              .doOnSuccess(posts::postValue)
        );
    }
    
    public void like(long postId, Callback<Like> callback) {
        subscribe(callback, postRepository.like(postId));
    }
    
    public void unlike(long postId, Callback<Like> callback) {
        subscribe(callback, postRepository.unlike(postId));
    }
}
