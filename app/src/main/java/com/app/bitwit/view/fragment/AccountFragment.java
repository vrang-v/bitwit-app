package com.app.bitwit.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.app.bitwit.R;
import com.app.bitwit.databinding.FragmentAccountBinding;
import com.app.bitwit.viewmodel.AccountViewModel;

public class AccountFragment extends Fragment {
    
    private FragmentAccountBinding binding;
    private AccountViewModel       viewModel;
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding   = DataBindingUtil.inflate(inflater, R.layout.fragment_account, container, false);
        viewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        
        return binding.getRoot( );
    }
}