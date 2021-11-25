package com.app.bitwit.view.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.app.bitwit.R;
import com.app.bitwit.data.source.remote.dto.request.CreateBallotRequest;
import com.app.bitwit.databinding.ActivityStockSearchBinding;
import com.app.bitwit.dto.SearchItem;
import com.app.bitwit.view.adapter.StockSearchAdapter;
import com.app.bitwit.viewmodel.SearchActivityViewModel;
import dagger.hilt.android.AndroidEntryPoint;
import lombok.var;

import static com.app.bitwit.domain.VotingOption.DECREMENT;
import static com.app.bitwit.domain.VotingOption.INCREMENT;
import static com.app.bitwit.util.IntentKeys.STOCK_TICKER;
import static com.app.bitwit.util.LiveDataUtils.observeNotNull;
import static com.app.bitwit.util.TransitionNames.KOREAN_NAME;
import static com.app.bitwit.util.TransitionNames.TICKER;
import static com.app.bitwit.view.adapter.StockSearchAdapter.StockSearchEvent.INCREMENT_BTN;

@AndroidEntryPoint
public class SearchActivity extends AppCompatActivity {
    
    private ActivityStockSearchBinding binding;
    private SearchActivityViewModel    viewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init( );
        
        observeNotNull(this, viewModel.getSearchWord( ), searchWord ->
                viewModel.search(searchWord).subscribe( )
        );
        
        var adapter = new StockSearchAdapter( );
        adapter.setHasStableIds(true);
        adapter.addAdapterEventListener(this, event -> {
            SearchItem searchItem = event.getItem( );
            switch (event.getEvent( )) {
                case ROOT:
                    viewModel.getLastVoteByTicker(searchItem.getStock( ).getTicker( ))
                             .onSuccess(voteItem -> {
                                 if (voteItem.isParticipated( )) {
                                     var intent = new Intent(this, StockInfoActivity.class)
                                             .putExtra(STOCK_TICKER, searchItem.getStock( ).getTicker( ));
                                     startActivity(intent, getActivityOptions(event.getView( )).toBundle( ));
                                 }
                                 else {
                                     searchItem.setVoteId(voteItem.getId( ));
                                     adapter.showVoteScreen(event.getPosition( ));
                                 }
                             })
                             .then(voteItem -> viewModel.refreshVoteItem(voteItem.getId( )))
                             .subscribe( );
                    break;
                
                case INCREMENT_BTN:
                case DECREMENT_BTN:
                    var request = new CreateBallotRequest(
                            searchItem.getVoteId( ), event.getEvent( ) == INCREMENT_BTN ? INCREMENT : DECREMENT
                    );
                    viewModel.createBallot(request)
                             .onSuccess(ballot -> {
                                 adapter.hideVoteScreen(event.getPosition( ));
                                 startActivity(new Intent(this, StockInfoActivity.class)
                                         .putExtra(STOCK_TICKER, searchItem.getStock( ).getTicker( )));
                             })
                             .then(ballot -> viewModel.refreshVoteItem(ballot.getVoteId( )))
                             .subscribe( );
                    break;
            }
        });
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    
    private void init( ) {
        binding   = DataBindingUtil.setContentView(this, R.layout.activity_stock_search);
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
