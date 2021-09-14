package com.app.bitwit.data.source.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.app.bitwit.data.source.local.entity.VoteItem;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

import java.time.LocalDateTime;
import java.util.List;

@Dao
public interface VoteItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertVote(VoteItem voteItem);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertVotes(List<VoteItem> voteItems);
    
    @Query("select * from VoteItem where VoteItem.id = :voteId")
    LiveData<VoteItem> loadById(Long voteId);
    
    @Query("select * from VoteItem")
    LiveData<List<VoteItem>> loadAll();
    
    @Query("select * from VoteItem order by VoteItem.fluctuateRate asc")
    LiveData<List<VoteItem>> loadAllOrderByFluctuateRateAsc( );
    
    @Query("select * from VoteItem order by VoteItem.fluctuateRate desc")
    LiveData<List<VoteItem>> loadAllOrderByFluctuateRateDesc( );
    
    @Query("select * from VoteItem order by VoteItem.currentStockPrice asc")
    LiveData<List<VoteItem>> loadAllOrderByPriceAsc( );
    
    @Query("select * from VoteItem order by VoteItem.currentStockPrice desc")
    LiveData<List<VoteItem>> loadAllOrderByPriceDesc( );
    
    @Query("select * from VoteItem order by VoteItem.participantsCount asc")
    LiveData<List<VoteItem>> loadAllOrderByParticipantsCountAsc( );
    
    @Query("select * from VoteItem order by VoteItem.participantsCount desc")
    LiveData<List<VoteItem>> loadAllOrderByParticipantsCountDesc( );
    
    @Query("select * from VoteItem where VoteItem.endedAt >= :endedAt")
    LiveData<List<VoteItem>> loadAllBeforeEndedAt(LocalDateTime endedAt);
    
    @Query("select * from VoteItem where VoteItem.endedAt >= :endedAt order by VoteItem.fluctuateRate asc")
    LiveData<List<VoteItem>> loadAllBeforeEndedAtOrderByFluctuateRateAsc(LocalDateTime endedAt );
    
    @Query("select * from VoteItem where VoteItem.endedAt >= :endedAt order by VoteItem.fluctuateRate desc")
    LiveData<List<VoteItem>> loadAllBeforeEndedAtOrderByFluctuateRateDesc(LocalDateTime endedAt );
    
    @Query("select * from VoteItem where VoteItem.endedAt >= :endedAt order by VoteItem.currentStockPrice asc")
    LiveData<List<VoteItem>> loadAllBeforeEndedAtOrderByPriceAsc(LocalDateTime endedAt );
    
    @Query("select * from VoteItem where VoteItem.endedAt >= :endedAt order by VoteItem.currentStockPrice desc")
    LiveData<List<VoteItem>> loadAllBeforeEndedAtOrderByPriceDesc(LocalDateTime endedAt );
    
    @Query("select * from VoteItem where VoteItem.endedAt >= :endedAt order by VoteItem.participantsCount asc")
    LiveData<List<VoteItem>> loadAllBeforeEndedAtOrderByParticipantsCountAsc( LocalDateTime endedAt);
    
    @Query("select * from VoteItem where VoteItem.endedAt >= :endedAt order by VoteItem.participantsCount desc")
    LiveData<List<VoteItem>> loadAllBeforeEndedAtOrderByParticipantsCountDesc( LocalDateTime endedAt);
    
    @Query("select * from VoteItem where VoteItem.ticker = :ticker order by VoteItem.endedAt desc limit 1")
    LiveData<VoteItem> loadLastEndedVoteByTicker(String ticker);
    
    @Query("select count(*) from VoteItem")
    Single<Long> count( );
    
}
