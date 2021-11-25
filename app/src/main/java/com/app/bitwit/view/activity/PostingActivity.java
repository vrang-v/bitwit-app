package com.app.bitwit.view.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.app.bitwit.R;
import com.app.bitwit.databinding.ActivityPostingBinding;
import com.app.bitwit.dto.LoginAccount;
import com.app.bitwit.view.adapter.TagAdapter;
import com.app.bitwit.view.dialog.NicknameSettingDialog;
import com.app.bitwit.viewmodel.PostingViewModel;
import com.google.android.material.snackbar.Snackbar;
import dagger.hilt.android.AndroidEntryPoint;
import lombok.var;

import static com.app.bitwit.util.livedata.LiveDataUtils.observe;
import static com.app.bitwit.util.livedata.LiveDataUtils.observeAll;
import static com.app.bitwit.util.StringUtils.hasText;
import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT;

@AndroidEntryPoint
public class PostingActivity extends AppCompatActivity {
    
    public static final int RESULT_SUCCESS = 10;
    
    private ActivityPostingBinding binding;
    private PostingViewModel       viewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init( );
        observeViewModel( );
        setClickListener( );
        addTagAdapter( );
    }
    
    private void observeViewModel( ) {
        observe(this, viewModel.getSnackbar( ), s ->
                Snackbar.make(binding.getRoot( ), s, LENGTH_SHORT).show( )
        );
        
        observeAll(this, viewModel.getTitle( ), viewModel.getContent( ), (title, content) ->
                binding.confirm.setEnabled(hasText(title) && hasText(content))
        );
        
        observe(this, viewModel.getInputTag( ), tag ->
                binding.addTagBtn.setEnabled(hasText(tag))
        );
    }
    
    private void setClickListener( ) {
        binding.backBtn.setOnClickListener(v ->
                finish( )
        );
        
        binding.confirm.setOnClickListener(v -> {
            if (! hasAccountName(viewModel.getAccount( ))) {
                showNicknameSettingDialog( );
                return;
            }
            viewModel.createPost( )
                     .onSuccess(post -> {
                         setResult(RESULT_SUCCESS);
                         finish( );
                     })
                     .subscribe( );
        });
        
        binding.addTagBtn.setOnClickListener(v ->
                viewModel.addTag( )
        );
    }
    
    private void addTagAdapter( ) {
        var tagAdapter = new TagAdapter(viewModel.getTags( ));
        viewModel.getTags( ).observe(this, tags -> tagAdapter.notifyDataSetChanged( ));
        binding.tagRecycler.setAdapter(tagAdapter);
        binding.tagRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
    }
    
    private boolean hasAccountName(LoginAccount account) {
        return account != null && hasText(account.getName( ));
    }
    
    private void showNicknameSettingDialog( ) {
        NicknameSettingDialog
                .builder(this)
                .doOnSuccess(unused -> {
                    viewModel.setSnackbar("닉네임을 설정했어요");
                    viewModel.loadAccount( )
                             .then(loginAccount -> viewModel.createPost( ))
                             .onSuccess(post -> {
                                 setResult(RESULT_SUCCESS);
                                 finish( );
                             })
                             .subscribe( );
                })
                .doOnError(e -> viewModel.setSnackbar("닉네임을 설정하는 도중 문제가 발생했어요"))
                .build( )
                .show( );
    }
    
    private void init( ) {
        binding   = DataBindingUtil.setContentView(this, R.layout.activity_posting);
        viewModel = new ViewModelProvider(this).get(PostingViewModel.class);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        binding.executePendingBindings( );
        viewModel.loadAccount( ).subscribe( );
    }
}
