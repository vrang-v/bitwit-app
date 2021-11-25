package com.app.bitwit.view.adapter;

import android.util.AndroidRuntimeException;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.app.bitwit.databinding.ItemCommentBinding;
import com.app.bitwit.databinding.ItemInnerCommentBinding;
import com.app.bitwit.domain.Comment;
import com.app.bitwit.view.adapter.CommentAdapter.CommentAdapterEvent;
import com.app.bitwit.view.adapter.common.EventAdapter;
import com.app.bitwit.view.adapter.common.RecyclerViewEvent;

import static com.app.bitwit.view.adapter.CommentAdapter.CommentAdapterEvent.*;

public class CommentAdapter extends EventAdapter<Comment, CommentAdapterEvent> {
    
    @Override
    public int getItemViewType(int position) {
        return items.get(position).getDepth( );
    }
    
    @NonNull
    @Override
    public EventAdapter<Comment, CommentAdapterEvent>.EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                return new CommentViewHolder(
                        ItemCommentBinding.inflate(LayoutInflater.from(parent.getContext( )), parent, false)
                );
            case 1:
                return new CommentViewHolder(
                        ItemInnerCommentBinding.inflate(LayoutInflater.from(parent.getContext( )), parent, false)
                );
            default:
                throw new AndroidRuntimeException( );
        }
    }
    
    public enum CommentAdapterEvent implements RecyclerViewEvent {
        CLICK_COMMENT, CLICK_REPLY, EDIT, DELETE, HEART
    }
    
    class CommentViewHolder extends EventAdapter<Comment, CommentAdapterEvent>.EventViewHolder implements OnCreateContextMenuListener {
        
        private final boolean isRoot;
        private final boolean showContour;
        
        private ItemCommentBinding      binding;
        private ItemInnerCommentBinding replyBinding;
        
        public CommentViewHolder(ItemCommentBinding binding) {
            super(binding.getRoot( ));
            this.binding     = binding;
            this.isRoot      = true;
            this.showContour = getAdapterPosition( ) != 0;
            binding.getRoot( ).setOnCreateContextMenuListener(this);
            binding.reply.setOnClickListener(v -> publishEvent(CLICK_COMMENT, v));
            binding.heartBtn.setOnClickListener(v -> publishEvent(HEART, v));
        }
        
        public CommentViewHolder(ItemInnerCommentBinding binding) {
            super(binding.getRoot( ));
            this.replyBinding = binding;
            this.isRoot       = false;
            this.showContour  = false;
            binding.getRoot( ).setOnCreateContextMenuListener(this);
            binding.reply.setOnClickListener(v -> publishEvent(CLICK_REPLY, v));
            binding.heartBtn.setOnClickListener(v -> publishEvent(HEART, v));
        }
        
        @Override
        public void bind( ) {
            if (isRoot) {
                binding.setComment(item);
                binding.executePendingBindings( );
                binding.contour.setVisibility(showContour ? View.VISIBLE : View.INVISIBLE);
            }
            else {
                replyBinding.setComment(item);
                replyBinding.executePendingBindings( );
            }
        }
        
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
            if (item.isEditable( )) {
                menu.add("수정").setOnMenuItemClickListener(item -> {
                    publishEvent(EDIT, item.getActionView( ));
                    return true;
                });
                menu.add("삭제").setOnMenuItemClickListener(item -> {
                    publishEvent(DELETE, item.getActionView( ));
                    return true;
                });
            }
        }
    }
}
