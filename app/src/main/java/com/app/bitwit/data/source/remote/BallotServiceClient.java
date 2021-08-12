package com.app.bitwit.data.source.remote;

import com.app.bitwit.data.source.remote.dto.request.CreateOrChangeBallotRequest;
import com.app.bitwit.domain.Ballot;
import io.reactivex.rxjava3.core.Single;
import retrofit2.Response;
import retrofit2.http.*;

public interface BallotServiceClient
{
    @POST("/api/v1/ballots")
    Single<Response<Ballot>> createOrChangeBallot(@Body CreateOrChangeBallotRequest request);
}
