package com.app.bitwit.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import com.app.bitwit.adapter.SearchAdapter.ViewHolder;
import com.app.bitwit.databinding.SearchItemBinding;
import com.app.bitwit.viewmodel.MutableLiveEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.var;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.app.bitwit.adapter.SearchAdapter.EventType.DECREMENT_BTN;
import static com.app.bitwit.adapter.SearchAdapter.EventType.INCREMENT_BTN;
import static com.app.bitwit.adapter.SearchAdapter.EventType.ROOT;

public class SearchAdapter extends Adapter<ViewHolder> {
    
    private final List<SearchItem> searchItems = new ArrayList<>( );
    
    private final MutableLiveEvent<SearchItemClick> searchItemClick = new MutableLiveEvent<>( );
    
    private int lastSelectedPosition = - 1;
    
    @Override
    public long getItemId(int position) {
        return searchItems.get(position).getStock( ).getId( );
    }
    
    @Nonnull
    @Override
    public ViewHolder onCreateViewHolder(@Nonnull ViewGroup parent, int viewType) {
        var binding = SearchItemBinding.inflate(LayoutInflater.from(parent.getContext( )), parent, false);
        return new ViewHolder(binding);
    }
    
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(searchItems.get(position));
    }
    
    @Override
    public int getItemCount( ) {
        return searchItems.size( );
    }
    
    public void updateVoteViews(List<SearchItem> searchItems) {
        this.searchItems.clear( );
        this.searchItems.addAll(searchItems);
        notifyDataSetChanged( );
    }
    
    public void setOnItemClickListener(LifecycleOwner lifecycleOwner, Consumer<SearchItemClick> onSearchItemClick) {
        searchItemClick.observe(lifecycleOwner, onSearchItemClick::accept);
    }
    
    public void showVoteScreen(int position) {
        if (lastSelectedPosition != - 1) {
            searchItems.get(lastSelectedPosition).setShowVoteScreen(false);
            notifyItemChanged(lastSelectedPosition);
        }
        searchItems.get(position).setShowVoteScreen(true);
        lastSelectedPosition = position;
        notifyItemChanged(position);
    }
    
    public void hideVoteScreen(int position) {
        searchItems.get(position).setShowVoteScreen(false);
        notifyItemChanged(position);
    }
    
    public enum EventType {
        INCREMENT_BTN, DECREMENT_BTN, ROOT
    }
    
    @Data
    @AllArgsConstructor
    public static class SearchItemClick {
        private EventType  eventType;
        private SearchItem searchItem;
        private View       view;
        private int        position;
    }
    
    class ViewHolder extends RecyclerView.ViewHolder {
        
        private final SearchItemBinding binding;
        
        private SearchItem searchItem;
        
        public ViewHolder(SearchItemBinding binding) {
            super(binding.getRoot( ));
            this.binding = binding;
            binding.root.setOnClickListener(v -> publishEvent(ROOT, v));
            binding.buttonIncrement.setOnClickListener(v -> publishEvent(INCREMENT_BTN, v));
            binding.buttonDecrement.setOnClickListener(v -> publishEvent(DECREMENT_BTN, v));
        }
        
        private void publishEvent(EventType eventType, View view) {
            searchItemClick.postValue(new SearchItemClick(eventType, searchItem, view, getAdapterPosition( )));
        }
        
        public void bind(SearchItem searchItem) {
            this.searchItem = searchItem;
            binding.setSearchItem(searchItem);
            binding.executePendingBindings( );
        }
    }
}
