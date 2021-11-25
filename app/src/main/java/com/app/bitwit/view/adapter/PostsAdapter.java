package com.app.bitwit.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.app.bitwit.databinding.ItemPostBinding;
import com.app.bitwit.databinding.ProgressBarBinding;
import com.app.bitwit.domain.Post;
import com.app.bitwit.view.adapter.common.AdapterEvent;
import com.app.bitwit.view.adapter.common.RecyclerViewEvent;
import com.app.bitwit.view.holder.ProgressBar;
import lombok.var;

import java.util.ArrayList;
import java.util.List;

import static androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL;
import static com.app.bitwit.view.adapter.PostsAdapter.PostsAdapterEvent.NEXT_PAGE;

public class PostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    
    private final List<Post> posts = new ArrayList<>( );
    
    private final MutableLiveData<AdapterEvent<Post, PostsAdapterEvent>> event = new MutableLiveData<>( );
    
    private boolean lastPage = false;
    
    @Override
    public long getItemId(int position) {
        return position < posts.size( ) ? posts.get(position).getId( ) : - 1;
    }
    
    @Override
    public int getItemViewType(int position) {
        return position < posts.size( ) ? 0 : 1;
    }
    
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return viewType == 0
               ? new ViewHolder(ItemPostBinding.inflate(LayoutInflater.from(parent.getContext( )), parent, false))
               : new ProgressBar(ProgressBarBinding.inflate(LayoutInflater.from(parent.getContext( )), parent, false));
    }
    
    
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position == posts.size( )) {
            return;
        }
        ViewHolder viewHolder = (ViewHolder)holder;
        viewHolder.bind(posts.get(position));
        if (! lastPage && position == posts.size( ) - 5) {
            viewHolder.publishNextPageEvent( );
        }
    }
    
    @Override
    public int getItemCount( ) {
        return lastPage ? posts.size( ) : posts.size( ) + 1;
    }
    
    public void updatePost(int index, Post post) {
        this.posts.set(index, post);
        notifyItemChanged(index);
    }
    
    public void updatePosts(List<Post> posts) {
        this.posts.clear( );
        this.posts.addAll(posts);
        notifyDataSetChanged( );
    }
    
    public void addEventListener(LifecycleOwner lifecycleOwner, Observer<AdapterEvent<Post, PostsAdapterEvent>> eventConsumer) {
        event.observe(lifecycleOwner, eventConsumer);
    }
    
    public void lastPage( ) {
        this.lastPage = true;
    }
    
    public void init( ) {
        this.lastPage = false;
    }
    
    public enum PostsAdapterEvent implements RecyclerViewEvent {
        CLICK, HEART, NEXT_PAGE
    }
    
    class ViewHolder extends RecyclerView.ViewHolder {
        
        private final ItemPostBinding binding;
        
        private Post post;
        
        public ViewHolder(@NonNull ItemPostBinding binding) {
            super(binding.getRoot( ));
            this.binding = binding;
            binding.root.setOnClickListener(v -> publishEvent(PostsAdapterEvent.CLICK, v));
            binding.heartBtn.setOnClickListener(v -> publishEvent(PostsAdapterEvent.HEART, v));
        }
        
        public void publishNextPageEvent( ) {
            event.postValue(new AdapterEvent<>(null, NEXT_PAGE, binding.getRoot( ), getAdapterPosition( )));
        }
        
        private void publishEvent(PostsAdapterEvent type, View view) {
            event.postValue(new AdapterEvent<>(post, type, view, getAdapterPosition( )));
        }
        
        public void bind(Post post) {
            this.post = post;
            var adapter = new TickerAdapter( );
            binding.tickerRecycler.setAdapter(adapter);
            binding.tickerRecycler.setLayoutManager(new LinearLayoutManager(itemView.getContext( ), HORIZONTAL, true));
            binding.setPost(post);
            binding.executePendingBindings( );
        }
    }
}
