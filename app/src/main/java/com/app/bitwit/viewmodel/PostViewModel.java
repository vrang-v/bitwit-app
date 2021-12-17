package com.app.bitwit.viewmodel;

import androidx.lifecycle.MutableLiveData;
import com.app.bitwit.data.repository.AccountRepository;
import com.app.bitwit.data.repository.PostRepository;
import com.app.bitwit.data.source.remote.dto.CreateCommentRequest;
import com.app.bitwit.domain.Comment;
import com.app.bitwit.domain.Like;
import com.app.bitwit.domain.Post;
import com.app.bitwit.dto.LoginAccount;
import com.app.bitwit.util.Empty;
import com.app.bitwit.util.StringUtils;
import com.app.bitwit.util.subscription.SingleSubscription;
import com.app.bitwit.util.subscription.Subscription;
import com.app.bitwit.viewmodel.common.RxJavaViewModelSupport;
import com.app.bitwit.viewmodel.common.SnackbarViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import lombok.Getter;
import lombok.var;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Getter
@HiltViewModel
public class PostViewModel extends RxJavaViewModelSupport implements SnackbarViewModel {
    
    private final PostRepository    postRepository;
    private final AccountRepository accountRepository;
    
    private final MutableLiveData<Post> post = new MutableLiveData<>( );
    
    private final MutableLiveData<List<Comment>> flattenComments = new MutableLiveData<>( );
    
    private final MutableLiveData<String>      commentContent  = new MutableLiveData<>( );
    private final MutableLiveData<String>      commentDetail   = new MutableLiveData<>("댓글을 작성하고 있어요");
    private final MutableLiveData<String>      commentLength   = new MutableLiveData<>( );
    private final MutableLiveData<Comment>     commentSelected = new MutableLiveData<>( );
    private final MutableLiveData<CommentType> commentType     = new MutableLiveData<>(CommentType.COMMENT);
    
    private final MutableLiveData<LoginAccount> account = new MutableLiveData<>( );
    
    private boolean likeChanged = false;
    
    @Inject
    public PostViewModel(PostRepository postRepository, AccountRepository accountRepository) {
        this.postRepository    = postRepository;
        this.accountRepository = accountRepository;
        loadAccount( ).subscribe( );
    }
    
    public Subscription<LoginAccount> loadAccount( ) {
        return subscribe(
                accountRepository
                        .loadAccount( )
                        .doOnSuccess(account::postValue)
                        .doOnError(e -> setSnackbar("사용자 정보를 불러오는 도중 문제가 발생했어요"))
        );
    }
    
    public Subscription<Post> loadPost(Long postId) {
        return subscribe(
                postRepository.
                        viewPost(postId)
                        .doOnSuccess(post -> postFlattenComments(post.getComments( )))
                        .doOnSuccess(post::postValue)
        );
    }
    
    public Subscription<Empty> deletePost( ) {
        return subscribe(
                postRepository
                        .deletePost(post.getValue( ).getId( ))
                        .doOnError(e -> setSnackbar("게시글을 삭제하는 도중 문제가 발생했어요"))
        );
    }
    
    public Subscription<Comment> createComment( ) {
        String content = commentContent.getValue( ).trim( );
        if (! isValidComment(content)) {
            return empty( );
        }
        var request = new CreateCommentRequest(content, post.getValue( ).getId( ), getParentId( ));
        return subscribe(
                postRepository
                        .createComment(request)
                        .doOnSuccess(comment -> commentContent.postValue(""))
                        .doOnError(e -> setSnackbar("댓글을 등록하는 도중 문제가 발생했어요"))
        );
    }
    
    public Subscription<Empty> updateComment( ) {
        String content = commentContent.getValue( ).trim( );
        if (! isValidComment(content)) {
            return empty( );
        }
        return subscribe(
                postRepository
                        .updateComment(commentSelected.getValue( ).getId( ), content)
                        .doOnSuccess(unused -> commentContent.postValue(""))
                        .doOnError(e -> setSnackbar("댓글을 수정하는 도중 문제가 발생했어요"))
        );
    }
    
    public Subscription<Empty> deleteComment(Long commentId) {
        return subscribe(
                postRepository
                        .deleteComment(commentId)
                        .doOnError(e -> setSnackbar("댓글을 삭제하는 도중 문제가 발생했어요"))
        );
    }
    
    public Subscription<List<Comment>> reloadComments( ) {
        return subscribe(
                postRepository
                        .getCommentsOnPost(post.getValue( ).getId( ))
                        .doOnSuccess(this::postFlattenComments)
                        .doOnError(e -> setSnackbar("댓글을 불러오는 도중 문제가 발생했습니다"))
        );
    }
    
    private SingleSubscription<Like> like( ) {
        return subscribe(postRepository.like(this.post.getValue( ).getId( )));
    }
    
    private Subscription<Like> unlike( ) {
        return subscribe(postRepository.unlike(this.post.getValue( ).getId( )));
    }
    
    public Subscription<Like> likeComment(Long commentId) {
        return subscribe(postRepository.likeComment(commentId));
    }
    
    public Subscription<Like> unlikeComment(Long commentId) {
        return subscribe(postRepository.unlikeComment(commentId));
    }
    
    private void postFlattenComments(List<Comment> nestedComments) {
        var flattenComments = new ArrayList<Comment>( );
        nestedComments.forEach(comment -> getChildrenComments(comment, flattenComments));
        flattenComments.forEach(comment -> comment.setEditable(account.getValue( ).getId( )));
        this.flattenComments.postValue(flattenComments);
    }
    
    private void getChildrenComments(Comment comment, List<Comment> flattenComments) {
        if (comment.getDepth( ) > 1) {
            return;
        }
        flattenComments.add(comment);
        if (comment.getChildren( ).isEmpty( )) {
            return;
        }
        comment.getChildren( ).forEach(child -> getChildrenComments(child, flattenComments));
    }
    
    public void invertLike( ) {
        likeChanged = ! likeChanged;
        Post post = this.post.getValue( );
        post.setLikeCount(post.isLike( ) ? post.getLikeCount( ) - 1 : post.getLikeCount( ) + 1);
        post.setLike(! post.isLike( ));
        this.post.postValue(post);
    }
    
    public void commitLike( ) {
        if (likeChanged) {
            (post.getValue( ).isLike( ) ? like( ) : unlike( )).subscribe( );
        }
    }
    
    private boolean isValidComment(String content) {
        if (StringUtils.hasText(content)) {
            return true;
        }
        setSnackbar("댓글을 다시 확인해주세요");
        return false;
    }
    
    @Nullable
    private Long getParentId( ) {
        Long parentId = null;
        switch (commentType.getValue( )) {
            case COMMENT:
                break;
            case COMMENT_REPLY:
                parentId = commentSelected.getValue( ).getId( );
                break;
            case REPLY_REPLY:
                parentId = commentSelected.getValue( ).getParentId( );
                break;
        }
        return parentId;
    }
    
    public void setCommentContent(String message) {
        commentContent.postValue(message);
    }
    
    public void setCommentDetail(String message) {
        commentDetail.postValue(message);
    }
    
    public void setCommentSelected(Comment parent) {
        commentSelected.postValue(parent);
    }
    
    public void setCommentLengthInfo(String lengthInfo) {
        commentLength.postValue(lengthInfo);
    }
    
    public void setCommentType(CommentType commentType) {
        this.commentType.postValue(commentType);
    }
    
    public enum CommentType {
        COMMENT, COMMENT_REPLY, REPLY_REPLY, EDIT
    }
}
