package com.app.bitwit.data.source.remote;

import com.app.bitwit.domain.VoteResponse;
import io.reactivex.rxjava3.core.Single;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;

public interface VoteServiceClient
{
    @POST("/api/v1/votes")
    Call<Void> createVote(@Body VoteResponse voteResponse);
    
    @GET("/api/v1/votes/{voteId}")
    Single<Response<VoteResponse>> getVote(@Path("voteId") Long voteId);
    
    @GET("/api/v1/votes/{voteId}/type/{responseType}")
    Single<Response<VoteResponse>> getVote(@Path("voteId") Long voteId, @Path("responseType") String responseType);
    
    @GET("/api/v1/votes")
    Single<Response<List<VoteResponse>>> getVotes( );
    
    @GET("/api/v1/votes/type/{responseType}")
    Single<Response<List<VoteResponse>>> getVotes(@Path("responseType") String responseType);
}
