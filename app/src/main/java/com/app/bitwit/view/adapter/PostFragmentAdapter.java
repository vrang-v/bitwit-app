package com.app.bitwit.view.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.app.bitwit.view.fragment.HotPostFragment;
import com.app.bitwit.view.fragment.MainFragment;
import com.app.bitwit.view.fragment.RecentPostFragment;

import java.util.ArrayList;
import java.util.List;

public class PostFragmentAdapter extends FragmentStateAdapter {
    
    private final List<Fragment> fragments = new ArrayList<>( );
    
    public PostFragmentAdapter(@NonNull Fragment fragment) {
        super(fragment);
        fragments.add(new HotPostFragment( ));
        fragments.add(new RecentPostFragment( ));
        fragments.add(new MainFragment( ));
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
