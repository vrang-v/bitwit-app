package com.app.bitwit.webclient;

import com.app.bitwit.domain.Ballot;
import com.app.bitwit.webclient.dto.request.BallotRequest;
import retrofit2.Call;
import retrofit2.http.*;

public interface BallotServiceClient
{
    @POST("/ballots")
    Call<Ballot> createBallot(@Body BallotRequest ballotRequest);
    
    @PATCH("/ballots/{ballotId}")
    Call<Void> updateBallot(@Path("ballotId") Long ballotId, @Body Ballot ballot);
    
    @DELETE("/ballots/{ballotId}")
    Call<Void> deleteBallot(@Path("ballotId") Long ballotId);
}
