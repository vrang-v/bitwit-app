package com.app.bitwit.data.source.local.support;

import android.annotation.SuppressLint;
import androidx.room.TypeConverter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Converters {
    
    @SuppressLint("NewApi")
    @TypeConverter
    public static LocalDateTime fromTimestamp(Long value) {
        return value == null ? null : LocalDateTime.ofInstant(Instant.ofEpochSecond(value), ZoneId.systemDefault( ));
    }
    
    @SuppressLint("NewApi")
    @TypeConverter
    public static Long localDateTimeToTimeStamp(LocalDateTime localDateTime) {
        return localDateTime == null ? null : localDateTime.toEpochSecond(ZoneOffset.UTC);
    }
    
}
