package com.app.bitwit.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.app.bitwit.R;
import com.app.bitwit.constant.ExtraKey;
import com.app.bitwit.databinding.FragmentPostListBinding;
import com.app.bitwit.view.activity.PostActivity;
import com.app.bitwit.view.adapter.PostsAdapter;
import com.app.bitwit.viewmodel.PostListViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemReselectedListener;
import dagger.hilt.android.AndroidEntryPoint;
import lombok.var;

@AndroidEntryPoint
public class PostListFragment extends Fragment implements OnNavigationItemReselectedListener {
    
    private final Class<? extends PostListViewModel> viewModelClass;
    
    protected FragmentPostListBinding binding;
    protected PostListViewModel       viewModel;
    
    protected PostsAdapter adapter;
    
    protected ActivityResultLauncher<Intent> postLauncher;
    
    private long lastClickedPostId;
    
    public PostListFragment(Class<? extends PostListViewModel> viewModelClass) {
        this.viewModelClass = viewModelClass;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postLauncher = registerForActivityResult(new StartActivityForResult( ),
                result -> {
                    if (result.getResultCode( ) == PostActivity.RESULT_DELETED) {
                        viewModel.setSnackbar("게시글을 삭제했어요");
                        viewModel.removePost(lastClickedPostId);
                    }
                }
        );
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_post_list, container, false);
        init( );
        
        adapter = new PostsAdapter( );
        adapter.setHasStableIds(true);
        adapter.addEventListener(this, event -> {
            var post = event.getItem( );
            switch (event.getEvent( )) {
                case CLICK:
                    var intent = new Intent(getContext( ), PostActivity.class)
                            .putExtra(ExtraKey.POST_ID, post.getId( ));
                    lastClickedPostId = post.getId( );
                    postLauncher.launch(intent);
                    getActivity( ).overridePendingTransition(
                            R.anim.slide_right_to_left_enter, R.anim.slide_right_to_left_exit
                    );
                    break;
                case HEART:
                    if (post.isLike( )) {
                        viewModel.unlike(post.getId( ))
                                 .then(like -> viewModel.loadPost(post.getId( )))
                                 .onSuccess(post_ -> adapter.updatePost(event.getPosition( ), post_))
                                 .subscribe( );
                    }
                    else {
                        viewModel.like(post.getId( ))
                                 .then(like -> viewModel.loadPost(post.getId( )))
                                 .onSuccess(post_ -> adapter.updatePost(event.getPosition( ), post_))
                                 .subscribe( );
                    }
                    break;
                case NEXT_PAGE:
                    viewModel.nextPage( )
                             .onSuccess(posts -> {
                                 if (posts.size( ) < viewModel.getPageSize( )) {
                                     adapter.lastPage( );
                                 }
                             })
                             .subscribe( );
                    break;
                default:
                    break;
            }
            
        });
        binding.postRecyclerview.setAdapter(adapter);
        binding.postRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity( )));
        binding.swipeLayout.setOnRefreshListener(( ) -> {
            adapter.init( );
            viewModel.refreshPage( )
                     .onSuccess(posts -> {
                         if (posts.size( ) < viewModel.getPageSize( )) {
                             adapter.lastPage( );
                         }
                         binding.swipeLayout.setRefreshing(false);
                     })
                     .subscribe( );
        });
        return binding.getRoot( );
    }
    
    public void refreshPosts( ) {
        adapter.init( );
        viewModel.refreshPage( )
                 .onSuccess(posts -> {
                     if (posts.size( ) < viewModel.getPageSize( )) {
                         adapter.lastPage( );
                     }
                 })
                 .subscribe( );
    }
    
    private void initRecyclerViewPosition( ) {
        binding.postRecyclerview.smoothScrollToPosition(0);
    }
    
    private void init( ) {
        viewModel = new ViewModelProvider(this).get(viewModelClass);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        binding.executePendingBindings( );
        
        viewModel.nextPage( )
                 .onSuccess(posts -> {
                     if (posts.size( ) < viewModel.getPageSize( )) {
                         adapter.lastPage( );
                     }
                 })
                 .subscribe( );
    }
    
    @Override
    public void onNavigationItemReselected(@NonNull MenuItem item) {
        initRecyclerViewPosition( );
    }
}
