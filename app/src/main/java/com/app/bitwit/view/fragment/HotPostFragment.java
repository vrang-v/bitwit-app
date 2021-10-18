package com.app.bitwit.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import com.app.bitwit.databinding.FragmentHotPostBinding;
import com.app.bitwit.domain.Like;
import com.app.bitwit.domain.Post;
import com.app.bitwit.util.Callback;
import com.app.bitwit.view.activity.PostActivity;
import com.app.bitwit.view.adapter.PostsAdapter;
import com.app.bitwit.viewmodel.HotPostViewModel;
import dagger.hilt.android.AndroidEntryPoint;
import lombok.var;

import static com.app.bitwit.util.Callback.callback;
import static com.app.bitwit.util.LiveDataUtils.observeHasText;

@AndroidEntryPoint
public class HotPostFragment extends Fragment {
    
    private FragmentHotPostBinding binding;
    private HotPostViewModel       viewModel;
    
    private PostsAdapter adapter;
    
    private ActivityResultLauncher<Intent> activityResultLauncher;
    
    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityResultLauncher = registerForActivityResult(new StartActivityForResult( ),
                result -> {
                    if (result.getResultCode( ) == 10) {
                        viewModel.setSnackbar("게시글을 삭제했어요");
                    }
                }
        );
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_hot_post, container, false);
        init( );
        
        observeHasText(this, viewModel.getSnackbar( ), s ->
                ((PostFragment)getParentFragment( )).makeSnackbar(s)
        );
        
        adapter = new PostsAdapter( );
        adapter.setHasStableIds(true);
        adapter.addEventListener(this, event -> {
            switch (event.getEventType( )) {
                case CLICK:
                    var intent = new Intent(getContext( ), PostActivity.class)
                            .putExtra(ExtraKey.POST_ID, event.getPost( ).getId( ));
//                    startActivity(intent);
                    activityResultLauncher.launch(intent);
                    getActivity( ).overridePendingTransition(
                            R.anim.anim_slide_right_to_left_enter, R.anim.anim_slide_right_to_left_exit
                    );
                    break;
                case HEART:
                    Callback<Post> updatePost = callback(
                            post -> adapter.updatePost(event.getPosition( ), post), null
                    );
                    Callback<Like> getPost = callback(
                            unused -> viewModel.refreshPost(event.getPost( ).getId( ), updatePost), null
                    );
                    if (event.getPost( ).isLike( )) {
                        viewModel.unlike(event.getPost( ).getId( ), getPost);
                    }
                    else {
                        viewModel.like(event.getPost( ).getId( ), getPost);
                    }
                    break;
                case NEXT_PAGE:
                    viewModel.loadMostViewedPostNextPage(
                            callback(
                                    posts -> {
                                        if (posts.isEmpty( )) {
                                            adapter.lastPage( );
                                        }
                                    },
                                    e -> viewModel.setSnackbar("게시물을 불러오는 도중 문제가 발생했어요")
                            )
                    );
                    break;
                default:
                    break;
            }
            
        });
        binding.postRecyclerview.setAdapter(adapter);
        binding.postRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity( )));
        binding.swipeLayout.setOnRefreshListener(( ) -> viewModel.refreshPage(
                callback(posts -> binding.swipeLayout.setRefreshing(false), null)
        ));
        return binding.getRoot( );
    }
    
    public void initRecyclerViewPosition( ) {
        binding.postRecyclerview.smoothScrollToPosition(0);
    }
    
    private void init( ) {
        viewModel = new ViewModelProvider(this).get(HotPostViewModel.class);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        binding.executePendingBindings( );
        
        viewModel.loadMostViewedPostNextPage(
                callback(
                        posts -> {
                            if (posts.isEmpty( )) {
                                adapter.lastPage( );
                            }
                        },
                        e -> viewModel.setSnackbar("게시물을 불러오는 도중 문제가 발생했어요")
                )
        );
    }
}
