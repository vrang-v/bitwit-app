package com.app.bitwit.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.app.bitwit.viewmodel.SearchablePostListViewModel;
import dagger.hilt.android.AndroidEntryPoint;
import lombok.var;

import static com.app.bitwit.util.livedata.LiveDataUtils.observeAll;

@AndroidEntryPoint
public class SearchablePostListFragment extends PostListFragment {
    
    private SearchablePostListViewModel viewModel;
    
    public SearchablePostListFragment(Class<SearchablePostListViewModel> viewModelClass) {
        super(viewModelClass);
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        this.viewModel = ((SearchablePostListViewModel)super.viewModel);
        observeAll(this, viewModel.getSearchWord( ), viewModel.getPosts( ), (searchWord, posts) -> {
            binding.swipeLayout.setEnabled(! posts.isEmpty( ));
            binding.postRecyclerview.setVisibility(posts.isEmpty( ) ? View.INVISIBLE : View.VISIBLE);
            var bundle = new Bundle( );
            bundle.putString("searchWord", searchWord);
            bundle.putInt("postCount", posts.size( ));
            getParentFragmentManager( ).setFragmentResult("postSearchResult", bundle);
        });
        return view;
    }
    
    public void setSearchWord(String searchWord) {
        viewModel.setSearchWord(searchWord);
    }
    
    public void search( ) {
        adapter.init( );
        viewModel.refreshPage( )
                 .onSuccess(posts -> {
                     if (posts.size( ) < viewModel.getPageSize( )) {
                         adapter.lastPage( );
                     }
                 })
                 .subscribe( );
    }
}
