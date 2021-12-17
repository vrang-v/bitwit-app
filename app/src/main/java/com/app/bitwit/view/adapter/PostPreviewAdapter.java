package com.app.bitwit.view.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.app.bitwit.R;
import com.app.bitwit.databinding.ItemPostPreviewBinding;
import com.app.bitwit.dto.PostPreviewItem;
import com.app.bitwit.view.adapter.PostPreviewAdapter.PostPreviewAdapterEvent;
import com.app.bitwit.view.adapter.common.EventAdapter;
import com.app.bitwit.view.adapter.common.AdapterEventType;
import lombok.var;

import static android.view.animation.AnimationUtils.loadAnimation;

public class PostPreviewAdapter extends EventAdapter<PostPreviewItem, PostPreviewAdapterEvent> {
    
    @NonNull
    @Override
    public EventAdapter<PostPreviewItem, PostPreviewAdapterEvent>.EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var binding = ItemPostPreviewBinding.inflate(LayoutInflater.from(parent.getContext( )), parent, false);
        return new PostPreviewAdapter.PostPreviewAdapterViewHolder(binding);
    }
    
    public enum PostPreviewAdapterEvent implements AdapterEventType {
        CLICK
    }
    
    class PostPreviewAdapterViewHolder extends EventAdapter<PostPreviewItem, PostPreviewAdapterEvent>.EventViewHolder {
        
        private final ItemPostPreviewBinding binding;
        
        public PostPreviewAdapterViewHolder(ItemPostPreviewBinding binding) {
            super(binding.getRoot( ));
            this.binding = binding;
            this.binding.root.setOnClickListener(v -> publishEvent(PostPreviewAdapterEvent.CLICK, v));
        }
        
        @Override
        public void bind( ) {
            binding.setItem(item);
            binding.executePendingBindings( );
            setAnimation( );
        }
        
        private void setAnimation( ) {
            binding.getRoot( ).startAnimation(loadAnimation(itemView.getContext( ), R.anim.anim_fade));
        }
    }
}
