package com.app.bitwit.webclient;

import com.app.bitwit.domain.Vote;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;

public interface VoteServiceClient
{
    @POST("/votes")
    Call<Void> createVote(@Body Vote vote);
    
    @GET("/votes/{voteId}")
    Call<Vote> getVote(@Path("voteId") Long voteId);
    
    @GET("/votes/{voteId}/type/{responseType}")
    Call<Vote> getVote(@Path("voteId") Long voteId, @Path("responseType") String responseType);
    
    @GET("/votes")
    Call<List<Vote>> getVotes( );
    
    @GET("/votes/type/{responseType}")
    Call<List<Vote>> getVotes(@Path("responseType") String responseType);
}
