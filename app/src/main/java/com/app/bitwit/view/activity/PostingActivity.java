package com.app.bitwit.view.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.app.bitwit.R;
import com.app.bitwit.databinding.ActivityPostingBinding;
import com.app.bitwit.view.adapter.EditableTickerAdapter;
import com.app.bitwit.viewmodel.PostingViewModel;
import com.google.android.material.snackbar.Snackbar;
import dagger.hilt.android.AndroidEntryPoint;
import lombok.var;

import static com.app.bitwit.util.Callback.callback;
import static com.app.bitwit.util.LiveDataUtils.observe;
import static com.app.bitwit.util.LiveDataUtils.observeAll;
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
        
        viewModel.getSnackbar( ).observe(this, s ->
                Snackbar.make(binding.getRoot( ), s, LENGTH_SHORT).show( )
        );
        
        observeAll(this, viewModel.getTitle( ), viewModel.getContent( ), (title, content) ->
                binding.confirm.setEnabled(hasText(title) && hasText(content))
        );
        
        observe(this, viewModel.getInputTag( ), tag ->
                binding.addTagBtn.setEnabled(hasText(tag))
        );
        
        binding.back.setOnClickListener(v ->
                finish( )
        );
        
        binding.confirm.setOnClickListener(v ->
                viewModel.createPost(callback(
                        post -> {
                            setResult(RESULT_SUCCESS);
                            finish( );
                        },
                        e -> viewModel.setSnackbar("게시글 작성 도중 오류가 발생했어요")
                ))
        );
        
        binding.addTagBtn.setOnClickListener(v ->
                viewModel.addTag( )
        );
        
        var editableTickerAdapter = new EditableTickerAdapter(viewModel.getTags( ));
        viewModel.getTags( ).observe(this, strings -> editableTickerAdapter.notifyDataSetChanged( ));
        binding.tagRecycler.setAdapter(editableTickerAdapter);
        binding.tagRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
    }
    
    private void init( ) {
        binding   = DataBindingUtil.setContentView(this, R.layout.activity_posting);
        viewModel = new ViewModelProvider(this).get(PostingViewModel.class);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        binding.executePendingBindings( );
    }
}
