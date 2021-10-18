package com.app.bitwit.view.activity;

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
import com.app.bitwit.view.adapter.SearchAdapter;
import com.app.bitwit.data.source.remote.dto.request.CreateOrChangeBallotRequest;
import com.app.bitwit.databinding.SearchActivityBinding;
import com.app.bitwit.viewmodel.SearchActivityViewModel;
import dagger.hilt.android.AndroidEntryPoint;
import lombok.var;

import static com.app.bitwit.domain.VotingOption.DECREMENT;
import static com.app.bitwit.domain.VotingOption.INCREMENT;
import static com.app.bitwit.util.IntentKeys.STOCK_TICKER;
import static com.app.bitwit.util.LiveDataUtils.observeNotNull;
import static com.app.bitwit.util.TransitionNames.*;

@AndroidEntryPoint
public class SearchActivity extends AppCompatActivity {
    
    private SearchActivityBinding   binding;
    private SearchActivityViewModel viewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init( );
        
        observeNotNull(this, viewModel.getSearchWord( ), viewModel::search);
        
        var adapter = new SearchAdapter( );
        adapter.setHasStableIds(true);
        adapter.setOnItemClickListener(this, event -> {
            switch (event.getEventType( )) {
                case ROOT:
                    viewModel.getLastVoteByTicker(event.getSearchItem( ).getStock( ).getTicker( ), voteItem -> {
                        if (voteItem.isParticipated( )) {
                            var intent = new Intent(this, StockInfoActivity.class)
                                    .putExtra(STOCK_TICKER, event.getSearchItem( ).getStock( ).getTicker( ));
                            startActivity(intent, getActivityOptions(event.getView( )).toBundle( ));
                        }
                        else {
                            event.getSearchItem( ).setVoteId(voteItem.getId( ));
                            adapter.showVoteScreen(event.getPosition( ));
                        }
                    });
                    break;
                
                case INCREMENT_BTN:
                    var request = new CreateOrChangeBallotRequest(event.getSearchItem( ).getVoteId( ), INCREMENT);
                    viewModel.createOrChangeBallot(request, unused ->
                            startActivity(new Intent(this, StockInfoActivity.class)
                                    .putExtra(STOCK_TICKER, event.getSearchItem( ).getStock( ).getTicker( ))));
                    adapter.hideVoteScreen(event.getPosition( ));
                    break;
                
                case DECREMENT_BTN:
                    request = new CreateOrChangeBallotRequest(event.getSearchItem( ).getVoteId( ), DECREMENT);
                    viewModel.createOrChangeBallot(request, unused ->
                            startActivity(new Intent(this, StockInfoActivity.class)
                                    .putExtra(STOCK_TICKER, event.getSearchItem( ).getStock( ).getTicker( ))));
                    adapter.hideVoteScreen( event.getPosition( ));
                    break;
                
                default:
                    throw new AndroidRuntimeException( );
            }
        });
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    
    private void init( ) {
        binding   = DataBindingUtil.setContentView(this, R.layout.search_activity);
        viewModel = new ViewModelProvider(this).get(SearchActivityViewModel.class);
        binding.setActivity(this);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        binding.executePendingBindings( );
    }
    
    private ActivityOptions getActivityOptions(View view) {
        return ActivityOptions.makeSceneTransitionAnimation(this,
                Pair.create(view.findViewById(R.id.ticker), TICKER),
                Pair.create(view.findViewById(R.id.koreanName), KOREAN_NAME)
        );
    }
}
