package com.app.bitwit.view.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.app.bitwit.databinding.ItemStockSearchBinding;
import com.app.bitwit.dto.SearchItem;
import com.app.bitwit.view.adapter.StockSearchAdapter.StockSearchEvent;
import com.app.bitwit.view.adapter.common.EventAdapter;
import com.app.bitwit.view.adapter.common.RecyclerViewEvent;
import lombok.var;

import static com.app.bitwit.view.adapter.StockSearchAdapter.StockSearchEvent.DECREMENT_BTN;
import static com.app.bitwit.view.adapter.StockSearchAdapter.StockSearchEvent.INCREMENT_BTN;
import static com.app.bitwit.view.adapter.StockSearchAdapter.StockSearchEvent.ROOT;

public class StockSearchAdapter extends EventAdapter<SearchItem, StockSearchEvent> {
    
    private int lastSelectedPosition = - 1;
    
    @Override
    public long getItemId(int position) {
        return items.get(position).getStock( ).getId( );
    }
    
    public void showVoteScreen(int position) {
        if (lastSelectedPosition != - 1) {
            items.get(lastSelectedPosition).setShowVoteScreen(false);
            notifyItemChanged(lastSelectedPosition);
        }
        items.get(position).setShowVoteScreen(true);
        lastSelectedPosition = position;
        notifyItemChanged(position);
    }
    
    public void hideVoteScreen(int position) {
        items.get(position).setShowVoteScreen(false);
        notifyItemChanged(position);
    }
    
    @NonNull
    @Override
    public EventAdapter<SearchItem, StockSearchEvent>.EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var binding = ItemStockSearchBinding.inflate(LayoutInflater.from(parent.getContext( )), parent, false);
        return new StockSearchViewHolder(binding);
    }
    
    public enum StockSearchEvent implements RecyclerViewEvent {
        INCREMENT_BTN, DECREMENT_BTN, ROOT
    }
    
    class StockSearchViewHolder extends EventAdapter<SearchItem, StockSearchEvent>.EventViewHolder {
        
        private final ItemStockSearchBinding binding;
        
        protected StockSearchViewHolder(ItemStockSearchBinding binding) {
            super(binding.getRoot( ));
            this.binding = binding;
            binding.root.setOnClickListener(v -> publishEvent(ROOT, v));
            binding.buttonIncrement.setOnClickListener(v -> publishEvent(INCREMENT_BTN, v));
            binding.buttonDecrement.setOnClickListener(v -> publishEvent(DECREMENT_BTN, v));
        }
        
        @Override
        public void bind( ) {
            binding.setSearchItem(item);
            binding.executePendingBindings( );
        }
    }
}
