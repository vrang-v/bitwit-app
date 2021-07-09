package com.app.bitwit;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.app.bitwit.adapter.MyRecyclerAdapter;
import com.app.bitwit.domain.Ballot;
import com.app.bitwit.domain.Vote;
import com.app.bitwit.domain.VotingOption;
import com.app.bitwit.webclient.AccountServiceClient;
import com.app.bitwit.webclient.BallotServiceClient;
import com.app.bitwit.webclient.VoteServiceClient;
import com.app.bitwit.webclient.dto.request.AccountRequest;
import com.app.bitwit.webclient.dto.request.BallotRequest;
import lombok.var;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

import static com.app.bitwit.webclient.WebClientFactory.getAccountServiceClient;
import static com.app.bitwit.webclient.WebClientFactory.getBallotServiceClient;
import static com.app.bitwit.webclient.WebClientFactory.getVoteServiceClient;

public class MainActivity extends AppCompatActivity
{
    private final AccountServiceClient accountServiceClient = getAccountServiceClient( );
    private final VoteServiceClient    voteServiceClient    = getVoteServiceClient( );
    private final BallotServiceClient  ballotServiceClient  = getBallotServiceClient( );
    
    private RecyclerView      recyclerView;
    private MyRecyclerAdapter mRecyclerAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    
        recyclerView = findViewById(R.id.recyclerView);
        
        createAccount( );
        
        mRecyclerAdapter.setOnIncrementClickListener((view, position) -> {
            var vote    = mRecyclerAdapter.getVote(position);
            var request = new BallotRequest(1L, vote.getId( ), VotingOption.INCREMENT);
            createBallot(request);
        });
        
        mRecyclerAdapter.setOnDecrementClickListener((view, position) -> {
            var vote    = mRecyclerAdapter.getVote(position);
            var request = new BallotRequest(1L, vote.getId( ), VotingOption.DECREMENT);
            createBallot(request);
        });
        
        recyclerView.setAdapter(mRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    
        List<Vote> votes = new ArrayList<>( );
        mRecyclerAdapter.setVotes(votes);
        
        getVotes( );
    }
    
    private void createBallot(BallotRequest request)
    {
        ballotServiceClient
                .createBallot(request)
                .enqueue(new Callback<Ballot>( )
                {
                    @Override
                    public void onResponse(Call<Ballot> call, Response<Ballot> response)
                    {
                        getVotes( );
                    }
                    
                    @Override
                    public void onFailure(Call<Ballot> call, Throwable t)
                    {
                        Log.e("ERROR", "onFailure: ", t);
                    }
                });
    }
    
    private void createAccount( )
    {
        mRecyclerAdapter = new MyRecyclerAdapter( );
        var accountRequest = new AccountRequest("test", "test@email.com", "plain");
        accountServiceClient.createAccount(accountRequest).enqueue(new Callback<Void>( )
        {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) { }
            
            @Override
            public void onFailure(Call<Void> call, Throwable t) { }
        });
    }
    
    private void getVotes( )
    {
        voteServiceClient.getVotes("min")
                         .enqueue(new Callback<List<Vote>>( )
                         {
                             @Override
                             public void onResponse(@NotNull Call<List<Vote>> call,
                                                    @NotNull Response<List<Vote>> response)
                             {
                                 mRecyclerAdapter.setVotes(response.body( ));
                             }
            
                             @Override
                             public void onFailure(@NotNull Call<List<Vote>> call, @NotNull Throwable t)
                             {
                                 Log.e("ERROR", "onFailure: ", t);
                             }
                         });
    }
}

