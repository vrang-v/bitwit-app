package com.app.bitwit.viewmodel;

import android.annotation.SuppressLint;
import androidx.lifecycle.MutableLiveData;
import com.app.bitwit.data.repository.BallotRepository;
import com.app.bitwit.data.repository.StockRepository;
import com.app.bitwit.data.repository.VoteRepository;
import com.app.bitwit.data.source.local.entity.VoteItem;
import com.app.bitwit.data.source.remote.dto.request.CreateBallotRequest;
import com.app.bitwit.data.source.remote.dto.request.SearchStockRequest;
import com.app.bitwit.domain.Ballot;
import com.app.bitwit.domain.Stock;
import com.app.bitwit.domain.Vote;
import com.app.bitwit.util.subscription.SingleSubscription;
import com.app.bitwit.util.subscription.Subscription;
import com.app.bitwit.dto.SearchItem;
import com.app.bitwit.viewmodel.common.RxJavaViewModelSupport;
import dagger.hilt.android.lifecycle.HiltViewModel;
import lombok.Getter;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
@HiltViewModel
public class SearchActivityViewModel extends RxJavaViewModelSupport {
    
    private final VoteRepository   voteRepository;
    private final StockRepository  stockRepository;
    private final BallotRepository ballotRepository;
    
    private final MutableLiveData<String> searchWord = new MutableLiveData<>( );
    
    private final MutableLiveData<List<Stock>> stocks = new MutableLiveData<>( );
    
    private final MutableLiveData<List<SearchItem>> searchItems          = new MutableLiveData<>( );
    private final MutableLiveData<VoteItem>         clickedStockVoteItem = new MutableLiveData<>( );
    
    @Inject
    public SearchActivityViewModel(VoteRepository voteRepository, StockRepository stockRepository, BallotRepository ballotRepository) {
        this.voteRepository   = voteRepository;
        this.stockRepository  = stockRepository;
        this.ballotRepository = ballotRepository;
    }
    
    public Subscription<List<SearchItem>> search(String keyword) {
        if (keyword.isEmpty( )) {
            stocks.postValue(new ArrayList<>( ));
            return unsubscribe( );
        }
        
        return subscribe(
                stockRepository
                        .searchStock(new SearchStockRequest(keyword))
                        .map(SearchItem::fromStocks)
                        .doOnSuccess(searchItems::postValue)
        );
    }
    
    @SuppressLint("NewApi")
    public SingleSubscription<VoteItem> getLastVoteByTicker(String ticker) {
        return subscribe(
                voteRepository
                        .getVotesByTicker("vote-item", ticker)
                        .map(votes -> {
                            votes.sort(Comparator.comparing(Vote::getEndedAt).reversed( ));
                            return votes.get(0);
                        })
                        .map(VoteItem::fromVote)
        );
    }
    
    public Subscription<Ballot> createBallot(CreateBallotRequest request) {
        return subscribe(ballotRepository.createOrChangeBallot(request));
    }
    
    public Subscription<Void> refreshVoteItem(Long voteId) {
        return subscribe(voteRepository.getVoteItem(voteId, "vote-item"));
    }
}
