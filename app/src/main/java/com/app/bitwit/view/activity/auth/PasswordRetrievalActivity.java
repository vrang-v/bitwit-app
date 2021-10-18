package com.app.bitwit.view.activity.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import com.app.bitwit.R;
import com.app.bitwit.data.source.remote.AccountServiceClient;
import com.app.bitwit.databinding.ActivityPasswordRetrievalBinding;
import com.google.android.material.snackbar.Snackbar;
import dagger.hilt.android.AndroidEntryPoint;
import lombok.var;

import javax.inject.Inject;

import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT;

@AndroidEntryPoint
public class PasswordRetrievalActivity extends AppCompatActivity {
    
    @Inject
    AccountServiceClient accountServiceClient;
    
    private ActivityPasswordRetrievalBinding binding;
    
    private long backButtonKeyDownTime;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_password_retrieval);
        
        Bundle extras = getIntent( ).getExtras( );
        
        if (extras != null) {
            binding.emailEdit.setText(extras.getString("email"));
        }
        
        binding.confirmBtn.setOnClickListener(v ->
                Snackbar.make(binding.getRoot( ), "아직 사용할 수 없는 기능이에요", LENGTH_SHORT).show( )
        );
        
        binding.navigateLoginBtn.setOnClickListener(v -> {
            var intent = new Intent(getApplicationContext( ), LoginActivity.class);
            startActivity(intent);
            finish( );
        });
    }
    
    @Override
    protected void onPause( ) {
        super.onPause( );
        overridePendingTransition(R.anim.anim_slide_left_to_right_enter, R.anim.anim_slide_left_to_right_exit);
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean result = true;
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis( ) - backButtonKeyDownTime > 2000) {
                Snackbar.make(binding.getRoot( ), "뒤로가기 버튼을 누르면 앱이 종료됩니다", LENGTH_SHORT).show( );
                backButtonKeyDownTime = System.currentTimeMillis( );
            }
            else {
                finish( );
            }
        }
        else {
            result = super.onKeyDown(keyCode, event);
        }
        return result;
    }
}