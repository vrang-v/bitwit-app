package com.app.bitwit.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.app.bitwit.R;
import com.app.bitwit.constant.ExtraKey;
import com.app.bitwit.databinding.ActivityPostBinding;
import com.app.bitwit.domain.Comment;
import com.app.bitwit.dto.LoginAccount;
import com.app.bitwit.view.adapter.CommentAdapter;
import com.app.bitwit.view.adapter.TickerAdapter;
import com.app.bitwit.view.dialog.NicknameSettingDialog;
import com.app.bitwit.viewmodel.PostViewModel;
import com.app.bitwit.viewmodel.PostViewModel.CommentType;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import dagger.hilt.android.AndroidEntryPoint;
import lombok.var;

import static com.app.bitwit.util.StringUtils.hasText;
import static com.app.bitwit.util.livedata.LiveDataUtils.observe;
import static com.app.bitwit.util.livedata.LiveDataUtils.observeAll;
import static com.app.bitwit.util.livedata.LiveDataUtils.observeHasText;
import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT;

@AndroidEntryPoint
public class PostActivity extends AppCompatActivity {
    
    public static final int RESULT_DELETED = 10;
    
    private InputMethodManager inputMethodManager;
    
    private ActivityPostBinding binding;
    private PostViewModel       viewModel;
    
    private final ActivityResultLauncher<Intent> postEditingLauncher = registerForActivityResult(
            new StartActivityForResult( ), result -> {
                if (result.getResultCode( ) == PostEditingActivity.RESULT_SUCCESS) {
                    viewModel.loadPost( )
                             .onSuccess(post -> viewModel.setSnackbar("게시글이 수정되었습니다"))
                             .subscribe( );
                }
            }
    );
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init( );
        
        observeHasText(this, viewModel.getSnackbar( ), s ->
                Snackbar.make(binding.getRoot( ), s, LENGTH_SHORT).show( )
        );
        
        observe(this, viewModel.getPost( ), post -> {
            var profileImageUrl = post.getWriter( ).getProfileImageUrl( );
            if (hasText(profileImageUrl)) {
                Glide.with(this)
                     .load(profileImageUrl)
                     .into(binding.profileImage);
            }
        });
        
        observe(this, viewModel.getCommentContent( ), s -> {
            viewModel.setCommentLengthInfo(s.trim( ).length( ) + "/100");
            binding.commentConfirm.setEnabled(hasText(s));
        });
        
        observeAll(this, viewModel.getCommentContent( ), viewModel.getCommentType( ), (comment, type) -> {
            Comment selected            = viewModel.getCommentSelected( ).getValue( );
            boolean hasText             = hasText(comment);
            String  blankCommentMessage = "공백 댓글은 작성할 수 없어요";
            switch (type) {
                case COMMENT:
                    viewModel.setCommentDetail(hasText ? "댓글을 작성하고 있어요" : blankCommentMessage);
                    break;
                case COMMENT_REPLY:
                case REPLY_REPLY:
                    String toName = selected.getWriter( ).getName( );
                    String myName = viewModel.getAccount( ).getValue( ).getName( );
                    if (toName.equals(myName)) {
                        viewModel.setCommentDetail(hasText ? "나에게 남길 답글을 작성하고 있어요" : blankCommentMessage);
                    }
                    else {
                        viewModel.setCommentDetail(hasText ? toName + " 님에게 남길 답글을 작성하고 있어요" : blankCommentMessage);
                    }
                    break;
                case EDIT:
                    viewModel.setCommentDetail(hasText ? "댓글을 수정하고 있어요" : "공백 댓글로 수정할 수 없어요");
                    break;
            }
        });
        
        binding.backBtn.setOnClickListener(v ->
                finish( )
        );
        
        binding.editBtn.setOnClickListener(v -> {
            var intent = new Intent(this, PostEditingActivity.class)
                    .putExtra("postId", viewModel.getPost( ).getValue( ).getId( ));
            postEditingLauncher.launch(intent);
            overridePendingTransition(R.anim.slide_right_to_left_enter, R.anim.slide_right_to_left_exit);
        });
        
        binding.deleteBtn.setOnClickListener(v ->
                viewModel.deletePost( )
                         .onSuccess(empty -> {
                             setResult(RESULT_DELETED);
                             finish( );
                         })
                         .subscribe( )
        );
        
        binding.heartBtn.setOnClickListener(v ->
                viewModel.invertLike( )
        );
        
        binding.commentConfirm.setOnClickListener(v -> {
            hideSoftKeyboard( );
            if (! hasAccountName(viewModel.getAccount( ).getValue( ))) {
                showNicknameSettingDialog( );
                return;
            }
            if (viewModel.getCommentType( ).getValue( ) == CommentType.EDIT) {
                viewModel.updateComment( )
                         .then(empty -> viewModel.reloadComments( ))
                         .subscribe( );
            }
            else {
                viewModel.createComment( )
                         .then(comment -> viewModel.reloadComments( ))
                         .subscribe( );
            }
        });
        
