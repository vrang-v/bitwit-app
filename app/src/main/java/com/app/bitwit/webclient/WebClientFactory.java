package com.app.bitwit.webclient;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WebClientFactory
{
    private static final Retrofit RETROFIT;
    
    private static AccountServiceClient accountServiceClient;
    private static VoteServiceClient    voteServiceClient;
    private static BallotServiceClient  ballotServiceClient;
    private static StockServiceClient   stockServiceClient;

//    private static final Gson GSON;
    
    static {
//        GSON = new GsonBuilder( )
//                .registerTypeAdapter(LocalDateTime.class,
//                        (JsonDeserializer<LocalDateTime>)(json, type, jsonDeserializationContext) ->
//                                LocalDateTime.parse(json.getAsString( ), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
//                .create( );
//
        RETROFIT = new Retrofit.Builder( )
                .baseUrl("http://119.71.82.20:8000/")
                .addConverterFactory(GsonConverterFactory.create( ))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create( ))
                .build( );
        
        accountServiceClient = RETROFIT.create(AccountServiceClient.class);
        voteServiceClient    = RETROFIT.create(VoteServiceClient.class);
        ballotServiceClient  = RETROFIT.create(BallotServiceClient.class);
    }
    
    public static AccountServiceClient getAccountServiceClient( )
    {
        if (accountServiceClient == null) {
            accountServiceClient = RETROFIT.create(AccountServiceClient.class);
        }
        return accountServiceClient;
    }
    
    public static VoteServiceClient getVoteServiceClient( )
    {
        if (voteServiceClient == null) {
            voteServiceClient = RETROFIT.create(VoteServiceClient.class);
        }
        return voteServiceClient;
    }
    
    public static BallotServiceClient getBallotServiceClient( )
    {
        if (ballotServiceClient == null) {
            ballotServiceClient = RETROFIT.create(BallotServiceClient.class);
        }
        return ballotServiceClient;
    }
    
    public static StockServiceClient getStockServiceClient( )
    {
        if (stockServiceClient == null) {
            stockServiceClient = RETROFIT.create(StockServiceClient.class);
        }
        return stockServiceClient;
    }
}
