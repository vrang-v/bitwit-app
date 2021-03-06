package com.app.bitwit.viewmodel;

import com.app.bitwit.data.repository.PostRepository;
import com.app.bitwit.domain.Like;
import com.app.bitwit.domain.Post;
import com.app.bitwit.util.livedata.MutableLiveList;
import com.app.bitwit.util.subscription.SingleSubscription;
import com.app.bitwit.viewmodel.common.RxJavaViewModelSupport;
import com.app.bitwit.viewmodel.common.SnackbarViewModel;
import io.reactivex.rxjava3.core.Single;
import lombok.Getter;
import lombok.var;

import java.util.List;
import java.util.stream.Collectors;

public abstract class PostListViewModel extends RxJavaViewModelSupport implements SnackbarViewModel {
    
    @Getter
    protected final MutableLiveList<Post> posts = new MutableLiveList<>( );
    protected final PostRepository        postRepository;
    
    protected int postPage = 0;
    
    protected PostListViewModel(PostRepository postRepository) {
        this.postRepository = postRepository;
    }
    
    public abstract int getPageSize( );
    
    protected abstract Single<List<Post>> loadPosts( );
    
    public SingleSubscription<Post> loadPost(Long postId) {
        return subscribe(postRepository.getPost(postId));
    }
    
    public SingleSubscription<List<Post>> nextPage( ) {
        return subscribe(
                loadPosts( )
                        .doOnSuccess(x -> postPage += 1)
                        .doOnSuccess(posts::addAll)
                        .doOnError(e -> setSnackbar("게시물을 불러오는 도중 문제가 발생했어요"))
        );
    }
    
    public SingleSubscription<List<Post>> refreshPage( ) {
        postPage = 0;
        return subscribe(
                loadPosts( )
                        .doOnSuccess(x -> postPage += 1)
                        .doOnSuccess(posts::postValue)
                        .doOnError(e -> setSnackbar("새로고침 도중 문제가 발생했어요"))
        );
    }
    
    public void removePost(Long postId) {
        var removed = posts.stream( )
                           .filter(post -> post.getId( ).equals(postId))
                           .collect(Collectors.toList( ));
        posts.removeAll(removed);
    }
    
    public SingleSubscription<Like> like(long postId) {
        return subscribe(postRepository.like(postId));
    }
    
    public SingleSubscription<Like> unlike(long postId) {
        return subscribe(postRepository.unlike(postId));
    }
}
