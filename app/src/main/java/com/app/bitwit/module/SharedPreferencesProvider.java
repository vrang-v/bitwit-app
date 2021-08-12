package com.app.bitwit.module;

import android.content.Context;
import android.content.SharedPreferences;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class SharedPreferencesProvider
{
    @Provides
    public SharedPreferences sharedPreferences(@ApplicationContext Context context)
    {
        return context.getSharedPreferences("bitwit", Context.MODE_PRIVATE);
    }
}
