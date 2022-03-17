package com.app.bitwit.view.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.app.bitwit.R;
import com.app.bitwit.databinding.ActivityPostEditingBinding;
import com.app.bitwit.dto.LoginAccount;
import com.app.bitwit.view.adapter.TagAdapter;
import com.app.bitwit.view.adapter.TagAdapter.TagAdapterEvent;
import com.app.bitwit.view.dialog.NicknameSettingDialog;
import com.app.bitwit.viewmodel.PostEditingViewModel;
import com.google.android.material.snackbar.Snackbar;
import dagger.hilt.android.AndroidEntryPoint;
import lombok.var;

import static com.app.bitwit.util.StringUtils.hasText;
import static com.app.bitwit.util.livedata.LiveDataUtils.observe;
import static com.app.bitwit.util.livedata.LiveDataUtils.observeAll;
import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT;

@AndroidEntryPoint
public class PostEditingActivity extends AppCompatActivity {
    
    public static final int RESULT_SUCCESS = 10;
    
    private ActivityPostEditingBinding binding;
    private PostEditingViewModel       viewModel;
    
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
            viewModel.updatePost( )
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
        var tagAdapter = new TagAdapter( );
        tagAdapter.addAdapterEventListener(this, event -> {
            if (event.getEvent( ) == TagAdapterEvent.DELETE) {
                viewModel.removeTag(event.getItem( ));
            }
        });
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
                             .then(loginAccount -> viewModel.updatePost( ))
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
        binding   = DataBindingUtil.setContentView(this, R.layout.activity_post_editing);
        viewModel = new ViewModelProvider(this).get(PostEditingViewModel.class);
        long postId = getIntent( ).getLongExtra("postId", - 1L);
        viewModel.setPostId(postId);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        binding.executePendingBindings( );
        viewModel.loadAccount( ).subscribe( );
        viewModel.loadPost( ).subscribe( );
    }
    
    @Override
    public void onBackPressed( ) {
        super.onBackPressed( );
        overridePendingTransition(R.anim.slide_left_to_right_enter, R.anim.slide_left_to_right_exit);
    }
}
