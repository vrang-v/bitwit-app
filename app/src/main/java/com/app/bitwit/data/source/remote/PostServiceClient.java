package com.app.bitwit.data.source.remote;

import com.app.bitwit.domain.Post;
import io.reactivex.rxjava3.core.Single;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;

public interface PostServiceClient {
    
    @GET("/api/posts/search")
    Single<Response<List<Post>>> searchPostsByTickers(@Query("ticker") List<String> tickers);
    
}
