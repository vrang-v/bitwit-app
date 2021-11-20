package com.app.bitwit.viewmodel;

import androidx.lifecycle.MutableLiveData;
import com.app.bitwit.data.repository.PostRepository;
import com.app.bitwit.domain.Like;
import com.app.bitwit.domain.Post;
import com.app.bitwit.util.Callback;
import com.app.bitwit.util.MutableLiveList;
import com.app.bitwit.util.SnackbarViewModel;
import com.app.bitwit.util.StringUtils;
import dagger.hilt.android.lifecycle.HiltViewModel;
import lombok.Getter;
import lombok.var;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@HiltViewModel
public class PostSearchViewModel extends RxJavaViewModelSupport implements SnackbarViewModel {
    
    private static final int PAGE_SIZE = 20;
    
    private final PostRepository postRepository;
    
    private final MutableLiveData<String> searchWord = new MutableLiveData<>( );
    private final MutableLiveList<Post>   posts      = new MutableLiveList<>( );
    
    private int pageNumber = 0;
    
    @Inject
    public PostSearchViewModel(PostRepository postRepository) {
        this.postRepository = postRepository;
    }
    
    public void searchPostPage(Callback<List<Post>> callback) {
        if (! StringUtils.hasText(searchWord.getValue( ))) {
            posts.clear( );
            return;
        }
        pageNumber = 0;
        subscribe(callback,
                postRepository
                        .searchPostPageByKeyword(searchWord.getValue( ), pageNumber, PAGE_SIZE)
                        .doOnSuccess(posts -> {
                            this.posts.postValue(posts);
                            pageNumber += 1;
                        })
        );
    }
    
    public void loadNextPage(Callback<List<Post>> callback) {
        subscribe(callback,
                postRepository
                        .searchPostPageByKeyword(searchWord.getValue( ), pageNumber, PAGE_SIZE)
                        .doOnSuccess(posts -> {
                            this.posts.addAll(posts);
                            pageNumber += 1;
                        })
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
