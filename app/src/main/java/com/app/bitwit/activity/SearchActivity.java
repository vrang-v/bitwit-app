package com.app.bitwit.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import com.app.bitwit.R;
import com.app.bitwit.databinding.SearchActivityBinding;
import com.app.bitwit.viewmodel.SearchActivityViewModel;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SearchActivity extends AppCompatActivity {
    
    private SearchActivityBinding   binding;
    private SearchActivityViewModel viewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init( );
        
        viewModel.getSearchWord( ).observe(this, viewModel::search);
    }
    
    private void init( ) {
        binding   = DataBindingUtil.setContentView(this, R.layout.search_activity);
        viewModel = new ViewModelProvider(this).get(SearchActivityViewModel.class);
    }
}
