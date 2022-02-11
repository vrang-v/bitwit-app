package com.app.bitwit.viewmodel;

import com.app.bitwit.data.repository.PostRepository;
import com.app.bitwit.domain.Comment;
import com.app.bitwit.dto.Page;
import com.app.bitwit.util.Empty;
import com.app.bitwit.util.livedata.MutableLiveList;
import com.app.bitwit.util.subscription.SingleSubscription;
import com.app.bitwit.viewmodel.common.RxJavaViewModelSupport;
import com.app.bitwit.viewmodel.common.SnackbarViewModel;
import io.reactivex.rxjava3.core.Single;
import lombok.Getter;

public abstract class CommentListViewModel extends RxJavaViewModelSupport implements SnackbarViewModel {
    
    @Getter
    protected final MutableLiveList<Comment> comments = new MutableLiveList<>( );
    protected final PostRepository           postRepository;
    
    protected int     page;
    protected boolean last;
    
    protected CommentListViewModel(PostRepository postRepository) {
        this.postRepository = postRepository;
    }
    
    public abstract int getPageSize( );
    
    protected abstract Single<Page<Comment>> loadCommentPage( );
    
    public SingleSubscription<Page<Comment>> nextPage( ) {
        if (last) {
            return SingleSubscription.empty( );
        }
        return subscribe(
                loadCommentPage( )
                        .doOnSuccess(x -> page += 1)
                        .doOnSuccess(commentPage -> last = commentPage.isLast( ))
                        .doOnSuccess(commentPage -> comments.postValue(commentPage.getContent( )))
                        .doOnError(e -> setSnackbar("댓글을 불러오는 도중 문제가 발생했어요"))
        );
    }
    
    public SingleSubscription<Page<Comment>> refreshPage( ) {
        page = 0;
        return subscribe(
                loadCommentPage( )
                        .doOnSuccess(commentPage -> {
                            page += 1;
                            last = commentPage.isLast( );
                            comments.postValue(commentPage.getContent( ));
                        })
                        .doOnError(e -> setSnackbar("새로고침 도중 문제가 발생했어요"))
        );
    }
    
    public SingleSubscription<Empty> deleteComment(Long commentId) {
        return subscribe(
                postRepository
                        .deleteComment(commentId)
                        .doOnSuccess(x -> setSnackbar("댓글을 삭제했어요"))
                        .doOnError(e -> setSnackbar("댓글을 삭제하는 도중 문제가 발생했어요"))
        );
    }
}
