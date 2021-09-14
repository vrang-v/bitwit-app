package com.app.bitwit.viewmodel;

import android.annotation.SuppressLint;
import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import com.app.bitwit.adapter.SearchItem;
import com.app.bitwit.data.repository.BallotRepository;
import com.app.bitwit.data.repository.StockRepository;
import com.app.bitwit.data.repository.VoteRepository;
import com.app.bitwit.data.source.local.entity.VoteItem;
import com.app.bitwit.data.source.remote.dto.request.CreateOrChangeBallotRequest;
import com.app.bitwit.data.source.remote.dto.request.SearchStockRequest;
import com.app.bitwit.domain.Ballot;
import com.app.bitwit.domain.Stock;
import com.app.bitwit.domain.Vote;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.Getter;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
@HiltViewModel
public class SearchActivityViewModel extends DisposableViewModel {
    
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
    
    public void search(String keyword) {
        if (keyword.isEmpty( )) {
            stocks.postValue(new ArrayList<>( ));
            return;
        }
        
        addDisposable(
                stockRepository
                        .searchStock(new SearchStockRequest(keyword))
                        .subscribeOn(Schedulers.io( ))
                        .observeOn(AndroidSchedulers.mainThread( ))
                        .map(SearchItem::fromStocks)
                        .subscribe(searchItems::postValue, e -> Log.e("ERROR", "search", e))
        );
    }
    
    @SuppressLint("NewApi")
    public void getLastVoteByTicker(String ticker, Consumer<VoteItem> onSuccess) {
        addDisposable(
                voteRepository
                        .getVotesByTicker(ticker)
                        .map(votes -> {
                            votes.sort(Comparator.comparing(Vote::getEndedAt).reversed( ));
                            return votes.get(0);
                        })
                        .map(VoteItem::fromVote)
                        .subscribeOn(Schedulers.io( ))
                        .observeOn(AndroidSchedulers.mainThread( ))
                        .subscribe(onSuccess, e -> Log.e("SearchActivityViewModel", "getLastVoteByTicker", e))
        );
    }
    
    public void createOrChangeBallot(CreateOrChangeBallotRequest request, Consumer<Ballot> onSuccess) {
        addDisposable(
                ballotRepository
                        .createOrChangeBallot(request)
                        .subscribeOn(Schedulers.io( ))
                        .observeOn(AndroidSchedulers.mainThread( ))
                        .subscribe(onSuccess, e -> Log.e("ERROR", "createOrChangeBallot: ", e))
        );
    }
}
