package com.app.bitwit.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.app.bitwit.data.source.local.CandlestickDao;
import com.app.bitwit.data.source.local.VoteItemDao;
import com.app.bitwit.data.source.local.entity.VoteItem;
import com.app.bitwit.data.source.local.support.Converters;

import java.util.Objects;

@TypeConverters(Converters.class)
@Database(entities = {VoteItem.class, com.app.bitwit.data.source.local.entity.Candlestick.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    
    private static AppDatabase instance;
    
    public static AppDatabase getInstance(Context context) {
        if (Objects.isNull(instance)) {
            synchronized (AppDatabase.class) {
                if (Objects.isNull(instance)) {
                    instance = Room.databaseBuilder(context.getApplicationContext( ), AppDatabase.class, "bitwit")
                                   .build( );
                }
            }
        }
        return instance;
    }
    
    public abstract VoteItemDao voteItemDao( );
    
    public abstract CandlestickDao chartDao( );
}
