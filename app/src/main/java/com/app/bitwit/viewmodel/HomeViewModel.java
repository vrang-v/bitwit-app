package com.app.bitwit.viewmodel;

import androidx.lifecycle.MutableLiveData;
import com.app.bitwit.data.repository.AccountRepository;
import com.app.bitwit.data.repository.BallotRepository;
import com.app.bitwit.data.repository.VoteRepository;
import com.app.bitwit.data.source.local.entity.VoteItem;
import com.app.bitwit.data.source.remote.dto.request.CreateBallotRequest;
import com.app.bitwit.domain.Ballot;
import com.app.bitwit.util.*;
import com.app.bitwit.util.subscription.Subscription;
import com.app.bitwit.viewmodel.common.RxJavaViewModelSupport;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.Observable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.var;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.app.bitwit.viewmodel.HomeViewModel.Const.*;
import static java.lang.Boolean.FALSE;
import static lombok.AccessLevel.PRIVATE;

@Getter
@HiltViewModel
public class HomeViewModel extends RxJavaViewModelSupport implements SnackbarViewModel {
    
    private final AccountRepository accountRepository;
    private final VoteRepository    voteRepository;
    private final BallotRepository  ballotRepository;
    
    private final MutableLiveData<LoginAccount> account           = new MutableLiveData<>( );
    private final MutableLiveData<Boolean>      filterViewVisible = new MutableLiveData<>(FALSE);
    private final MutableLiveData<Integer>      sortOption        = new MutableLiveData<>(- 1);
    private final MutableLiveData<Integer>      sortDirection     = new MutableLiveData<>(- 1);
    private final MutableLiveData<String>       sortStatus        = new MutableLiveData<>("");
    
    private final ChangeableLiveData<List<VoteItem>> voteItems = new ChangeableLiveData<>( );
    
    @Inject
    public HomeViewModel(VoteRepository voteRepository, AccountRepository accountRepository, BallotRepository ballotRepository) {
        this.voteRepository    = voteRepository;
        this.accountRepository = accountRepository;
        this.ballotRepository  = ballotRepository;
        voteItems.changeSource(voteRepository.loadVoteItems( ));
    }
    
    public Subscription<Void> refreshVoteItem(int position) {
        var voteId = voteItems.getValue( ).get(position).getId( );
        return subscribe(
                voteRepository.getVoteItem(voteId, "vote-item")
        );
    }
    
    public Subscription<Void> refreshVoteItemsSchedule(long refreshInterval) {
        return subscribe(
                Observable.interval(0L, refreshInterval, TimeUnit.SECONDS)
                          .flatMapCompletable(event -> voteRepository.refreshVoteItems( ))
        );
    }
    
    public Subscription<Ballot> createBallot(CreateBallotRequest request) {
        return subscribe(
                ballotRepository.createOrChangeBallot(request)
        );
    }
    
    public Subscription<LoginAccount> loadAccount( ) {
        return subscribe(
                accountRepository.loadAccount( )
                                 .doOnSuccess(account::postValue)
        );
    }
    
    public void loadVotes(int sortOption, int sortDirection) {
        if (sortOption * sortDirection < 0) {
            return;
        }
        voteItems.changeSource(voteRepository.loadVoteItems(sortOption, sortDirection));
    }
    
    public void setFilterViewVisibility(boolean isVisible) {
        filterViewVisible.postValue(isVisible);
    }
    
    public void changeFilterViewVisibility( ) {
        filterViewVisible.postValue(Boolean.FALSE.equals(filterViewVisible.getValue( )));
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
    
    public void setSortOption(int option) {
        if (Objects.equals(sortOption.getValue( ), option)) {
            sortOption.postValue(NONE);
            return;
        }
        sortOption.postValue(option);
    }
    
    public void setSortDirection(int direction) {
        if (Objects.equals(sortDirection.getValue( ), direction)) {
            sortDirection.postValue(NONE);
            return;
        }
        sortDirection.postValue(direction);
    }
    
    public void invertSortDirection( ) {
        sortDirection.postValue(sortDirection.getValue( ) == ASC ? DESC : ASC);
    }
    
    @Override
    protected void onCleared( ) {
//        voteItemsObserveDelegate.dispose( );
        voteItems.dispose( );
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
