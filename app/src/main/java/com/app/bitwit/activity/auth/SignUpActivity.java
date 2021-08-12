package com.app.bitwit.activity.auth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import com.app.bitwit.R;
import com.app.bitwit.data.source.remote.dto.response.SimpleIdResponse;
import com.app.bitwit.databinding.SignUpActivityBinding;
import com.app.bitwit.viewmodel.SignUpViewModel;
import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.functions.Consumer;
import lombok.var;

import static android.util.Patterns.EMAIL_ADDRESS;

@AndroidEntryPoint
public class SignUpActivity extends AppCompatActivity {
    
    private SignUpActivityBinding binding;
    private SignUpViewModel       viewModel;
    
    private long backButtonKeyDownTime;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init( );
        
        viewModel.getToast( )
                 .observe(this, s -> Toast.makeText(SignUpActivity.this, s, Toast.LENGTH_SHORT).show( ));
        
        viewModel.getEmail( )
                 .observe(this, password -> {
                     String email = viewModel.getEmail( ).getValue( );
                     if (email != null && ! EMAIL_ADDRESS.matcher(email).matches( )) {
                         binding.signUpText1.setText("입력된 이메일에 문제가 발생했습니다");
                         binding.signUpText2.setText("제대로 된 형식인지 다시 한번 확인해 주세요");
                     }
                 });
        
        viewModel.getCompleteBtnClick( )
                 .observe(this, unused -> {
                             Consumer<SimpleIdResponse> onComplete = response -> {
                                 viewModel.makeToast("회원가입 성공");
                                 Intent intent = new Intent(getApplicationContext( ), LoginActivity.class)
                                         .putExtra("email", viewModel.getEmail( ).getValue( ))
                                         .putExtra("password", viewModel.getPassword( ).getValue( ))
                                         .putExtra("textViewLogin", true);
                                 startActivity(intent);
                                 finish( );
                             };
                    
                             Consumer<Throwable> onError = e -> {
                                 viewModel.makeToast("회원가입 실패");
                                 binding.warningText.setText("이미 등록된 메일이거나 서버 오류일 수 있습니다");
                             };
                    
                             viewModel.signUp(onComplete, onError);
                         }
                 );
        
        viewModel.getNavigateLoginBtnClick( )
                 .observe(this, unused -> {
                     var intent = new Intent(getApplicationContext( ), LoginActivity.class).putExtra("email", "");
                     startActivity(intent);
                     finish( );
                 });
        
        binding.naverButton.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://bitwit.site/login/oauth2/authorize/google"));
            startActivity(intent);
//            var intent = new Intent(getApplicationContext( ), NaverLoginActivity.class);
//            startActivity(intent);
        });
    }
    
    private void init( ) {
        binding   = DataBindingUtil.setContentView(this, R.layout.sign_up_activity);
        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
        
        binding.setActivity(this);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        binding.executePendingBindings( );
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean result = true;
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis( ) - backButtonKeyDownTime > 2000) {
                viewModel.makeToast("뒤로가기 버튼을 누르면 앱이 종료됩니다.");
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