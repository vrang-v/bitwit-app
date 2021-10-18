package com.app.bitwit.view.adapter;

import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.app.bitwit.databinding.CommentItemBinding;
import com.app.bitwit.databinding.InnerCommentItemBinding;
import com.app.bitwit.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CommentAdapter extends Adapter<RecyclerView.ViewHolder> {
    
    private final List<Comment> comments = new ArrayList<>( );
    
    private final MutableLiveData<CommentAdapterEvent> event = new MutableLiveData<>( );
    
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                return new CommentViewHolder(
                        CommentItemBinding.inflate(LayoutInflater.from(parent.getContext( )), parent, false)
                );
            case 1:
                return new CommentViewHolder(
                        InnerCommentItemBinding.inflate(LayoutInflater.from(parent.getContext( )), parent, false)
                );
            default:
                return null;
        }
    }
    
    @Override
    public int getItemViewType(int position) {
        return comments.get(position).getDepth( );
    }
    
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        switch (comment.getDepth( )) {
            case 0:
                ((CommentViewHolder)holder).bind(comment, position != 0);
                break;
            case 1:
                ((CommentViewHolder)holder).bind(comment, false);
                break;
            default:
                break;
        }
    }
    
    @Override
    public int getItemCount( ) {
        return comments.size( );
    }
    
    public void addEventListener(LifecycleOwner lifecycleOwner, Consumer<CommentAdapterEvent> onEvent) {
        event.observe(lifecycleOwner, onEvent::accept);
    }
    
    public void updateComments(List<Comment> comments) {
        this.comments.clear( );
        this.comments.addAll(comments);
        notifyDataSetChanged( );
    }
    
    public enum EventType {
        CLICK_COMMENT, CLICK_REPLY, EDIT, DELETE, HEART
    }
    
    @Data
    @AllArgsConstructor
    public static class CommentAdapterEvent {
        private EventType eventType;
        private Comment   comment;
        private View      view;
        private int       position;
    }
    
    class CommentViewHolder extends RecyclerView.ViewHolder implements OnCreateContextMenuListener {
        
        private final boolean                 isRoot;
        private       CommentItemBinding      binding;
        private       InnerCommentItemBinding replyBinding;
        private       Comment                 comment;
        
        public CommentViewHolder(CommentItemBinding binding) {
            super(binding.getRoot( ));
            this.binding = binding;
            this.isRoot  = true;
            binding.getRoot( ).setOnCreateContextMenuListener(this);
            binding.reply.setOnClickListener(v -> publishEvent(EventType.CLICK_COMMENT, v));
            binding.heartBtn.setOnClickListener(v -> publishEvent(EventType.HEART, v));
        }
        
        public CommentViewHolder(InnerCommentItemBinding binding) {
            super(binding.getRoot( ));
            this.replyBinding = binding;
            this.isRoot       = false;
            binding.getRoot( ).setOnCreateContextMenuListener(this);
            binding.reply.setOnClickListener(v -> publishEvent(EventType.CLICK_REPLY, v));
            binding.heartBtn.setOnClickListener(v -> publishEvent(EventType.HEART, v));
        }
        
        private void publishEvent(EventType eventType, View view) {
            event.postValue(new CommentAdapterEvent(eventType, comment, view, getAdapterPosition( )));
        }
        
        public void bind(Comment comment, boolean showContour) {
            this.comment = comment;
            if (isRoot) {
                binding.setComment(comment);
                binding.executePendingBindings( );
                binding.contour.setVisibility(showContour ? View.VISIBLE : View.INVISIBLE);
            }
            else {
                replyBinding.setComment(comment);
                replyBinding.executePendingBindings( );
            }
        }
        
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
            if (comment.isEditable( )) {
                menu.add("수정").setOnMenuItemClickListener(item -> {
                    publishEvent(EventType.EDIT, item.getActionView( ));
                    return true;
                });
                menu.add("삭제").setOnMenuItemClickListener(item -> {
                    publishEvent(EventType.DELETE, item.getActionView( ));
                    return true;
                });
            }
        }
    }
}
