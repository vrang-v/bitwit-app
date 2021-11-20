package com.app.bitwit.data.repository;

import com.app.bitwit.data.source.remote.PostServiceClient;
import com.app.bitwit.data.source.remote.dto.CreateCommentRequest;
import com.app.bitwit.data.source.remote.dto.request.CreatePostRequest;
import com.app.bitwit.data.source.remote.dto.request.UpdateCommentRequest;
import com.app.bitwit.domain.Comment;
import com.app.bitwit.domain.Like;
import com.app.bitwit.domain.Post;
import com.app.bitwit.util.Empty;
import com.app.bitwit.util.HttpUtils;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import java.util.List;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class PostRepository {
    
    private final PostServiceClient postServiceClient;
    
    public Single<Post> createPost(CreatePostRequest request) {
        return postServiceClient
                .createPost(request)
                .map(HttpUtils::get2xxBody);
    }
    
    public Single<List<Post>> searchPostByTickers(List<String> tickers, int size) {
        return postServiceClient
                .searchPostsByTickers(tickers, size)
                .map(HttpUtils::get2xxBody);
    }
    
    public Single<List<Post>> searchPostPageByKeyword(String keyword, int page, int size) {
        return postServiceClient
                .searchPostPageByKeyword(keyword, page, size)
                .map(HttpUtils::get2xxBody);
    }
    
    public Single<Post> getPost(Long postId) {
        return postServiceClient
                .getPost(postId)
                .map(HttpUtils::get2xxBody);
    }
    
    public Single<Post> viewPost(Long postId) {
        return postServiceClient
                .viewPost(postId)
                .map(HttpUtils::get2xxBody);
    }
    
    public Single<Empty> deletePost(Long postId) {
        return postServiceClient
                .deletePost(postId)
                .map(HttpUtils::get2xxEmptyBody);
    }
    
    public Single<List<Comment>> getCommentsOnPost(Long postId) {
        return postServiceClient
                .getCommentsOnPost(postId)
                .map(HttpUtils::get2xxBody);
    }
    
    public Single<Like> like(Long postId) {
        return postServiceClient
                .like(postId)
                .map(HttpUtils::get2xxBody);
    }
    
    public Single<Like> unlike(Long postId) {
        return postServiceClient
                .unlike(postId)
                .map(HttpUtils::get2xxBody);
    }
    
    public Single<Comment> createComment(CreateCommentRequest request) {
        return postServiceClient
                .createComment(request)
                .map(HttpUtils::get2xxBody);
    }
    
    public Single<Empty> updateComment(Long commentId, String content) {
        return postServiceClient
                .updateComment(commentId, new UpdateCommentRequest(content))
                .map(HttpUtils::get2xxEmptyBody);
    }
    
    public Single<Empty> deleteComment(Long commentId) {
        return postServiceClient
                .deleteComment(commentId)
                .map(HttpUtils::get2xxEmptyBody);
    }
    
    public Single<Like> likeComment(Long commentId) {
        return postServiceClient
                .likeComment(commentId)
                .map(HttpUtils::get2xxBody);
    }
    
    public Single<Like> unlikeComment(Long commentId) {
        return postServiceClient
                .unlikeComment(commentId)
                .map(HttpUtils::get2xxBody);
    }
    
    public Single<List<Post>> getMostRecentlyPostPage(int page, int size) {
        return postServiceClient
                .getPostPageOrderByCreatedAtDesc(page, size)
                .map(HttpUtils::get2xxBody);
    }
    
    public Single<List<Post>> getMostViewedPostPage(int page, int size) {
        return postServiceClient
                .getPostPageOrderByViewCountDesc(page, size)
                .map(HttpUtils::get2xxBody);
    }
}
