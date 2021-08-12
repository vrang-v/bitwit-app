package com.app.bitwit.data.source.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.app.bitwit.data.source.local.entity.VoteItem;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

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
    LiveData<List<VoteItem>> loadAll( );
    
    @Query("select count(*) from VoteItem")
    Single<Long> count( );
}
