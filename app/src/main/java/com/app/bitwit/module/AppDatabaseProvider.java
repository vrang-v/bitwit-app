package com.app.bitwit.module;

import android.content.Context;
import com.app.bitwit.data.AppDatabase;
import com.app.bitwit.data.source.local.CandlestickDao;
import com.app.bitwit.data.source.local.VoteItemDao;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

import javax.inject.Singleton;

@Module
@InstallIn(SingletonComponent.class)
public class AppDatabaseProvider {
    
    @Provides
    @Singleton
    public AppDatabase appDatabase(@ApplicationContext Context context) {
        return AppDatabase.getInstance(context);
    }
    
    @Provides
    @Singleton
    public VoteItemDao voteItemDao(AppDatabase appDataBase) {
        return appDataBase.voteItemDao( );
    }
    
    @Provides
    @Singleton
    public CandlestickDao chartDao(AppDatabase appDatabase) {
        return appDatabase.chartDao( );
    }
    
}
