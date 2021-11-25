package com.app.bitwit.module;

import android.annotation.SuppressLint;
import com.app.bitwit.data.source.remote.*;
import com.app.bitwit.common.interceptor.AuthenticationInterceptor;
import com.app.bitwit.common.serializer.InstantSerializer;
import com.app.bitwit.common.serializer.LocalDateTimeSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.inject.Singleton;
import java.time.Instant;
import java.time.LocalDateTime;

@Module
@InstallIn(SingletonComponent.class)
public class WebClientProvider {
    
    @SuppressLint("NewApi")
    @Provides
    @Singleton
    public Gson gson( ) {
        return new GsonBuilder( )
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer( ))
                .registerTypeAdapter(Instant.class, new InstantSerializer( ))
                .setLenient( )
                .create( );
    }
    
    @Provides
    @Singleton
    public OkHttpClient okHttpClient(AuthenticationInterceptor authenticationInterceptor) {
        return new OkHttpClient.Builder( )
                .addInterceptor(authenticationInterceptor)
                .build( );
    }
    
    @Provides
    @Singleton
    public Retrofit retrofit(OkHttpClient okHttpClient, Gson gson) {
        return new Retrofit.Builder( )
                .baseUrl("http://bitwit.site")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create( ))
                .client(okHttpClient)
                .build( );
    }
    
    @Provides
    @Singleton
    public AccountServiceClient getAccountServiceClient(Retrofit retrofit) {
        return retrofit.create(AccountServiceClient.class);
    }
    
    @Provides
    @Singleton
    public VoteServiceClient voteServiceClient(Retrofit retrofit) {
        return retrofit.create(VoteServiceClient.class);
    }
    
    @Provides
    @Singleton
    public BallotServiceClient ballotServiceClient(Retrofit retrofit) {
        return retrofit.create(BallotServiceClient.class);
    }
    
    @Provides
    @Singleton
    public StockServiceClient stockServiceClient(Retrofit retrofit) {
        return retrofit.create(StockServiceClient.class);
    }
    
    @Provides
    @Singleton
    public PostServiceClient postServiceClient(Retrofit retrofit) {
        return retrofit.create(PostServiceClient.class);
    }
    
    @Provides
    @Singleton
    public BithumbServiceClient bithumbServiceClient(Gson gson) {
        return new Retrofit.Builder( )
                .baseUrl("https://api.bithumb.com")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create( ))
                .build( )
                .create(BithumbServiceClient.class);
    }
}
