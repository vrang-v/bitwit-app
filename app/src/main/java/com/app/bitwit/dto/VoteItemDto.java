package com.app.bitwit.dto;

import android.graphics.Color;
import android.util.AndroidRuntimeException;
import com.app.bitwit.R;
import com.app.bitwit.data.source.local.entity.VoteItem;
import com.app.bitwit.constant.Colors;
import lombok.Data;
import lombok.experimental.Accessors;

import java.text.DecimalFormat;
import java.util.Locale;

@Accessors(chain = true)
@Data
public class VoteItemDto {
    
    DecimalFormat decimalFormat = new DecimalFormat("###,###");
    
    boolean participated;
    String  status;
    String  tag;
    String  participantCount;
    String  ticker;
    String  koreanName;
    String  fluctuateRate;
    String  currentPrice;
    String  realTimeFluctuation;
    String  incrementRate;
    String  decrementRate;
    float   incrementBarWeight;
    float   decrementBarWeight;
    int     stockTextColor;
    int     voteTextColor;
    int     fluctuationTextColor;
    int     background;
    
    String currentPriceString;
    
    public VoteItemDto(VoteItem voteItem) {
        this.participated     = voteItem.isParticipated( );
        this.tag              = "급상승";
        this.participantCount = String.valueOf(voteItem.getParticipantsCount( ));
        this.ticker           = voteItem.getTicker( );
        this.koreanName       = voteItem.getKoreanName( );
        this.fluctuateRate    = voteItem.getFluctuateRate( ) >= 0
                                ? String.format(Locale.getDefault( ), "+%.2f%%", voteItem.getFluctuateRate( ))
                                : String.format(Locale.getDefault( ), "%.2f%%", voteItem.getFluctuateRate( ));
        this.stockTextColor   = voteItem.getFluctuateRate( ) > 0 ? Colors.PRIMARY_RED : Colors.PRIMARY_BLUE;
        this.currentPrice     = voteItem.getCurrentStockPriceString( );
        float incrementWeight = (float)voteItem.getIncrementChoiceCount( ) / voteItem.getParticipantsCount( );
        this.incrementRate      = String.format(Locale.getDefault( ), "%.2f", incrementWeight * 100) + "%";
        this.decrementRate      = String.format(Locale.getDefault( ), "%.2f", (1 - incrementWeight) * 100) + "%";
        this.incrementBarWeight = incrementWeight;
        this.decrementBarWeight = 1 - incrementWeight;
        
        if (voteItem.getRealTimeFluctuation( ) > 0) {
            this.realTimeFluctuation  = "+" + decimalFormat.format(voteItem.getRealTimeFluctuation( ));
            this.fluctuationTextColor = Colors.PRIMARY_RED;
        }
        else if (voteItem.getRealTimeFluctuation( ) < 0) {
            this.realTimeFluctuation  = decimalFormat.format(voteItem.getRealTimeFluctuation( ));
            this.fluctuationTextColor = Colors.PRIMARY_BLUE;
        }
        else {
            this.realTimeFluctuation  = "0";
            this.fluctuationTextColor = Colors.GRAY;
        }
        
        switch (getStatus(voteItem)) {
            case NOT_PARTICIPATE:
                this.background = R.drawable.round_corner_gray;
                this.voteTextColor = Color.BLACK;
                this.status = "투표 미참여";
                break;
            
            case INCREMENT:
                this.background = R.drawable.round_corner_pink;
                this.voteTextColor = Colors.PRIMARY_RED;
                this.status = "매수 우세";
                break;
            
            case DECREMENT:
                this.background = R.drawable.round_corner_blue;
                this.voteTextColor = Colors.PRIMARY_BLUE;
                this.status = "매도 우세";
                break;
            
            case MAINTAIN:
                this.background = R.drawable.round_corner_yel;
                this.voteTextColor = Color.BLACK;
                this.status = "중립";
                break;
            
            default:
                throw new AndroidRuntimeException("실행될 수 없는 코드");
        }
    }
    
    private VoteStatus getStatus(VoteItem voteItem) {
        if (! voteItem.isParticipated( )) {
            return VoteStatus.NOT_PARTICIPATE;
        }
        int incrementChoiceCount = voteItem.getIncrementChoiceCount( );
        int decrementChoiceCount = voteItem.getDecrementChoiceCount( );
        if (incrementChoiceCount > decrementChoiceCount) {
            return VoteStatus.INCREMENT;
        }
        else if (incrementChoiceCount < decrementChoiceCount) {
            return VoteStatus.DECREMENT;
        }
        else {
            return VoteStatus.MAINTAIN;
        }
    }
    
    private enum VoteStatus {
        NOT_PARTICIPATE, INCREMENT, DECREMENT, MAINTAIN
    }
}
