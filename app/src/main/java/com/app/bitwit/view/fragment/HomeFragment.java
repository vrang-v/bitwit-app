package com.app.bitwit.view.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import com.app.bitwit.R;
import com.app.bitwit.data.source.remote.dto.request.CreateBallotRequest;
import com.app.bitwit.databinding.FragmentHomeBinding;
import com.app.bitwit.view.activity.SearchActivity;
import com.app.bitwit.view.activity.StockInfoActivity;
import com.app.bitwit.view.adapter.VoteItemAdapter;
import com.app.bitwit.viewmodel.HomeViewModel;
import com.app.bitwit.viewmodel.HomeViewModel.Const;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemReselectedListener;
import dagger.hilt.android.AndroidEntryPoint;
import lombok.var;
import org.jetbrains.annotations.NotNull;

import static android.view.View.VISIBLE;
import static com.app.bitwit.constant.IntentKeys.STOCK_TICKER;
import static com.app.bitwit.constant.TransitionNames.*;
import static com.app.bitwit.domain.VotingOption.DECREMENT;
import static com.app.bitwit.domain.VotingOption.INCREMENT;
import static com.app.bitwit.util.livedata.LiveDataUtils.observeAllNotNull;
import static com.app.bitwit.view.adapter.VoteItemAdapter.VoteItemAdapterEvent.INCREMENT_BTN_CLICK;

@AndroidEntryPoint
public class HomeFragment extends Fragment implements OnNavigationItemReselectedListener {
    
    private FragmentHomeBinding binding;
    private HomeViewModel       viewModel;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        init(inflater, container);
        
        binding.logo.setOnClickListener(v ->
                initRecyclerViewPosition( )
        );
        binding.search.setOnClickListener(v -> {
            var intent = new Intent(getContext( ), SearchActivity.class);
            startActivity(intent);
        });
        binding.filter.setOnClickListener(v ->
                viewModel.changeFilterViewVisibility( )
        );
        binding.hideFilter.setOnClickListener(v ->
                viewModel.changeFilterViewVisibility( )
        );
        binding.refresh.setOnClickListener(v -> {
            viewModel.setSortOption(Const.NONE);
            viewModel.setSortDirection(Const.NONE);
        });
        binding.reset.setOnClickListener(v -> {
            viewModel.setSortOption(Const.NONE);
            viewModel.setSortDirection(Const.NONE);
        });
        binding.sortStatus.setOnClickListener(v ->
                viewModel.invertSortDirection( )
        );
        binding.fluctuation.setOnClickListener(v ->
                viewModel.setSortOption(Const.PRICE_FLUCTUATION)
        );
        binding.price.setOnClickListener(v ->
                viewModel.setSortOption(Const.PRICE)
        );
        binding.participants.setOnClickListener(v ->
                viewModel.setSortOption(Const.PARTICIPANTS)
        );
        binding.desc.setOnClickListener(v ->
                viewModel.setSortDirection(Const.DESC)
        );
        binding.asc.setOnClickListener(v ->
                viewModel.setSortDirection(Const.ASC)
        );
        
        observeAllNotNull(this, viewModel.getSortOption( ), viewModel.getSortDirection( ), viewModel::loadVotes);
        observeAllNotNull(this, viewModel.getSortOption( ), viewModel.getSortDirection( ), viewModel::setSortStatus);
        
        var adapter = new VoteItemAdapter( );
        adapter.setHasStableIds(true);
        adapter.addAdapterEventListener(this, event -> {
            switch (event.getEvent( )) {
                case LAYOUT_CLICK:
                    if (event.getItem( ).isParticipated( )) {
                        var activityOptions = getActivityOptions(event.getView( ));
                        var intent = new Intent(getContext( ), StockInfoActivity.class)
                                .putExtra(STOCK_TICKER, event.getItem( ).getTicker( ));
                        startActivity(intent, activityOptions.toBundle( ));
                    }
                    else {
                        viewModel.setSnackbar("먼저 투표에 참여해주세요");
                    }
                    break;
                
                case INCREMENT_BTN_CLICK:
                case DECREMENT_BTN_CLICK:
                    var request = new CreateBallotRequest(
                            event.getItem( ).getId( ),
                            event.getEvent( ) == INCREMENT_BTN_CLICK ? INCREMENT : DECREMENT
                    );
                    viewModel.createBallot(request)
                             .then(ballot -> viewModel.refreshVoteItem(event.getPosition( )))
                             .subscribe( );
                    break;
            }
        });
        
        binding.recyclerView.addOnScrollListener(new OnScrollListener( ) {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (Math.abs(dy) > 50) {
                    viewModel.setFilterViewVisibility(false);
                }
            }
        });
        
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext( )));
        return binding.getRoot( );
    }
    
    private void initRecyclerViewPosition( ) {
        binding.recyclerView.smoothScrollToPosition(0);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.search.setVisibility(VISIBLE);
        binding.filter.setVisibility(VISIBLE);
    }
    
    private ActivityOptions getActivityOptions(View view) {
        return ActivityOptions.makeSceneTransitionAnimation(getActivity( ),
                Pair.create(view.findViewById(R.id.englishTicker), TICKER),
                Pair.create(view.findViewById(R.id.koreanName), KOREAN_NAME),
                Pair.create(view.findViewById(R.id.participantCount), PARTICIPANT_COUNT),
                Pair.create(view.findViewById(R.id.participantCountBase), PARTICIPANT_COUNT_BASE),
                Pair.create(view.findViewById(R.id.voteStatusBar), VOTE_STATUS_BAR),
                Pair.create(view.findViewById(R.id.incrementRate), INCREMENT_RATE),
                Pair.create(view.findViewById(R.id.decrementRate), DECREMENT_RATE)
        );
    }
    
    private void init(LayoutInflater inflater, ViewGroup container) {
        binding   = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        viewModel.loadAccount( ).subscribe( );
        viewModel.refreshVoteItemsSchedule(3L)
                 .onError(e -> viewModel.setSnackbar("새로고침 도중 문제가 발생했어요"))
                 .subscribe( );
        
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        binding.executePendingBindings( );
    }
    
    @Override
    public void onNavigationItemReselected(@NonNull @NotNull MenuItem item) {
        initRecyclerViewPosition( );
    }
}
