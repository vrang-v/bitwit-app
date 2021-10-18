package com.app.bitwit.view.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.AndroidRuntimeException;
import android.util.Pair;
import android.view.LayoutInflater;
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
import com.app.bitwit.data.source.remote.dto.request.CreateOrChangeBallotRequest;
import com.app.bitwit.databinding.FragmentMainBinding;
import com.app.bitwit.view.activity.FrameActivity;
import com.app.bitwit.view.activity.SearchActivity;
import com.app.bitwit.view.activity.StockInfoActivity;
import com.app.bitwit.view.adapter.VoteItemAdapter;
import com.app.bitwit.viewmodel.MainViewModel;
import com.app.bitwit.viewmodel.MainViewModel.Const;
import dagger.hilt.android.AndroidEntryPoint;
import lombok.var;

import static android.view.View.VISIBLE;
import static com.app.bitwit.domain.VotingOption.DECREMENT;
import static com.app.bitwit.domain.VotingOption.INCREMENT;
import static com.app.bitwit.util.IntentKeys.STOCK_TICKER;
import static com.app.bitwit.util.LiveDataUtils.observeAllNotNull;
import static com.app.bitwit.util.TransitionNames.*;

@AndroidEntryPoint
public class MainFragment extends Fragment {
    
    private FrameActivity       frameActivity;
    private FragmentMainBinding binding;
    private MainViewModel       viewModel;
    
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
        
        observeAllNotNull(this, viewModel.getSortOption( ), viewModel.getSortDirection( ), viewModel::loadVote);
        
        observeAllNotNull(this, viewModel.getSortOption( ), viewModel.getSortDirection( ), viewModel::setSortStatus);
        
        var adapter = new VoteItemAdapter( );
        adapter.setHasStableIds(true);
        adapter.setOnItemClickListener(this, event -> {
            switch (event.getEventType( )) {
                case LAYOUT:
                    if (event.getVoteItem( ).isParticipated( )) {
                        var activityOptions = getActivityOptions(event.getView( ));
                        var intent = new Intent(getContext( ), StockInfoActivity.class)
                                .putExtra(STOCK_TICKER, event.getVoteItem( ).getTicker( ));
                        startActivity(intent, activityOptions.toBundle( ));
                    }
                    else {
                        frameActivity.makeSnackbar("먼저 투표에 참여해주세요");
                    }
                    break;
                
                case INCREMENT_BTN:
                    var request = new CreateOrChangeBallotRequest(event.getVoteItem( ).getId( ), INCREMENT);
                    viewModel.createOrChangeBallot(request, unused -> viewModel.refreshVoteItem(event.getPosition( )));
                    break;
                
                case DECREMENT_BTN:
                    request = new CreateOrChangeBallotRequest(event.getVoteItem( ).getId( ), DECREMENT);
                    viewModel.createOrChangeBallot(request, unused -> viewModel.refreshVoteItem(event.getPosition( )));
                    break;
                
                default:
                    throw new AndroidRuntimeException( );
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
    
    public void initRecyclerViewPosition( ) {
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
        frameActivity = ((FrameActivity)getActivity( ));
        binding       = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        viewModel     = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.refreshVoteItemsSchedule(3L);
        viewModel.loadAccount( );
        
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        binding.executePendingBindings( );
    }
}
