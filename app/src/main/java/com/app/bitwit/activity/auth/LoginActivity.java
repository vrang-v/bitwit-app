package com.app.bitwit.activity.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import com.app.bitwit.R;
import com.app.bitwit.activity.MainActivity;
import com.app.bitwit.data.source.remote.dto.response.LoginResponse;
import com.app.bitwit.databinding.LoginActivityBinding;
import com.app.bitwit.viewmodel.LoginViewModel;
import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.functions.Consumer;
import lombok.var;

import static android.widget.Toast.makeText;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity
{
    private LoginActivityBinding binding;
    private LoginViewModel       viewModel;
    
    private long backButtonKeyDownTime;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        init( );
        
        viewModel.getToast( )
                 .observe(this, s -> makeText(this, s, Toast.LENGTH_SHORT).show( ));
        
        viewModel.getLoginBtnClick( )
                 .observe(this, unused -> {
                     Consumer<LoginResponse> onComplete = response -> {
                         viewModel.setToast("반갑습니다");
                         var intent = new Intent(getApplicationContext( ), MainActivity.class);
                         startActivity(intent);
                         finish( );
                     };
            
                     Consumer<Throwable> onError = throwable -> {
                         Log.e("ERROR", "login", throwable);
                         viewModel.setToast("로그인 실패");
                         binding.warning1Text.setText("아이디 또는 비밀번호가 일치하지 않거나");
                         binding.warning2Text.setText("네트워크에 연결되어 있지 않습니다");
                         binding.warning2Text.setVisibility(View.VISIBLE);
                     };
            
                     viewModel.login(onComplete, onError);
                 });
        
        viewModel.getNavigateSignUp( )
                 .observe(this, unused -> {
                     var intent = new Intent(getApplicationContext( ), SignUpActivity.class);
                     intent.putExtra("email", "");
                     startActivity(intent);
                     finish( );
                 });
        
        Bundle extras = getIntent( ).getExtras( );
        
        if (extras != null) {
            binding.emailEdit.setText(extras.getString("email"));
            binding.passwordEdit.setText(extras.getString("password"));
            if (extras.getBoolean("textViewLogin")) {
                binding.loginText.setText("로그인 버튼을 눌러주세요");
            }
        }
    }
    
    private void init( )
    {
        binding   = DataBindingUtil.setContentView(this, R.layout.login_activity);
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        
        binding.setActivity(this);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        binding.executePendingBindings( );
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        boolean result = true;
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis( ) - backButtonKeyDownTime > 2000) {
                viewModel.setToast("뒤로가기 버튼을 누르면 앱이 종료됩니다");
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
