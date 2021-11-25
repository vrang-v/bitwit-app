package com.app.bitwit.view.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.app.bitwit.view.fragment.PostListFragment;
import com.app.bitwit.viewmodel.HotPostListViewModel;
import com.app.bitwit.viewmodel.RecentPostListViewModel;

import java.util.ArrayList;
import java.util.List;

public class PostFragmentAdapter extends FragmentStateAdapter {
    
    private final List<Fragment> fragments = new ArrayList<>( );
    
    public PostFragmentAdapter(@NonNull Fragment fragment) {
        super(fragment);
        fragments.add(new PostListFragment(HotPostListViewModel.class));
        fragments.add(new PostListFragment(RecentPostListViewModel.class));
        
        // TODO 종목토론방으로 교체
        fragments.add(new PostListFragment(HotPostListViewModel.class));
    }
    
    public Fragment getFragment(int index) {
        return fragments.get(index);
    }
    
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }
    
    @Override
    public int getItemCount( ) {
        return fragments.size( );
    }
}
