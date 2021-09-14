package com.app.bitwit.data.repository;

import android.annotation.SuppressLint;
import androidx.lifecycle.LiveData;
import com.app.bitwit.data.source.local.VoteItemDao;
import com.app.bitwit.data.source.local.entity.VoteItem;
import com.app.bitwit.data.source.remote.VoteServiceClient;
import com.app.bitwit.domain.Vote;
import com.app.bitwit.util.HttpUtils;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class VoteRepository {
    
    private final VoteServiceClient voteServiceClient;
    private final VoteItemDao       voteItemDao;
    
    public Single<Vote> getVote(Long voteId, String type) {
        return voteServiceClient
                .getVote(voteId, type)
                .map(HttpUtils::get2xxBody);
    }
    
    public Single<List<Vote>> getVotesByTicker(String ticker) {
        return voteServiceClient
                .searchVotes("vote-item", Collections.singletonList(ticker))
                .map(HttpUtils::get2xxBody);
    }
    
    public Single<List<Vote>> getVotes(String type) {
        return voteServiceClient
                .getVotes(type)
                .map(HttpUtils::get2xxBody);
    }
    
    public Completable refreshVoteItem(Long voteId) {
        return getVote(voteId, "vote-item")
                .map(VoteItem::fromVote)
                .map(VoteItem::enableAnim)
                .flatMapCompletable(voteItemDao::insertVote);
    }
    
    public Completable refreshVoteItems( ) {
        return getVotes("vote-item")
                .flattenAsObservable(responses -> responses)
                .map(VoteItem::fromVote)
                .toList( )
                .flatMapCompletable(voteItemDao::insertVotes);
    }
    
    public LiveData<VoteItem> loadVoteItem(Long voteItemId) {
        return voteItemDao.loadById(voteItemId);
    }
    
    public LiveData<VoteItem> loadLastEndedVoteByTicker(String ticker) {
        return voteItemDao.loadLastEndedVoteByTicker(ticker);
    }
    
    @SuppressLint("NewApi")
    public LiveData<List<VoteItem>> loadVoteItems( ) {
        return voteItemDao.loadAllBeforeEndedAt(LocalDateTime.now( ));
    }
    
    @SuppressLint("NewApi")
    public LiveData<List<VoteItem>> loadVoteItems(int sortOption, int sortDirection) {
        if (sortOption == Const.PRICE_FLUCTUATION) {
            if (sortDirection == Const.ASC) {
                return voteItemDao.loadAllBeforeEndedAtOrderByFluctuateRateAsc(LocalDateTime.now( ));
            }
            else if (sortDirection == Const.DESC) {
                return voteItemDao.loadAllBeforeEndedAtOrderByFluctuateRateDesc(LocalDateTime.now( ));
            }
        }
        else if (sortOption == Const.PRICE) {
            if (sortDirection == Const.ASC) {
                return voteItemDao.loadAllBeforeEndedAtOrderByPriceAsc(LocalDateTime.now( ));
            }
            else if (sortDirection == Const.DESC) {
                return voteItemDao.loadAllBeforeEndedAtOrderByPriceDesc(LocalDateTime.now( ));
            }
        }
        else if (sortOption == Const.PARTICIPANTS) {
            if (sortDirection == Const.ASC) {
                return voteItemDao.loadAllBeforeEndedAtOrderByParticipantsCountAsc(LocalDateTime.now( ));
            }
            else if (sortDirection == Const.DESC) {
                return voteItemDao.loadAllBeforeEndedAtOrderByParticipantsCountDesc(LocalDateTime.now( ));
            }
        }
        return voteItemDao.loadAllBeforeEndedAt(LocalDateTime.now( ));
    }
    
    @Getter
    @NoArgsConstructor(access = PRIVATE)
    public static class Const {
        public static final int NONE = - 1;
        
        public static final int DESC = 0;
        public static final int ASC  = 1;
        
        public static final int PRICE_FLUCTUATION = 0;
        public static final int PRICE             = 1;
        public static final int PARTICIPANTS      = 2;
    }
}
