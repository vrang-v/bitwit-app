package com.app.bitwit.viewmodel;

import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import com.app.bitwit.data.repository.AccountRepository;
import com.app.bitwit.data.repository.PostRepository;
import com.app.bitwit.data.source.remote.dto.CreateCommentRequest;
import com.app.bitwit.domain.Comment;
import com.app.bitwit.domain.Like;
import com.app.bitwit.domain.Post;
import com.app.bitwit.util.Callback;
import com.app.bitwit.util.Empty;
import com.app.bitwit.util.LoginAccount;
import com.app.bitwit.util.StringUtils;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.Getter;
import lombok.var;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Getter
@HiltViewModel
public class PostViewModel extends RxJavaViewModelSupport {
    
    private final MutableLiveData<Post> post = new MutableLiveData<>( );
    
    private final MutableLiveData<List<Comment>> flattenComments = new MutableLiveData<>( );
    
    private final MutableLiveData<String>      commentContent  = new MutableLiveData<>( );
    private final MutableLiveData<String>      commentDetail   = new MutableLiveData<>("댓글을 작성하고 있어요");
    private final MutableLiveData<String>      commentLength   = new MutableLiveData<>( );
    private final MutableLiveData<Comment>     commentSelected = new MutableLiveData<>( );
    private final MutableLiveData<CommentType> commentType     = new MutableLiveData<>(CommentType.COMMENT);
    
    private final MutableLiveData<String> snackbar = new MutableLiveData<>( );
    
    private final PostRepository    postRepository;
    private final AccountRepository accountRepository;
    
    private LoginAccount account;
    
    private boolean likeChanged = false;
    
    @Inject
    public PostViewModel(PostRepository postRepository, AccountRepository accountRepository) {
        this.postRepository    = postRepository;
        this.accountRepository = accountRepository;
        loadAccount( );
    }
    
    public void loadPost(Long postId) {
        addDisposable(
                postRepository
                        .viewPost(postId)
                        .subscribeOn(Schedulers.io( ))
                        .observeOn(AndroidSchedulers.mainThread( ))
                        .doOnSuccess(post -> postFlattenComments(post.getComments( )))
                        .subscribe(post::postValue, e -> Log.e("ERROR", "refreshPost", e))
        );
    }
    
    
    public void deletePost(Callback<Empty> callback) {
        subscribe(callback, postRepository.deletePost(post.getValue( ).getId( )));
    }
    
    public void createComment(Consumer<Comment> onSuccess, Consumer<Throwable> onError) {
        String content = commentContent.getValue( ).trim( );
        if (! isValidComment(content)) {
            return;
        }
        var request = new CreateCommentRequest(content, post.getValue( ).getId( ), getParentId( ));
        addDisposable(
                postRepository
                        .createComment(request)
                        .subscribeOn(Schedulers.io( ))
                        .observeOn(AndroidSchedulers.mainThread( ))
                        .doOnSuccess(unused -> commentContent.postValue(""))
                        .subscribe(onSuccess, onError)
        );
    }
    
    
    public void updateComment(Consumer<Empty> onSuccess, Consumer<Throwable> onError) {
        String content = commentContent.getValue( ).trim( );
        if (! isValidComment(content)) {
            return;
        }
        addDisposable(
                postRepository
                        .updateComment(commentSelected.getValue( ).getId( ), content)
                        .subscribeOn(Schedulers.io( ))
                        .observeOn(AndroidSchedulers.mainThread( ))
                        .doOnSuccess(unused -> commentContent.postValue(""))
                        .subscribe(onSuccess, onError)
        );
    }
    
    public void deleteComment(Long commentId) {
        addDisposable(
                postRepository
                        .deleteComment(commentId)
                        .subscribeOn(Schedulers.io( ))
                        .observeOn(AndroidSchedulers.mainThread( ))
                        .subscribe(
                                empty -> {
                                    snackbar.postValue("댓글을 삭제했어요");
                                    reloadComments( );
                                },
                                e -> {
                                    snackbar.postValue("문제가 생겨 댓글을 삭제하지 못했어요");
                                    Log.e("PostViewModel", "deleteComment", e);
                                }
                        )
        );
        
    }
    
    public void reloadComment(int position) {
        Comment comment = flattenComments.getValue( ).get(position);
        addDisposable(
                postRepository
                        .getCommentsOnPost(post.getValue( ).getId( ))
                        .subscribeOn(Schedulers.io( ))
                        .observeOn(AndroidSchedulers.mainThread( ))
                        .subscribe(this::postFlattenComments, e -> snackbar.postValue("댓글을 불러오는 도중 문제가 발생했습니다"))
        );
    }
    
    public void reloadComments( ) {
        addDisposable(
                postRepository
                        .getCommentsOnPost(post.getValue( ).getId( ))
                        .subscribeOn(Schedulers.io( ))
                        .observeOn(AndroidSchedulers.mainThread( ))
                        .subscribe(this::postFlattenComments, e -> snackbar.postValue("댓글을 불러오는 도중 문제가 발생했습니다"))
        );
    }
    
    private void like( ) {
        addDisposable(
                postRepository
                        .like(this.post.getValue( ).getId( ))
                        .subscribeOn(Schedulers.io( ))
                        .observeOn(AndroidSchedulers.mainThread( ))
                        .subscribe(
                                like -> { },
                                e -> Log.e("ERROR", "likeByPosition", e)
                        )
        );
    }
    
    private void unlike( ) {
        addDisposable(
                postRepository
                        .unlike(this.post.getValue( ).getId( ))
                        .subscribeOn(Schedulers.io( ))
                        .observeOn(AndroidSchedulers.mainThread( ))
                        .subscribe(
                                like -> { },
                                e -> Log.e("ERROR", "unlike", e)
                        )
        );
    }
    
    public void likeComment(Long commentId, Consumer<Like> onSuccess) {
        addDisposable(
                postRepository
                        .likeComment(commentId)
                        .subscribeOn(Schedulers.io( ))
                        .observeOn(AndroidSchedulers.mainThread( ))
                        .subscribe(
                                onSuccess,
                                e -> Log.e("ERROR", "likeByPosition", e)
                        )
        );
    }
    
    public void unlikeComment(Long commentId, Consumer<Like> onSuccess) {
        addDisposable(
                postRepository
                        .unlikeComment(commentId)
                        .subscribeOn(Schedulers.io( ))
                        .observeOn(AndroidSchedulers.mainThread( ))
                        .subscribe(
                                onSuccess,
                                e -> Log.e("ERROR", "likeByPosition", e)
                        )
        );
    }
    
    public void loadAccount( ) {
        addDisposable(
                accountRepository
                        .loadAccount( )
                        .subscribeOn(Schedulers.io( ))
                        .observeOn(AndroidSchedulers.mainThread( ))
                        .subscribe(
                                loginAccount -> this.account = loginAccount,
                                e -> snackbar.postValue("사용자 정보를 불러오는 도중 문제가 발생했습니다")
                        )
        );
    }
    
    private void postFlattenComments(List<Comment> nestedComments) {
        var flattenComments = new ArrayList<Comment>( );
        nestedComments.forEach(comment -> getChildrenComments(comment, flattenComments));
        flattenComments.forEach(comment -> comment.setEditable(account.getId( )));
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
            if (post.getValue( ).isLike( )) {
                like( );
            }
            else {
                unlike( );
            }
        }
    }
    
    private boolean isValidComment(String content) {
        if (StringUtils.hasText(content)) {
            return true;
        }
        snackbar.postValue("댓글을 다시 확인해주세요");
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
    
    public void setSnackbar(String message) {
        snackbar.postValue(message);
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
