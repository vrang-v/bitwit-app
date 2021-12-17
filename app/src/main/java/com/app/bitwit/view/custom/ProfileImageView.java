package com.app.bitwit.view.custom;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.app.bitwit.R;

public class ProfileImageView extends androidx.appcompat.widget.AppCompatImageView {
    
    public ProfileImageView(@NonNull Context context) {
        super(context);
        init( );
    }
    
    public ProfileImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init( );
    }
    
    public ProfileImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init( );
    }
    
    private void init( ) {
        setImageResource(R.drawable.default_profile_icon_24);
        setClipToOutline(true);
        setScaleType(ScaleType.CENTER_CROP);
        setBackground(new ShapeDrawable(new OvalShape( )));
    }
}
