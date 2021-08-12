package com.app.bitwit.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.AndroidRuntimeException;
import android.util.Pair;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.app.bitwit.R;
import com.app.bitwit.adapter.VoteAdapter;
import com.app.bitwit.data.source.remote.dto.request.CreateOrChangeBallotRequest;
import com.app.bitwit.databinding.MainActivityBinding;
import com.app.bitwit.viewmodel.MainViewModel;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import dagger.hilt.android.AndroidEntryPoint;
import lombok.var;

import static com.app.bitwit.domain.VotingOption.DECREMENT;
import static com.app.bitwit.domain.VotingOption.INCREMENT;
import static com.app.bitwit.util.IntentKeys.VOTE_ID;
import static com.app.bitwit.util.TransitionNames.*;

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
        
        var adapter = new VoteAdapter( );
        adapter.setHasStableIds(true);
        adapter.setOnItemClickListener(this, event -> {
            switch (event.getEventType( )) {
                case LAYOUT:
                    if (event.getVoteItem( ).isParticipated( )) {
                        var activityOptions = getActivityOptions(event.getView( ));
                        var intent = new Intent(this, VoteInfoActivity.class)
                                .putExtra(VOTE_ID, event.getVoteItem( ).getId( ));
                        startActivity(intent, activityOptions.toBundle( ));
                    }
                    else {
                        Snackbar.make(binding.recyclerView, "먼저 투표에 참여해주세요", BaseTransientBottomBar.LENGTH_SHORT)
                                .show( );
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
        
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
        viewModel.refreshVoteItemsSchedule(5L);
        viewModel.loadAccount( );
        
        binding.setActivity(this);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        binding.executePendingBindings( );
    }
}
