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
import com.app.bitwit.databinding.FragmentRecentPostBinding;
import com.app.bitwit.domain.Like;
import com.app.bitwit.domain.Post;
import com.app.bitwit.util.Callback;
import com.app.bitwit.view.activity.PostActivity;
import com.app.bitwit.view.adapter.PostsAdapter;
import com.app.bitwit.viewmodel.RecentPostViewModel;
import dagger.hilt.android.AndroidEntryPoint;
import lombok.var;

import static com.app.bitwit.util.Callback.callback;

@AndroidEntryPoint
public class RecentPostFragment extends Fragment {
    
    private FragmentRecentPostBinding binding;
    private RecentPostViewModel       viewModel;
    
    private PostsAdapter adapter;
    
    private ActivityResultLauncher<Intent> launcher;
    
    private long lastClickedPostId;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        launcher = registerForActivityResult(new StartActivityForResult( ),
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recent_post, container, false);
        init( );
        
        adapter = new PostsAdapter( );
        adapter.setHasStableIds(true);
        adapter.addEventListener(this, event -> {
            switch (event.getEventType( )) {
                case CLICK:
                    var intent = new Intent(getContext( ), PostActivity.class)
                            .putExtra(ExtraKey.POST_ID, event.getPost( ).getId( ));
                    lastClickedPostId = event.getPost( ).getId( );
                    launcher.launch(intent);
                    getActivity( ).overridePendingTransition(
                            R.anim.slide_right_to_left_enter, R.anim.slide_right_to_left_exit
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
                    viewModel.loadRecentPostNextPage(callback(
                            posts -> {
                                if (posts.size( ) < 20) {
                                    adapter.lastPage( );
                                }
                            },
                            e -> viewModel.setSnackbar("게시물을 불러오는 도중 문제가 발생했어요")
                    ));
                    break;
                default:
                    break;
            }
            
        });
        binding.postRecyclerview.setAdapter(adapter);
        binding.postRecyclerview.setLayoutManager(new LinearLayoutManager(getContext( )));
        binding.swipeLayout.setOnRefreshListener(( ) -> {
            adapter.init( );
            viewModel.refreshPage(
                    callback(
                            posts -> {
                                if (posts.size( ) < 20) {
                                    adapter.lastPage( );
                                }
                                binding.swipeLayout.setRefreshing(false);
                            },
                            e -> viewModel.setSnackbar("게시물 새로고침 도중 문제가 발생했어요")
                    )
            );
        });
        return binding.getRoot( );
    }
    
    public void refreshPosts( ) {
        viewModel.refreshPage(callback(null, e -> viewModel.setSnackbar("게시물 새로고침 도중 문제가 발생했어요")));
    }
    
    public void initRecyclerViewPosition( ) {
        binding.postRecyclerview.smoothScrollToPosition(0);
    }
    
    private void init( ) {
        viewModel = new ViewModelProvider(this).get(RecentPostViewModel.class);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        binding.executePendingBindings( );
        
        viewModel.loadRecentPostNextPage(callback(
                posts -> {
                    if (posts.size( ) < 20) {
                        adapter.lastPage( );
                    }
                },
                e -> viewModel.setSnackbar("게시물을 불러오는 도중 문제가 발생했어요")
        ));
    }
}
