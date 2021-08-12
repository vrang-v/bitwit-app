package com.app.bitwit.data.repository;

import androidx.lifecycle.LiveData;
import com.app.bitwit.data.source.local.VoteItemDao;
import com.app.bitwit.data.source.local.entity.VoteItem;
import com.app.bitwit.data.source.remote.VoteServiceClient;
import com.app.bitwit.domain.VoteResponse;
import com.app.bitwit.util.HttpUtils;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import lombok.AllArgsConstructor;

import javax.inject.Inject;
import java.util.List;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class VoteRepository {
    
    private final VoteServiceClient voteServiceClient;
    private final VoteItemDao       voteItemDao;
    
    public Single<VoteResponse> getVote(Long voteId, String type) {
        return voteServiceClient
                .getVote(voteId, type)
                .map(HttpUtils::get2xxBody);
    }
    
    public Single<List<VoteResponse>> getVotes(String type) {
        return voteServiceClient
                .getVotes(type)
                .map(HttpUtils::get2xxBody);
    }
    
    public Completable refreshVoteItem(Long voteId) {
        return getVote(voteId, "vote-item")
                .map(VoteItem::fromVoteResponse)
                .map(VoteItem::enableAnim)
                .flatMapCompletable(voteItemDao::insertVote);
    }
    
    public Completable refreshVoteItems( ) {
        return getVotes("vote-item")
                .flattenAsObservable(responses -> responses)
                .map(VoteItem::fromVoteResponse)
                .toList()
                .flatMapCompletable(voteItemDao::insertVotes);
    }
    
    public LiveData<VoteItem> loadVoteItem(Long voteItemId) {
        return voteItemDao.loadById(voteItemId);
    }
    
    public LiveData<List<VoteItem>> loadVoteItems( ) {
        return voteItemDao.loadAll( );
    }
}
