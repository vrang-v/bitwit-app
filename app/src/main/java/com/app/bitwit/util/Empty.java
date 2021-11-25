package com.app.bitwit.util;

public class Empty {
    
    private Empty( ) { /* private constructor for singleton pattern */ }
    
    public static Empty getInstance( ) {
        return InstanceHolder.instance;
    }
    
    private static class InstanceHolder {
        private static final Empty instance = new Empty( );
    }
}
