package com.app.bitwit.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import lombok.SneakyThrows;
import lombok.var;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class IOUtils {
    
    @SneakyThrows
    public static byte[] readAllBytes(InputStream input) {
        byte[]                buffer = new byte[8192];
        int bytesRead;
        var output = new ByteArrayOutputStream( );
        while ((bytesRead = input.read(buffer)) != - 1) {
            output.write(buffer, 0, bytesRead);
        }
        return output.toByteArray( );
    }
    
    public static String getFullPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] projection = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver( ).query(contentUri, projection, null, null, null);
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst( );
            return cursor.getString(columnIndex);
        }
        finally {
            if (cursor != null) {
                cursor.close( );
            }
        }
    }
}
