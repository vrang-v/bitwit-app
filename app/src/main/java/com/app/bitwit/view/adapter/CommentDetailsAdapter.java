package com.app.bitwit.view.adapter;

import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.app.bitwit.databinding.ItemCommentDetailsBinding;
import com.app.bitwit.domain.Comment;
import com.app.bitwit.view.adapter.CommentDetailsAdapter.CommentDetailsAdapterEvent;
import com.app.bitwit.view.adapter.common.AdapterEventType;
import com.app.bitwit.view.adapter.common.EventAdapter;
import lombok.var;

import static com.app.bitwit.view.adapter.CommentDetailsAdapter.CommentDetailsAdapterEvent.CLICK;
import static com.app.bitwit.view.adapter.CommentDetailsAdapter.CommentDetailsAdapterEvent.DELETE;
import static com.app.bitwit.view.adapter.CommentDetailsAdapter.CommentDetailsAdapterEvent.NEXT_PAGE;

public class CommentDetailsAdapter extends EventAdapter<Comment, CommentDetailsAdapterEvent> {
    
    private boolean lastPage;
    
    @NonNull
    @Override
    public EventAdapter<Comment, CommentDetailsAdapterEvent>.EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var binding = ItemCommentDetailsBinding.inflate(LayoutInflater.from(parent.getContext( )), parent, false);
        return new CommentDetailsViewHolder(binding);
    }
    
    public void lastPage( ) {
        this.lastPage = true;
    }
    
    public void initPage( ) {
        this.lastPage = false;
    }
    
    public enum CommentDetailsAdapterEvent implements AdapterEventType {
        CLICK, NEXT_PAGE, DELETE
    }
    
    class CommentDetailsViewHolder extends EventAdapter<Comment, CommentDetailsAdapterEvent>.EventViewHolder implements OnCreateContextMenuListener {
        
        private final ItemCommentDetailsBinding binding;
        
        protected CommentDetailsViewHolder(ItemCommentDetailsBinding binding) {
            super(binding.getRoot( ));
            this.binding = binding;
            binding.getRoot( ).setOnCreateContextMenuListener(this);
            binding.getRoot( ).setOnClickListener(v -> publishEvent(CLICK, v));
        }
        
        @Override
        public void bind( ) {
            binding.setComment(item);
            binding.executePendingBindings( );
            
            if (items.size( ) - getAdapterPosition( ) == 5 && ! lastPage) {
                publishEvent(NEXT_PAGE, binding.getRoot( ));
            }
        }
        
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
            if (item.isEditable( )) {
                menu.add("삭제").setOnMenuItemClickListener(item -> {
                    publishEvent(DELETE, item.getActionView( ));
                    return true;
                });
            }
        }
    }
}
