package com.app.bitwit.data.source.remote;

import com.app.bitwit.domain.Vote;
import io.reactivex.rxjava3.core.Single;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.*;

import java.util.List;

public interface VoteServiceClient {
    
    @POST("/api/votes")
    Call<Void> createVote(@Body Vote vote);
    
    @GET("/api/votes/{voteId}")
    Single<Response<Vote>> getVote(@Path("voteId") Long voteId);
    
    @GET("/api/votes/{voteId}/type/{responseType}")
    Single<Response<Vote>> getVote(@Path("voteId") Long voteId, @Path("responseType") String responseType);
    
    @GET("/api/votes")
    Single<Response<List<Vote>>> getVotes( );
    
    @GET("/api/votes/type/{responseType}")
    Single<Response<List<Vote>>> getVotes(@Path("responseType") String responseType);
    
    @GET("/api/votes/search/type/{responseType}")
    Single<Response<List<Vote>>> searchVotes(@Path("responseType") String responseType, @Query("ticker") List<String> tickers);
}
