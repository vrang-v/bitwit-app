package com.app.bitwit.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import com.app.bitwit.R;
import com.app.bitwit.databinding.FragmentPostBinding;
import com.app.bitwit.viewmodel.common.SnackbarViewModel;
import com.app.bitwit.view.activity.PostingActivity;
import com.app.bitwit.view.adapter.PostFragmentAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemReselectedListener;
import com.google.android.material.tabs.TabLayoutMediator;
import dagger.hilt.android.AndroidEntryPoint;
import lombok.var;

import java.util.Arrays;

@AndroidEntryPoint
public class PostFragment extends Fragment implements OnNavigationItemReselectedListener {
    
    private FragmentPostBinding binding;
    private PostFragmentAdapter adapter;
    
    private ActivityResultLauncher<Intent> postingLauncher;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postingLauncher = registerForActivityResult(
                new StartActivityForResult( ), result -> {
                    if (result.getResultCode( ) == PostingActivity.RESULT_SUCCESS) {
                        SnackbarViewModel.MESSAGE.postValue("게시글을 등록했어요");
                        var currentItem            = binding.viewPager.getCurrentItem( );
                        var currentAdapterFragment = adapter.getFragment(currentItem);
                        if (currentAdapterFragment instanceof PostListFragment) {
                            ((PostListFragment)currentAdapterFragment).refreshPosts( );
                        }
                    }
                }
        );
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        init(inflater, container);
        
        adapter = new PostFragmentAdapter(this);
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setSaveEnabled(false);
        
        binding.search.setOnClickListener(v -> {
            var postSearchFragment = new PostSearchFragment( );
            getParentFragmentManager( )
                    .beginTransaction( )
                    .setCustomAnimations(
                            R.anim.slide_right_to_left_enter, 0,
                            0, R.anim.slide_left_to_right_exit
                    )
                    .replace(binding.getRoot( ).getId( ), postSearchFragment, "POST_SEARCH")
                    .addToBackStack("POST_SEARCH")
                    .commit( );
        });
        
        binding.posting.setOnClickListener(v -> {
            var intent = new Intent(getContext( ), PostingActivity.class);
            postingLauncher.launch(intent);
        });
        
        var tabElements = Arrays.asList("인기글", "최신글", "종목 토론방");
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) ->
                tab.setText(tabElements.get(position))
        ).attach( );
        
        return binding.getRoot( );
    }
    
    private void init(LayoutInflater inflater, ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_post, container, false);
        binding.setLifecycleOwner(this);
        binding.executePendingBindings( );
    }
    
    private Fragment getCurrentFragment( ) {
        var fragmentManager = getParentFragmentManager( );
        int index           = fragmentManager.getBackStackEntryCount( ) - 1;
        if (index < 0) { return null; }
        var tag = fragmentManager.getBackStackEntryAt(index).getName( );
        return fragmentManager.findFragmentByTag(tag);
    }
    
    @Override
    public void onNavigationItemReselected(MenuItem item) {
        Fragment currentFragment = getCurrentFragment( );
        if (currentFragment instanceof PostSearchFragment) {
            ((PostSearchFragment)currentFragment).onNavigationItemReselected(item);
            return;
        }
        int currentItem = binding.viewPager.getCurrentItem( );
        ((PostListFragment)adapter.getFragment(currentItem)).onNavigationItemReselected(item);
    }
}
