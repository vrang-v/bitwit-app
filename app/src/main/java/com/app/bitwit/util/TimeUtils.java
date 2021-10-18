package com.app.bitwit.util;

import android.annotation.SuppressLint;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Instant;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class TimeUtils {
    
    @SuppressLint("NewApi")
    public static String formatTimeString(long epochSecond) {
        long now      = Instant.now( ).getEpochSecond( );
        long interval = now - epochSecond;
        if (interval < TimeUnit.SECOND) {
            return "방금 전";
        }
        interval /= TimeUnit.SECOND;
        if (interval < TimeUnit.MINUTE) {
            return interval + "분 전";
        }
        interval /= TimeUnit.MINUTE;
        if (interval < TimeUnit.HOUR) {
            return interval + "시간 전";
        }
        interval /= TimeUnit.HOUR;
        if (interval < TimeUnit.DAY) {
            return interval + "일 전";
        }
        interval /= TimeUnit.DAY;
        if (interval < TimeUnit.MONTH) {
            return interval + "달 전";
        }
        else {
            return interval + "년 전";
        }
    }
    
    private static class TimeUnit {
        public static final int SECOND = 60;
        public static final int MINUTE = 60;
        public static final int HOUR   = 24;
        public static final int DAY    = 30;
        public static final int MONTH  = 12;
    }
}
