package com.app.bitwit.view.activity.auth;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import com.app.bitwit.R;
import com.app.bitwit.databinding.ActivityEmailVerifyingBinding;
import com.app.bitwit.view.activity.FrameActivity;
import com.app.bitwit.viewmodel.EmailVerifyingViewModel;
import dagger.hilt.android.AndroidEntryPoint;
import lombok.var;

@AndroidEntryPoint
public class EmailVerifyingActivity extends AppCompatActivity {
    
    private ActivityEmailVerifyingBinding binding;
    private EmailVerifyingViewModel       viewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init( );
    
        binding.resendEmailBtn.setOnClickListener(v ->
                viewModel.resendEmailToken( )
                         .onSuccess(empty -> viewModel.setInfoMessage("메일함을 확인해주세요"))
                         .subscribe( )
        );
        
        binding.confirmBtn.setOnClickListener(v ->
                viewModel.isEmailVerifiedAccount( )
                         .onSuccess(response -> {
                             if (response.isVerified( )) {
                                 var intent = new Intent(this, FrameActivity.class);
                                 startActivity(intent);
                                 finish( );
                             }
                             else {
                                 viewModel.setInfoMessage("아직 인증이 되지 않았습니다\n이메일을 확인해주세요");
                             }
                         })
                         .subscribe( ));
        
        viewModel.setInfoMessage("아직 이메일 인증이 되지 않은 계정입니다\n서비스를 정상적으로 이용하기 위해 인증을 진행해주세요");
    }
    
    private void init( ) {
        binding   = DataBindingUtil.setContentView(this, R.layout.activity_email_verifying);
        viewModel = new ViewModelProvider(this).get(EmailVerifyingViewModel.class);
        
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        binding.executePendingBindings( );
    }
}