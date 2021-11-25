package com.app.bitwit.data.repository;

import com.app.bitwit.data.source.remote.BallotServiceClient;
import com.app.bitwit.data.source.remote.dto.request.CreateBallotRequest;
import com.app.bitwit.domain.Ballot;
import com.app.bitwit.util.HttpUtils;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class BallotRepository {
    
    private final BallotServiceClient ballotServiceClient;
    
    public Single<Ballot> createOrChangeBallot(CreateBallotRequest request) {
        return ballotServiceClient
                .createOrChangeBallot(request)
                .map(HttpUtils::get2xxBody);
    }
}
