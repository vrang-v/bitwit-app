package com.app.bitwit.view.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.app.bitwit.databinding.ItemTagBinding;
import com.app.bitwit.view.adapter.TagAdapter.TagAdapterEvent;
import com.app.bitwit.view.adapter.common.AdapterEventType;
import com.app.bitwit.view.adapter.common.EventAdapter;
import lombok.RequiredArgsConstructor;
import lombok.var;

@RequiredArgsConstructor
public class TagAdapter extends EventAdapter<String, TagAdapterEvent> {
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var binding = ItemTagBinding.inflate(LayoutInflater.from(parent.getContext( )), parent, false);
        return new ViewHolder(binding);
    }
    
    public enum TagAdapterEvent implements AdapterEventType {
        DELETE
    }
    
    class ViewHolder extends EventAdapter<String, TagAdapterEvent>.EventViewHolder {
        
        private final ItemTagBinding binding;
        
        public ViewHolder(ItemTagBinding binding) {
            super(binding.getRoot( ));
            this.binding = binding;
            binding.deleteBtn.setOnClickListener(v -> publishEvent(TagAdapterEvent.DELETE, v));
        }
        
        @Override
        public void bind( ) {
            binding.setTicker(item);
            binding.executePendingBindings( );
        }
    }
}
