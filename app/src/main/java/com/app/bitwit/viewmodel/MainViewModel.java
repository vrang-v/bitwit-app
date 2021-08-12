package com.app.bitwit.viewmodel;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.app.bitwit.data.repository.AccountRepository;
import com.app.bitwit.data.repository.BallotRepository;
import com.app.bitwit.data.repository.VoteRepository;
import com.app.bitwit.data.source.local.entity.VoteItem;
import com.app.bitwit.data.source.remote.dto.request.CreateOrChangeBallotRequest;
import com.app.bitwit.domain.Ballot;
import com.app.bitwit.util.LoginAccount;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.Getter;
import lombok.var;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Getter
@HiltViewModel
public class MainViewModel extends DisposableViewModel {
    
    private final AccountRepository accountRepository;
    private final VoteRepository    voteRepository;
    private final BallotRepository  ballotRepository;
    
    private final MutableLiveData<LoginAccount> account = new MutableLiveData<>( );
    
    private final LiveData<List<VoteItem>> voteItems;
    
    @Inject
    public MainViewModel(VoteRepository voteRepository, AccountRepository accountRepository, BallotRepository ballotRepository) {
        this.voteRepository    = voteRepository;
        this.accountRepository = accountRepository;
        this.ballotRepository  = ballotRepository;
        this.voteItems         = voteRepository.loadVoteItems( );
    }
    
    public void refreshVoteItem(int position) {
        var voteId = voteItems.getValue( ).get(position).getId( );
        addDisposable(
                voteRepository
                        .refreshVoteItem(voteId)
                        .subscribeOn(Schedulers.io( ))
                        .observeOn(AndroidSchedulers.mainThread( ))
                        .subscribe(( ) -> { }, e -> Log.e("ERROR", "refreshVoteItem", e))
        );
    }
    
    public void refreshVoteItemsSchedule(long refreshInterval) {
        addDisposable(
                Observable
                        .interval(0L, refreshInterval, TimeUnit.SECONDS)
                        .flatMapCompletable(event -> voteRepository.refreshVoteItems( ))
                        .subscribeOn(Schedulers.io( ))
                        .observeOn(AndroidSchedulers.mainThread( ))
                        .subscribe(( ) -> { }, e -> Log.e("ERROR", "refreshVoteItemsSchedule", e))
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
    
    public void loadAccount( ) {
        addDisposable(
                accountRepository
                        .loadAccount( )
                        .subscribeOn(Schedulers.io( ))
                        .observeOn(AndroidSchedulers.mainThread( ))
                        .subscribe(account::postValue, e -> Log.e("ERROR", "loadAccount:", e))
        );
    }
}