        var tickerAdapter = new TickerAdapter( );
        tickerAdapter.setTickerFontSize(13);
        binding.tickerRecycler.setAdapter(tickerAdapter);
        binding.tickerRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        
        var commentAdapter = new CommentAdapter( );
        commentAdapter.addAdapterEventListener(this, event -> {
            var comment = event.getItem( );
            if (comment.isDeleted( )) {
                return;
            }
            switch (event.getEvent( )) {
                case CLICK_COMMENT:
                    showSoftKeyboard( );
                    viewModel.setCommentContent("");
                    viewModel.setCommentSelected(comment);
                    viewModel.setCommentType(CommentType.COMMENT_REPLY);
                    break;
                case CLICK_REPLY:
                    showSoftKeyboard( );
                    viewModel.setCommentContent("");
                    viewModel.setCommentSelected(comment);
                    viewModel.setCommentType(CommentType.REPLY_REPLY);
                    break;
                case EDIT:
                    showSoftKeyboard( );
                    viewModel.setCommentContent(comment.getContent( ));
                    viewModel.setCommentSelected(comment);
                    viewModel.setCommentType(CommentType.EDIT);
                    break;
                case DELETE:
                    viewModel.deleteComment(comment.getId( ))
                             .onSuccess(empty -> Snackbar.make(binding.getRoot( ), "댓글을 삭제했어요", LENGTH_SHORT).show( ))
                             .then(empty -> viewModel.reloadComments( ))
                             .subscribe( );
                    break;
                case HEART:
                    if (comment.isLike( )) {
                        viewModel.unlikeComment(comment.getId( ))
                                 .then(empty -> viewModel.reloadComments( ))
                                 .subscribe( );
                    }
                    else {
                        viewModel.likeComment(comment.getId( ))
                                 .then(empty -> viewModel.reloadComments( ))
                                 .subscribe( );
                    }
                    break;
                default:
            }
        });
        binding.commentRecyclerView.setAdapter(commentAdapter);
        binding.commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        binding.swipeLayout.setOnRefreshListener(( ) ->
                viewModel.loadPost( )
                         .onSuccess(post -> binding.swipeLayout.setRefreshing(false))
                         .subscribe( )
        );
    }
    
    private boolean hasAccountName(LoginAccount account) {
        return account != null && hasText(account.getName( ));
    }
    
    private void showNicknameSettingDialog( ) {
        NicknameSettingDialog
                .builder(this)
                .doOnSuccess(unused -> {
                    hideSoftKeyboard( );
                    viewModel.setSnackbar("닉네임을 설정했어요");
                    viewModel.loadAccount( )
                             .then(loginAccount -> viewModel.createComment( ))
                             .then(comment -> viewModel.reloadComments( ))
                             .subscribe( );
                })
                .doOnError(e -> viewModel.setSnackbar("닉네임을 설정하는 도중 문제가 발생했어요"))
                .build( )
                .show( );
    }
    
    private void setOnKeyboardStatusChangeEvent( ) {
        binding.root.getViewTreeObserver( ).addOnGlobalLayoutListener(( ) -> {
            int     rootHeight = binding.root.getRootView( ).getHeight( );
            int     height     = binding.root.getHeight( );
            boolean keyboardOn = (double)height / rootHeight < 0.9;
            binding.commentInfo.setVisibility(keyboardOn ? View.VISIBLE : View.GONE);
            if (! keyboardOn && ! hasText(viewModel.getCommentContent( ).getValue( ))) {
                viewModel.setCommentSelected(null);
                viewModel.setCommentType(CommentType.COMMENT);
            }
        });
    }
    
    private void init( ) {
        binding   = DataBindingUtil.setContentView(this, R.layout.activity_post);
        viewModel = new ViewModelProvider(this).get(PostViewModel.class);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        binding.executePendingBindings( );
        inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        setOnKeyboardStatusChangeEvent( );
        
        var postId = getIntent( ).getExtras( ).getLong(ExtraKey.POST_ID);
        viewModel.setPostId(postId);
        viewModel.loadPost( )
                 .onError(e -> viewModel.setSnackbar("게시물을 불러오는 도중 문제가 발생했어요"))
                 .subscribe( );
        
        var boardVisible = getIntent( ).getExtras( ).getBoolean("boardVisible");
        binding.board.setVisibility(boardVisible ? View.VISIBLE : View.GONE);
    }
    
    @Override
    protected void onPause( ) {
        super.onPause( );
        viewModel.commitLike( );
    }
    
    private void showSoftKeyboard( ) {
        binding.commentInput.requestFocus( );
        binding.commentInput.postDelayed(
                ( ) -> inputMethodManager.showSoftInput(binding.commentInput, InputMethodManager.SHOW_IMPLICIT), 30L
        );
    }
    
    private void hideSoftKeyboard( ) {
        inputMethodManager.hideSoftInputFromWindow(binding.commentInput.getWindowToken( ), 0);
    }
    
    @Override
    public void onBackPressed( ) {
        super.onBackPressed( );
        overridePendingTransition(R.anim.slide_left_to_right_enter, R.anim.slide_left_to_right_exit);
    }
}
