package com.app.bitwit.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.AndroidRuntimeException;
import android.util.Pair;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import com.app.bitwit.R;
import com.app.bitwit.adapter.VoteItemAdapter;
import com.app.bitwit.data.source.remote.dto.request.CreateOrChangeBallotRequest;
import com.app.bitwit.databinding.MainActivityBinding;
import com.app.bitwit.viewmodel.MainViewModel;
import com.app.bitwit.viewmodel.MainViewModel.Const;
import com.google.android.material.snackbar.Snackbar;
import dagger.hilt.android.AndroidEntryPoint;
import lombok.var;

import static android.view.View.VISIBLE;
import static com.app.bitwit.domain.VotingOption.DECREMENT;
import static com.app.bitwit.domain.VotingOption.INCREMENT;
import static com.app.bitwit.util.IntentKeys.STOCK_TICKER;
import static com.app.bitwit.util.LiveDataUtils.observeAllNotNull;
import static com.app.bitwit.util.TransitionNames.*;
import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    
    private MainActivityBinding binding;
    private MainViewModel       viewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init( );
        
        binding.search.setOnClickListener(v -> {
            var intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        });
        
        binding.filter.setOnClickListener(v ->
                viewModel.changeFilterViewVisibility( )
        );
        
        binding.hide.setOnClickListener(v ->
                viewModel.changeFilterViewVisibility( )
        );
        
        binding.refresh.setOnClickListener(v -> {
            viewModel.postSortOption(Const.NONE);
            viewModel.postSortDirection(Const.NONE);
        });
        
        binding.reset.setOnClickListener(v -> {
            viewModel.postSortOption(Const.NONE);
            viewModel.postSortDirection(Const.NONE);
        });
        
        binding.sortStatus.setOnClickListener(v -> viewModel.postSortDirection(
                viewModel.getSortDirection( ).getValue( ) == Const.ASC ? Const.DESC : Const.ASC)
        );
        
        binding.fluctuation.setOnClickListener(v ->
                viewModel.postSortOption(Const.PRICE_FLUCTUATION)
        );
        binding.price.setOnClickListener(v ->
                viewModel.postSortOption(Const.PRICE)
        );
        binding.participants.setOnClickListener(v ->
                viewModel.postSortOption(Const.PARTICIPANTS)
        );
        
        binding.desc.setOnClickListener(v ->
                viewModel.postSortDirection(Const.DESC)
        );
        binding.asc.setOnClickListener(v ->
                viewModel.postSortDirection(Const.ASC))
        ;
        
        observeAllNotNull(this, viewModel.getSortOption( ), viewModel.getSortDirection( ), viewModel::loadVote);
        
        observeAllNotNull(this, viewModel.getSortOption( ), viewModel.getSortDirection( ), viewModel::setSortStatus);
        
        var adapter = new VoteItemAdapter( );
        adapter.setHasStableIds(true);
        adapter.setOnItemClickListener(this, event -> {
            switch (event.getEventType( )) {
                case LAYOUT:
                    if (event.getVoteItem( ).isParticipated( )) {
                        var activityOptions = getActivityOptions(event.getView( ));
                        var intent = new Intent(this, StockInfoActivity.class)
                                .putExtra(STOCK_TICKER, event.getVoteItem( ).getTicker( ));
                        startActivity(intent, activityOptions.toBundle( ));
                    }
                    else {
                        Snackbar.make(binding.recyclerView, "먼저 투표에 참여해주세요", LENGTH_SHORT).show( );
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
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    
    @Override
    public void onEnterAnimationComplete( ) {
        binding.search.setVisibility(VISIBLE);
        binding.filter.setVisibility(VISIBLE);
        super.onEnterAnimationComplete( );
    }
    
    private ActivityOptions getActivityOptions(View view) {
        return ActivityOptions.makeSceneTransitionAnimation(this,
                Pair.create(view.findViewById(R.id.englishTicker), TICKER),
                Pair.create(view.findViewById(R.id.koreanName), KOREAN_NAME),
                Pair.create(view.findViewById(R.id.participantCount), PARTICIPANT_COUNT),
                Pair.create(view.findViewById(R.id.participantCountBase), PARTICIPANT_COUNT_BASE),
                Pair.create(view.findViewById(R.id.voteStatusBar), VOTE_STATUS_BAR),
                Pair.create(view.findViewById(R.id.incrementRate), INCREMENT_RATE),
                Pair.create(view.findViewById(R.id.decrementRate), DECREMENT_RATE)
        );
    }
    
    private void init( ) {
        binding   = DataBindingUtil.setContentView(this, R.layout.main_activity);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.refreshVoteItemsSchedule(3L);
        viewModel.loadAccount( );
        
        binding.setActivity(this);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        binding.executePendingBindings( );
    }
}
