package com.app.bitwit.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import com.app.bitwit.R;
import com.app.bitwit.databinding.FragmentPostBinding;
import com.app.bitwit.view.activity.FrameActivity;
import com.app.bitwit.view.activity.PostingActivity;
import com.app.bitwit.view.adapter.PostFragmentAdapter;
import com.google.android.material.tabs.TabLayoutMediator;
import dagger.hilt.android.AndroidEntryPoint;
import lombok.var;

import java.util.Arrays;

@AndroidEntryPoint
public class PostFragment extends Fragment {
    
    private FragmentPostBinding binding;
    
    private PostFragmentAdapter adapter;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_post, container, false);
        binding.setLifecycleOwner(this);
        binding.executePendingBindings( );
        
        adapter = new PostFragmentAdapter(this);
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setSaveEnabled(false);
        
        binding.search.setOnClickListener(v -> {
                                 
        });
        
        binding.posting.setOnClickListener(v -> {
            var intent = new Intent(getContext( ), PostingActivity.class);
            startActivity(intent);
        });
        
        var tabElements = Arrays.asList("인기글", "최신글", "종목 토론방");
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) ->
                tab.setText(tabElements.get(position))
        ).attach( );
        
        return binding.getRoot( );
    }
    
    public void makeSnackbar(String message) {
        ((FrameActivity)getActivity( )).makeSnackbar(message);
    }
    
    public void initRecyclerViewPosition( ) {
        int currentItem = binding.viewPager.getCurrentItem( );
        if (currentItem == 0) {
            ((HotPostFragment)adapter.getFragment(currentItem)).initRecyclerViewPosition( );
        }
        else if (currentItem == 1) {
            ((RecentPostFragment)adapter.getFragment(currentItem)).initRecyclerViewPosition( );
        }
    }
}
