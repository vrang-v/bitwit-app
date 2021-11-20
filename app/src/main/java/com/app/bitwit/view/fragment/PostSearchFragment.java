package com.app.bitwit.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
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
import com.app.bitwit.databinding.FragmentPostSearchBinding;
import com.app.bitwit.domain.Like;
import com.app.bitwit.domain.Post;
import com.app.bitwit.util.Callback;
import com.app.bitwit.view.activity.PostActivity;
import com.app.bitwit.view.adapter.PostsAdapter;
import com.app.bitwit.viewmodel.PostSearchViewModel;
import dagger.hilt.android.AndroidEntryPoint;
import lombok.var;

import static com.app.bitwit.util.Callback.callback;
import static com.app.bitwit.util.LiveDataUtils.observeAll;
import static com.app.bitwit.util.StringUtils.hasText;

@AndroidEntryPoint
public class PostSearchFragment extends Fragment {
    
    private FragmentPostSearchBinding binding;
    private PostSearchViewModel       viewModel;
    
    private ActivityResultLauncher<Intent> launcher;
    
    private long lastClickedPostId;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        launcher = registerForActivityResult(new StartActivityForResult( ), result -> {
            if (result.getResultCode( ) == PostActivity.RESULT_DELETED) {
                viewModel.setSnackbar("게시글을 삭제했어요");
                viewModel.removePost(lastClickedPostId);
            }
        });
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding   = DataBindingUtil.inflate(inflater, R.layout.fragment_post_search, container, false);
        viewModel = new ViewModelProvider(this).get(PostSearchViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        binding.executePendingBindings( );
        
        binding.getRoot( ).setOnTouchListener((v, event) -> true);
        binding.back.setOnClickListener(v -> getParentFragmentManager( ).popBackStack( ));
        setOnKeyboardStatusChangeEvent( );
        
        observeAll(this, viewModel.getPosts( ), viewModel.getSearchWord( ), (posts, word) -> {
            binding.swipeLayout.setEnabled(! posts.isEmpty( ));
            binding.noSearchResultText.setVisibility(hasText(word) && posts.isEmpty( ) ? View.VISIBLE : View.INVISIBLE);
            binding.postRecycler.setVisibility(posts.isEmpty( ) ? View.INVISIBLE : View.VISIBLE);
        });
        
        var adapter = new PostsAdapter( );
        adapter.setHasStableIds(true);
        
        binding.inputSearchWord.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction( ) == KeyEvent.ACTION_DOWN) {
                adapter.init( );
                binding.inputSearchWord.setCursorVisible(false);
                viewModel.searchPostPage(callback(
                        posts -> {
                            if (posts.size( ) < 20) {
                                adapter.lastPage( );
                            }
                        },
                        e -> viewModel.setSnackbar("게시물을 불러오는 도중 문제가 발생했어요")
                ));
                return true;
            }
            return false;
        });
        
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
                    viewModel.loadNextPage(callback(
                            posts -> {
                                if (posts.size( ) < 15) {
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
        
        binding.swipeLayout.setOnRefreshListener(( ) -> {
            adapter.init( );
            viewModel.searchPostPage(
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
        
        binding.postRecycler.setAdapter(adapter);
        binding.postRecycler.setLayoutManager(new LinearLayoutManager(getContext( )));
        return binding.getRoot( );
    }
    
    public void initRecyclerViewPosition( ) {
        binding.postRecycler.smoothScrollToPosition(0);
    }
    
    private void setOnKeyboardStatusChangeEvent( ) {
        binding.getRoot( ).getViewTreeObserver( ).addOnGlobalLayoutListener(( ) -> {
            int     rootHeight = binding.getRoot( ).getRootView( ).getHeight( );
            int     height     = binding.getRoot( ).getHeight( );
            boolean keyboardOn = (double)height / rootHeight < 0.8;
            if (keyboardOn) {
                binding.noSearchResultText.setVisibility(View.INVISIBLE);
                binding.inputSearchWord.setCursorVisible(true);
                binding.postRecycler.setVisibility(View.GONE);
            }
        });
    }
}
