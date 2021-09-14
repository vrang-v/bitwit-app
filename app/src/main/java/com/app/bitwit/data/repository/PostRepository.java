package com.app.bitwit.data.repository;

import com.app.bitwit.data.source.remote.PostServiceClient;
import com.app.bitwit.domain.Post;
import com.app.bitwit.util.HttpUtils;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import java.util.List;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class PostRepository {
    
    private final PostServiceClient postServiceClient;
    
    public Single<List<Post>> searchPostByTickers(List<String> tickers) {
        return postServiceClient
                .searchPostsByTickers(tickers)
                .map(HttpUtils::get2xxBody);
    }
}
