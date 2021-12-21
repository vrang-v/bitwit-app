package com.app.bitwit.data.source.remote;

import com.app.bitwit.data.source.remote.dto.CreateCommentRequest;
import com.app.bitwit.data.source.remote.dto.request.CreatePostRequest;
import com.app.bitwit.data.source.remote.dto.request.UpdateCommentRequest;
import com.app.bitwit.data.source.remote.dto.request.UpdatePostRequest;
import com.app.bitwit.domain.Comment;
import com.app.bitwit.domain.Like;
import com.app.bitwit.domain.Post;
import com.app.bitwit.dto.Page;
import io.reactivex.rxjava3.core.Single;
import retrofit2.Response;
import retrofit2.http.*;

import java.util.List;

public interface PostServiceClient {
    
    @POST("/api/posts")
    Single<Response<Post>> createPost(@Body CreatePostRequest request);
    
    @PATCH("/api/posts/{postId}")
    Single<Response<Post>> updatePost(@Path("postId") Long postId, @Body UpdatePostRequest request);
    
    @GET("/api/posts/search")
    Single<Response<List<Post>>> searchPostsByTickers(@Query("ticker") List<String> tickers, @Query("size") int size);
    
    @GET("/api/posts/search")
    Single<Response<List<Post>>> searchPostPageByKeyword(@Query("keyword") String keyword, @Query("page") int page, @Query("size") int size);
    
    @GET("/api/posts/search?sort=createdAt,desc")
    Single<Response<List<Post>>> getPostPageOrderByCreatedAtDesc(@Query("page") int page, @Query("size") int size);
    
    @GET("/api/posts/search?sort=viewCount,desc")
    Single<Response<List<Post>>> getPostPageOrderByViewCountDesc(@Query("page") int page, @Query("size") int size);
    
    @GET("/api/posts/search?sort=createdAt,desc")
    Single<Response<List<Post>>> getPostPageByWriterNameOrderByCreatedAtDesc(@Query("writer") String name, @Query("page") int page, @Query("size") int size);
    
    @GET("/api/posts/search?sort=createdAt,desc")
    Single<Response<List<Post>>> getPostPageByLikerId(@Query("likerId") Long likerId, @Query("page") int page, @Query("size") int size);
    
    @GET("/api/posts/{postId}")
    Single<Response<Post>> getPost(@Path("postId") Long postId);
    
    @GET("/api/posts/{postId}/view")
    Single<Response<Post>> viewPost(@Path("postId") Long postId);
    
    @DELETE("/api/posts/{postId}")
    Single<Response<Void>> deletePost(@Path("postId") Long postId);
    
    @POST("/api/posts/{postId}/like")
    Single<Response<Like>> like(@Path("postId") Long postId);
    
    @DELETE("/api/posts/{postId}/like")
    Single<Response<Like>> unlike(@Path("postId") Long postId);
    
    @GET("/api/posts/{postId}/comments")
    Single<Response<List<Comment>>> getCommentsOnPost(@Path("postId") Long postId);
    
    @POST("/api/comments")
    Single<Response<Comment>> createComment(@Body CreateCommentRequest request);
    
    @GET("/api/comments/search/type/with-post?sort=createdAt,desc")
    Single<Response<Page<Comment>>> getCommentPageByWriterId(@Query("writerId") Long writerId, @Query("page") int page, @Query("size") int size);
    
    @GET("/api/comments/search/type/with-post?sort=createdAt,desc")
    Single<Response<Page<Comment>>> getCommentPageByLikerId(@Query("likerId") Long liker, @Query("page") int page, @Query("size") int size);
    
    @PATCH("/api/comments/{commentId}")
    Single<Response<Void>> updateComment(@Path("commentId") Long commentId, @Body UpdateCommentRequest request);
    
    @DELETE("/api/comments/{commentId}")
    Single<Response<Void>> deleteComment(@Path("commentId") Long commentId);
    
    @POST("/api/comments/{commentId}/like")
    Single<Response<Like>> likeComment(@Path("commentId") Long commentId);
    
    @DELETE("/api/comments/{commentId}/like")
    Single<Response<Like>> unlikeComment(@Path("commentId") Long commentId);
    
}
