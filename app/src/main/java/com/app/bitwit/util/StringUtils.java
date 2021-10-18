package com.app.bitwit.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static android.util.Patterns.EMAIL_ADDRESS;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtils {
    
    public static boolean hasText(String s) {
        return s != null && ! s.isEmpty( ) && containsText(s);
    }
    
    private static boolean containsText(String s) {
        int length = s.length( );
        for (int i = 0; i < length; i++) {
            if (! Character.isWhitespace(s.charAt(i))) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isEmailFormat(String s) {
        return hasText(s) && EMAIL_ADDRESS.matcher(s).matches( );
    }
}
