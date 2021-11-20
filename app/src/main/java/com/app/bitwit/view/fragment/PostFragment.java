package com.app.bitwit.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import com.app.bitwit.R;
import com.app.bitwit.databinding.FragmentPostBinding;
import com.app.bitwit.util.SnackbarViewModel;
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
    
    private ActivityResultLauncher<Intent> launcher;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        launcher = registerForActivityResult(new StartActivityForResult( ), result -> {
            if (result.getResultCode( ) == PostingActivity.RESULT_SUCCESS) {
                SnackbarViewModel.MESSAGE.postValue("게시글을 등록했어요");
                refreshPosts( );
            }
        });
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_post, container, false);
        binding.setLifecycleOwner(this);
        binding.executePendingBindings( );
        
        adapter = new PostFragmentAdapter(this);
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setSaveEnabled(false);
        
        binding.search.setOnClickListener(v -> {
            getParentFragmentManager( )
                    .beginTransaction( )
                    .setCustomAnimations(
                            R.anim.slide_right_to_left_enter, 0,
                            0, R.anim.slide_left_to_right_exit
                    )
                    .replace(binding.getRoot( ).getId( ), new PostSearchFragment( ), "POST_SEARCH")
                    .addToBackStack("POST_SEARCH")
                    .commit( );
            getParentFragmentManager( ).executePendingTransactions( );
        });
        
        binding.posting.setOnClickListener(v -> {
            var intent = new Intent(getContext( ), PostingActivity.class);
            launcher.launch(intent);
        });
        
        var tabElements = Arrays.asList("인기글", "최신글", "종목 토론방");
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) ->
                tab.setText(tabElements.get(position))
        ).attach( );
        
        return binding.getRoot( );
    }
    
    public void initRecyclerViewPosition( ) {
        if (getParentFragmentManager( ).getBackStackEntryCount( ) > 0) {
            if (getCurrentFragment( ) instanceof PostSearchFragment) {
                ((PostSearchFragment)getCurrentFragment( )).initRecyclerViewPosition( );
            }
            return;
        }
        int currentItem = binding.viewPager.getCurrentItem( );
        if (currentItem == 0) {
            ((HotPostFragment)adapter.getFragment(currentItem)).initRecyclerViewPosition( );
        }
        else if (currentItem == 1) {
            ((RecentPostFragment)adapter.getFragment(currentItem)).initRecyclerViewPosition( );
        }
    }
    
    public void refreshPosts( ) {
        int currentItem = binding.viewPager.getCurrentItem( );
        if (currentItem == 0) {
            ((HotPostFragment)adapter.getFragment(currentItem)).refreshPosts( );
        }
        else if (currentItem == 1) {
            ((RecentPostFragment)adapter.getFragment(currentItem)).refreshPosts( );
        }
    }
    
    private Fragment getCurrentFragment( ) {
        var fragmentManager = getParentFragmentManager( );
        var tag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount( ) - 1)
                                 .getName( );
        return fragmentManager.findFragmentByTag(tag);
    }
}
