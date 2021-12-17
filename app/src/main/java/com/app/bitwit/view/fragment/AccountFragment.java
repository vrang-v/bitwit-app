package com.app.bitwit.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.app.bitwit.R;
import com.app.bitwit.constant.ExtraKey;
import com.app.bitwit.databinding.FragmentAccountBinding;
import com.app.bitwit.util.IOUtils;
import com.app.bitwit.util.google.GoogleSignInUtils;
import com.app.bitwit.view.activity.auth.LoginActivity;
import com.app.bitwit.viewmodel.*;
import com.bumptech.glide.Glide;
import dagger.hilt.android.AndroidEntryPoint;
import lombok.var;

import java.io.FileNotFoundException;

import static android.app.Activity.RESULT_OK;
import static com.app.bitwit.util.StringUtils.hasText;
import static com.app.bitwit.util.livedata.LiveDataUtils.observe;

@AndroidEntryPoint
public class AccountFragment extends Fragment {
    
    private FragmentAccountBinding binding;
    private AccountViewModel       viewModel;
    
    private final ActivityResultLauncher<Intent> getProfileImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult( ), result -> {
                if (result.getResultCode( ) == RESULT_OK && result.getData( ) != null) {
                    var imageUri = result.getData( ).getData( );
                    try {
                        var inputStream = getContext( ).getContentResolver( ).openInputStream(imageUri);
                        viewModel.changeProfileImage(IOUtils.getFullPathFromUri(getContext( ), imageUri), inputStream)
                                 .subscribe( );
                    }
                    catch (FileNotFoundException e) {
                        e.printStackTrace( );
                    }
                }
            }
    );
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        init(inflater, container);
        
        observe(this, viewModel.getAccount( ), account -> {
            var profileImageUrl = account.getProfileImageUrl( );
            if (hasText(profileImageUrl)) {
                Glide.with(this)
                     .load(profileImageUrl)
                     .into(binding.profileImage);
            }
        });
        
        binding.logout.setOnClickListener(v ->
                signOut( )
        );
        
        binding.profileImage.setOnClickListener(v -> {
            var intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            getProfileImageLauncher.launch(intent);
        });
        
        binding.myPostLayout.setOnClickListener(v ->
                startFragment(new PostListFragment(MyPostListViewModel.class))
        );
        
        binding.likePostLayout.setOnClickListener(v ->
                startFragment(new PostListFragment(LikePostListViewModel.class))
        );
        
        var myCommentFragment = new CommentListFragment(MyCommentListViewModel.class);
        myCommentFragment.setTitle("내가 작성한 댓글");
        binding.myCommentLayout.setOnClickListener(v ->
                startFragment(myCommentFragment)
        );
        
        var likeCommentFragment = new CommentListFragment(LikeCommentListViewModel.class);
        likeCommentFragment.setTitle("내가 좋아요한 댓글");
        binding.likeCommentLayout.setOnClickListener(v ->
                startFragment(likeCommentFragment)
        );
        
        return binding.getRoot( );
    }
    
    private void startFragment(Fragment fragment) {
        getParentFragmentManager( )
                .beginTransaction( )
                .setCustomAnimations(
                        R.anim.slide_right_to_left_enter, 0,
                        0, R.anim.slide_left_to_right_exit
                )
                .replace(binding.getRoot( ).getId( ), fragment)
                .addToBackStack(null)
                .commit( );
    }
    
    private void signOut( ) {
        var googleSignInClient = GoogleSignInUtils.getGoogleSignInClient(getActivity( ));
        googleSignInClient
                .signOut( )
                .addOnCompleteListener(getActivity( ), task -> {
                    if (task.isSuccessful( )) {
                        viewModel.logout( )
                                 .onComplete(( ) -> {
                                     var intent = new Intent(getActivity( ), LoginActivity.class)
                                             .putExtra(ExtraKey.LOGOUT, true);
                                     startActivity(intent);
                                     getActivity( ).finish( );
                                 })
                                 .subscribe( );
                    }
                });
    }
    
    private void init(LayoutInflater inflater, ViewGroup container) {
        binding   = DataBindingUtil.inflate(inflater, R.layout.fragment_account, container, false);
        viewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        viewModel.loadAccount( )
                 .subscribe( );
        
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        binding.executePendingBindings( );
    }
}
