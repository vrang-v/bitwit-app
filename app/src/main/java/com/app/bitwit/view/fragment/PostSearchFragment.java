package com.app.bitwit.view.fragment;

import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.app.bitwit.R;
import com.app.bitwit.databinding.FragmentPostSearchBinding;
import com.app.bitwit.viewmodel.PostSearchViewModel;
import com.app.bitwit.viewmodel.SearchablePostListViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemReselectedListener;
import dagger.hilt.android.AndroidEntryPoint;
import lombok.var;
import org.jetbrains.annotations.NotNull;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.app.bitwit.util.livedata.LiveDataUtils.observe;
import static com.app.bitwit.util.StringUtils.hasText;

@AndroidEntryPoint
public class PostSearchFragment extends Fragment implements OnNavigationItemReselectedListener {
    
    private FragmentPostSearchBinding binding;
    private PostSearchViewModel       viewModel;
    
    private SearchablePostListFragment searchablePostListFragment;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding   = DataBindingUtil.inflate(inflater, R.layout.fragment_post_search, container, false);
        viewModel = new ViewModelProvider(this).get(PostSearchViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        binding.executePendingBindings( );
        
        binding.getRoot( ).setOnTouchListener((v, event) -> true);
        binding.backBtn.setOnClickListener(v -> getParentFragmentManager( ).popBackStack( ));
        setOnKeyboardStatusChangeEvent( );
        
        searchablePostListFragment = new SearchablePostListFragment(SearchablePostListViewModel.class);
        getChildFragmentManager( )
                .beginTransaction( )
                .replace(R.id.postList, searchablePostListFragment, "POST_LIST")
                .addToBackStack("POST_LIST")
                .commit( );
        
        getChildFragmentManager( )
                .setFragmentResultListener("postSearchResult", this, (requestKey, bundle) -> {
                    var searchWord = bundle.getString("searchWord");
                    var postCount  = bundle.getInt("postCount");
                    binding.noSearchResultText.setVisibility(
                            hasText(searchWord) && postCount == 0 ? VISIBLE : INVISIBLE);
                });
        
        observe(this, viewModel.getSearchWord( ), searchablePostListFragment::setSearchWord);
        
        binding.inputSearchWord.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction( ) == KeyEvent.ACTION_DOWN) {
                binding.inputSearchWord.setCursorVisible(false);
                searchablePostListFragment.search( );
                return true;
            }
            return false;
        });
        return binding.getRoot( );
    }
    
    private void setOnKeyboardStatusChangeEvent( ) {
        binding.getRoot( ).getViewTreeObserver( ).addOnGlobalLayoutListener(( ) -> {
            int     rootHeight = binding.getRoot( ).getRootView( ).getHeight( );
            int     height     = binding.getRoot( ).getHeight( );
            boolean keyboardOn = (double)height / rootHeight < 0.8;
            if (keyboardOn) {
                binding.noSearchResultText.setVisibility(INVISIBLE);
                binding.inputSearchWord.setCursorVisible(true);
            }
            binding.postList.setVisibility(keyboardOn ? INVISIBLE : VISIBLE);
        });
    }
    
    @Override
    public void onNavigationItemReselected(@NonNull @NotNull MenuItem item) {
        searchablePostListFragment.onNavigationItemReselected(item);
    }
}
