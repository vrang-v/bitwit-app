package com.app.bitwit.viewmodel;

import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import com.app.bitwit.data.repository.AccountRepository;
import com.app.bitwit.data.repository.BallotRepository;
import com.app.bitwit.data.repository.VoteRepository;
import com.app.bitwit.data.source.local.entity.VoteItem;
import com.app.bitwit.data.source.remote.dto.request.CreateOrChangeBallotRequest;
import com.app.bitwit.domain.Ballot;
import com.app.bitwit.util.LoginAccount;
import com.app.bitwit.util.MutableObserver;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.var;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.app.bitwit.viewmodel.MainViewModel.Const.*;
import static java.lang.Boolean.FALSE;
import static lombok.AccessLevel.PRIVATE;

@Getter
@HiltViewModel
public class MainViewModel extends DisposableViewModel {
    
    private final AccountRepository accountRepository;
    private final VoteRepository    voteRepository;
    private final BallotRepository  ballotRepository;
    
    private final MutableLiveData<LoginAccount> account           = new MutableLiveData<>( );
    private final MutableLiveData<Boolean>      filterViewVisible = new MutableLiveData<>(FALSE);
    private final MutableLiveData<Integer>      sortOption        = new MutableLiveData<>(- 1);
    private final MutableLiveData<Integer>      sortDirection     = new MutableLiveData<>(- 1);
    private final MutableLiveData<String>       sortStatus        = new MutableLiveData<>("");
    
    private final MutableLiveData<List<VoteItem>> voteItems = new MutableLiveData<>( );
    
    private final MutableObserver<List<VoteItem>> voteItemsObserver = new MutableObserver<>(voteItems::postValue);
    
    
    @Inject
    public MainViewModel(VoteRepository voteRepository, AccountRepository accountRepository, BallotRepository ballotRepository) {
        this.voteRepository    = voteRepository;
        this.accountRepository = accountRepository;
        this.ballotRepository  = ballotRepository;
        voteItemsObserver.observe(voteRepository.loadVoteItems( ));
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
    
    public void loadVote(int sortOption, int sortDirection) {
        if (sortOption * sortDirection < 0) {
            return;
        }
        voteItemsObserver.observe(voteRepository.loadVoteItems(sortOption, sortDirection));
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
    
    public void setFilterViewVisibility(boolean isVisible) {
        filterViewVisible.postValue(isVisible);
    }
    
    public void changeFilterViewVisibility( ) {
        filterViewVisible.postValue(FALSE.equals(filterViewVisible.getValue( )));
    }
    
    public void setSortStatus(int sortOption, int sortDirection) {
        if (sortOption == NONE || sortDirection == NONE) {
            sortStatus.postValue("");
            return;
        }
        StringBuilder status = new StringBuilder( );
        switch (sortOption) {
            case PRICE_FLUCTUATION:
                status.append("가격 변동");
                break;
            case PRICE:
                status.append("가격");
                break;
            case PARTICIPANTS:
                status.append("투표 인원");
                break;
            default:
        }
        status.append(" ");
        switch (sortDirection) {
            case ASC:
                status.append("낮은 순");
                break;
            case DESC:
                status.append("높은 순");
                break;
            default:
        }
        sortStatus.postValue(status.toString( ));
    }
    
    public void postSortOption(int option) {
        if (Objects.equals(sortOption.getValue( ), option)) {
            sortOption.postValue(NONE);
            return;
        }
        sortOption.postValue(option);
    }
    
    public void postSortDirection(int direction) {
        if (Objects.equals(sortDirection.getValue( ), direction)) {
            sortDirection.postValue(NONE);
            return;
        }
        sortDirection.postValue(direction);
    }
    
    @Override
    protected void onCleared( ) {
        voteItemsObserver.dispose( );
        super.onCleared( );
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
