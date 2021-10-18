package com.app.bitwit.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import com.app.bitwit.view.adapter.PostPreviewAdapter.ViewHolder;
import com.app.bitwit.databinding.PostPreviewItemBinding;
import com.app.bitwit.viewmodel.MutableLiveEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.var;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PostPreviewAdapter extends Adapter<ViewHolder> {
    
    private final List<PostPreviewItem> postPreviewItems = new ArrayList<>( );
    
    private final MutableLiveEvent<PostPreviewItemClick> itemClick = new MutableLiveEvent<>( );
    
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        var binding = PostPreviewItemBinding.inflate(LayoutInflater.from(parent.getContext( )), parent, false);
        return new PostPreviewAdapter.ViewHolder(binding);
    }
    
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(postPreviewItems.get(position));
    }
    
    @Override
    public int getItemCount( ) {
        return postPreviewItems.size( );
    }
    
    public void updateVoteViews(List<PostPreviewItem> postPreviewItems) {
        this.postPreviewItems.clear( );
        this.postPreviewItems.addAll(postPreviewItems);
        notifyDataSetChanged( );
    }
    
    public void setOnItemClickListener(LifecycleOwner lifecycleOwner, Consumer<PostPreviewItemClick> onItemClick) {
        itemClick.observe(lifecycleOwner, onItemClick::accept);
    }
    
    @Data
    @AllArgsConstructor
    public static class PostPreviewItemClick {
        private PostPreviewItem postPreviewItem;
        private View            view;
        private int             position;
    }
    
    class ViewHolder extends RecyclerView.ViewHolder {
        
        private PostPreviewItemBinding binding;
        private PostPreviewItem        item;
        
        public ViewHolder(PostPreviewItemBinding binding) {
            super(binding.getRoot( ));
            this.binding = binding;
            this.binding.root.setOnClickListener(this::publishEvent);
        }
        
        private void publishEvent(View view) {
            itemClick.postValue(new PostPreviewItemClick(item, view, getAdapterPosition( )));
        }
        
        public void bind(PostPreviewItem item) {
            this.item = item;
            binding.setItem(item);
            binding.executePendingBindings( );
        }
    }
}
