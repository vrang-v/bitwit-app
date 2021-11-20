package com.app.bitwit.view.activity;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.app.bitwit.R;
import com.app.bitwit.constant.ExtraKey;
import com.app.bitwit.databinding.ActivityPostBinding;
import com.app.bitwit.domain.Comment;
import com.app.bitwit.util.LoginAccount;
import com.app.bitwit.view.adapter.CommentAdapter;
import com.app.bitwit.view.adapter.TickerAdapter;
import com.app.bitwit.view.dialog.NicknameSettingDialog;
import com.app.bitwit.viewmodel.PostViewModel;
import com.app.bitwit.viewmodel.PostViewModel.CommentType;
import com.google.android.material.snackbar.Snackbar;
import dagger.hilt.android.AndroidEntryPoint;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.var;

import static com.app.bitwit.util.Callback.callback;
import static com.app.bitwit.util.LiveDataUtils.observe;
import static com.app.bitwit.util.LiveDataUtils.observeAll;
import static com.app.bitwit.util.LiveDataUtils.observeHasText;
import static com.app.bitwit.util.StringUtils.hasText;
import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT;

@AndroidEntryPoint
public class PostActivity extends AppCompatActivity {
    
    public static final int RESULT_DELETED = 10;
    
    private ActivityPostBinding binding;
    private PostViewModel       viewModel;
    
    private InputMethodManager inputMethodManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init( );
        inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        
        var postId = getIntent( ).getExtras( ).getLong(ExtraKey.POST_ID);
        
        viewModel.loadPost(postId);
        
        observeHasText(this, viewModel.getSnackbar( ), s ->
                Snackbar.make(binding.getRoot( ), s, LENGTH_SHORT).show( )
        );
        observe(this, viewModel.getCommentContent( ), s -> {
            viewModel.setCommentLengthInfo(s.trim( ).length( ) + "/100");
            binding.commentConfirm.setEnabled(hasText(s));
        });
        observeAll(this, viewModel.getCommentType( ), viewModel.getCommentDetail( ), (type, detail) -> {
            Comment selected = viewModel.getCommentSelected( ).getValue( );
            boolean hasText  = hasText(detail);
            switch (type) {
                case COMMENT:
                    viewModel.setCommentDetail(hasText ? "댓글을 작성하고 있어요" : "공백 댓글은 작성할 수 없어요");
                    break;
                case REPLY_REPLY:
                case COMMENT_REPLY:
                    String toName = selected.getWriter( ).getName( );
                    viewModel.setCommentDetail(hasText ? toName + " 님에게 남길 답글을 작성하고 있어요" : "공백 댓글은 작성할 수 없어요");
                    break;
                case EDIT:
                    viewModel.setCommentDetail(hasText ? "댓글을 수정하고 있어요" : "공백 댓글로 수정할 수 없어요");
                    break;
                default:
                    viewModel.setCommentDetail("");
                    break;
            }
            
        });
        binding.back.setOnClickListener(v ->
                finish( )
        );
        binding.delete.setOnClickListener(v ->
                viewModel.deletePost(callback(
                        empty -> {
                            setResult(RESULT_DELETED);
                            finish( );
                        },
                        e -> viewModel.setSnackbar("게시글을 삭제하는 도중 문제가 발생했어요")
                ))
        );
        binding.heartBtn.setOnClickListener(v ->
                viewModel.invertLike( )
        );
        binding.commentConfirm.setOnClickListener(v -> {
            hideSoftKeyboard( );
            if (! hasAccountName( )) {
                return;
            }
            if (viewModel.getCommentType( ).getValue( ) == CommentType.EDIT) {
                viewModel.updateComment(
                        empty -> viewModel.reloadComments( ),
                        e -> viewModel.setSnackbar("댓글을 수정하지 못했어요")
                );
            }
            else {
                viewModel.createComment(
                        comment -> viewModel.reloadComments( ),
                        e -> viewModel.getSnackbar( ).postValue("댓글을 등록하지 못했어요")
                );
            }
        });
        
        var tickerAdapter = new TickerAdapter( );
        tickerAdapter.setTickerSize(13);
        binding.tickerRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.tickerRecycler.setAdapter(tickerAdapter);
        
        var commentAdapter = new CommentAdapter( );
        commentAdapter.addEventListener(this, event -> {
            if (event.getComment( ).isDeleted( )) {
                return;
            }
            switch (event.getEventType( )) {
                case CLICK_COMMENT:
                    showSoftKeyboard( );
                    viewModel.setCommentContent("");
                    viewModel.setCommentSelected(event.getComment( ));
                    viewModel.setCommentType(CommentType.COMMENT_REPLY);
                    break;
                case CLICK_REPLY:
                    showSoftKeyboard( );
                    viewModel.setCommentContent("");
                    viewModel.setCommentSelected(event.getComment( ));
                    viewModel.setCommentType(CommentType.REPLY_REPLY);
                    break;
                case EDIT:
                    showSoftKeyboard( );
                    viewModel.setCommentContent(event.getComment( ).getContent( ));
                    viewModel.setCommentSelected(event.getComment( ));
                    viewModel.setCommentType(CommentType.EDIT);
                    break;
                case DELETE:
                    viewModel.deleteComment(event.getComment( ).getId( ));
                    break;
                case HEART:
                    if (event.getComment( ).isLike( )) {
                        viewModel.unlikeComment(event.getComment( ).getId( ), like -> viewModel.reloadComments( ));
                    }
                    else {
                        viewModel.likeComment(event.getComment( ).getId( ), like -> viewModel.reloadComments( ));
                    }
                    break;
                default:
            }
        });
        binding.commentRecyclerView.setAdapter(commentAdapter);
        binding.commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        setOnKeyboardStatusChangeEvent( );
    }
    
    private boolean hasAccountName( ) {
        LoginAccount account = viewModel.getAccount( );
        if (account == null || ! hasText(account.getName( ))) {
            createNicknameSettingDialog( );
            return false;
        }
        return true;
    }
    
    private void createNicknameSettingDialog( ) {
        NicknameSettingDialog
                .builder(this)
                .doOnSuccess(unused -> {
                    viewModel.loadAccount( );
                    hideSoftKeyboard( );
                    viewModel.setSnackbar("닉네임을 설정했어요");
                    viewModel.createComment(
                            comment -> viewModel.reloadComments( ),
                            throwable -> viewModel.getSnackbar( ).postValue("댓글을 등록하던 도중 문제가 발생했어요")
                    );
                })
                .doOnError(e -> viewModel.setSnackbar("닉네임을 설정하는 도중 문제가 발생했어요"))
                .doOnNotValid(( ) -> viewModel.setSnackbar("닉네임을 확인해주세요"))
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
        var boardVisible = getIntent( ).getExtras( ).getBoolean("boardVisible");
        binding.board.setVisibility(boardVisible ? View.VISIBLE : View.GONE);
    }
    
    @Override
    protected void onPause( ) {
        super.onPause( );
        viewModel.commitLike( );
        overridePendingTransition(R.anim.slide_left_to_right_enter, R.anim.slide_left_to_right_exit);
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
    
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Const {
        public static final String POST_ID = "postId";
    }
}