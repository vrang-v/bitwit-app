package com.app.bitwit.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.app.bitwit.constant.ExtraKey;
import com.app.bitwit.databinding.FragmentCommentListBinding;
import com.app.bitwit.view.activity.PostActivity;
import com.app.bitwit.view.adapter.CommentDetailsAdapter;
import com.app.bitwit.viewmodel.CommentListViewModel;
import dagger.hilt.android.AndroidEntryPoint;
import lombok.Getter;
import lombok.var;
import org.jetbrains.annotations.NotNull;

@AndroidEntryPoint
public class CommentListFragment extends Fragment {
    
    private final Class<? extends CommentListViewModel> viewModelClass;
    
    @Getter
    private final MutableLiveData<String> title = new MutableLiveData<>( );
    
    private FragmentCommentListBinding binding;
    private CommentListViewModel       viewModel;
    
    private CommentDetailsAdapter adapter;
    
    public CommentListFragment(Class<? extends CommentListViewModel> viewModelClass) {
        this.viewModelClass = viewModelClass;
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        init(inflater, container);
    
        binding.backBtn.setOnClickListener(v ->
                getActivity( ).onBackPressed( )
        );
        
        adapter = new CommentDetailsAdapter( );
        adapter.addAdapterEventListener(this, event -> {
            var comment = event.getItem( );
            switch (event.getEvent( )) {
                case CLICK:
                    var intent = new Intent(getContext( ), PostActivity.class)
                            .putExtra(ExtraKey.POST_ID, comment.getPost( ).getId( ));
                    startActivity(intent);
                    break;
                case NEXT_PAGE:
                    viewModel.nextPage( )
                             .onSuccess(commentPage -> {
                                 if (commentPage.isLast( )) {
                                     adapter.lastPage( );
                                 }
                             })
                             .subscribe( );
                    break;
                case DELETE:
                    viewModel.deleteComment(comment.getId( ))
                             .onSuccess(x -> adapter.deleteItem(event.getPosition( )))
                             .subscribe( );
            }
        });
        binding.commentsRecycler.setAdapter(adapter);
        binding.commentsRecycler.setLayoutManager(new LinearLayoutManager(getContext( )));
        return binding.getRoot( );
    }
    
    public void setTitle(String title) {
        this.title.postValue(title);
    }
    
    private void init(@NotNull LayoutInflater inflater, ViewGroup container) {
        binding   = FragmentCommentListBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(viewModelClass);
        binding.setLifecycleOwner(this);
        binding.setFragment(this);
        binding.setViewModel(viewModel);
        binding.executePendingBindings( );
        viewModel.nextPage( )
                 .onSuccess(commentPage -> {
                     if (commentPage.isLast( )) {
                         adapter.lastPage( );
                     }
                 })
                 .subscribe( );
    }
}
