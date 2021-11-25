package com.app.bitwit.data.source.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.app.bitwit.domain.Vote;
import com.app.bitwit.domain.VotingOption;
import com.app.bitwit.constant.Colors;
import lombok.Data;
import lombok.var;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Locale;

@Data
@Entity
public class VoteItem {
    
    @PrimaryKey
    Long id;
    
    boolean applyAnim;
    
    String ticker;
    
    String koreanName;
    
    String description;
    
    Double currentStockPrice;
    
    Double realTimeFluctuation;
    
    Double fluctuateRate;
    
    LocalDateTime startAt;
    
    LocalDateTime endedAt;
    
    int participantsCount;
    
    boolean participated;
    
    int incrementChoiceCount;
    
    int decrementChoiceCount;
    
    public static VoteItem fromVote(Vote vote) {
        VoteItem voteItem = new VoteItem( );
        voteItem.applyAnim           = false;
        voteItem.id                  = vote.getId( );
        voteItem.ticker              = vote.getStock( ).getTicker( );
        voteItem.koreanName          = vote.getStock( ).getKoreanName( );
        voteItem.description         = vote.getDescription( );
        voteItem.currentStockPrice   = vote.getStock( ).getCurrentPrice( );
        voteItem.realTimeFluctuation = vote.getStock( ).getRealTimeFluctuation( );
        voteItem.fluctuateRate       = vote.getStock( ).getFluctuateRate24h( );
        voteItem.startAt             = vote.getStartAt( );
        voteItem.endedAt             = vote.getEndedAt( );
        voteItem.participantsCount   = vote.getParticipantsCount( );
        
        voteItem.participated = vote.getSelectionCount( ) != null;
        
        if (voteItem.participated) {
            var incrementChoiceCount = vote.getSelectionCount( ).get(VotingOption.INCREMENT);
            voteItem.incrementChoiceCount = incrementChoiceCount == null ? 0 : incrementChoiceCount;
            
            var decrementChoiceCount = vote.getSelectionCount( ).get(VotingOption.DECREMENT);
            voteItem.decrementChoiceCount = decrementChoiceCount == null ? 0 : decrementChoiceCount;
        }
        else {
            voteItem.incrementChoiceCount = - 1;
            voteItem.decrementChoiceCount = - 1;
        }
        return voteItem;
    }
    
    public VoteItem enableAnim( ) {
        this.applyAnim = true;
        return this;
    }
    
    public float getIncrementBarWeight( ) {
        return (float)incrementChoiceCount / participantsCount;
    }
    
    public String getIncrementRateString( ) {
        return String.format(Locale.getDefault( ), "%.2f%%", getIncrementBarWeight( ) * 100);
    }
    
    public float getDecrementBarWeight( ) {
        return 1 - ((float)incrementChoiceCount / participantsCount);
    }
    
    public String getDecrementRateString( ) {
        return String.format(Locale.getDefault( ), "%.2f%%", (1 - getIncrementBarWeight( )) * 100);
    }
    
    public String getCurrentStockPriceString( ) {
        if (currentStockPrice < 100) {
            return String.format(Locale.getDefault( ), "%.2f 원", currentStockPrice);
        }
        DecimalFormat decimalFormat = new DecimalFormat("###,###");
        return decimalFormat.format(currentStockPrice) + " 원";
    }
    
    public String getFluctuateRateString( ) {
        return fluctuateRate >= 0
               ? String.format(Locale.getDefault( ), "+%.2f%%", fluctuateRate)
               : String.format(Locale.getDefault( ), "%.2f%%", fluctuateRate);
    }
    
    public int getStockTextColor( ) {
        return fluctuateRate > 0 ? Colors.PRIMARY_RED : Colors.PRIMARY_BLUE;
    }
}
