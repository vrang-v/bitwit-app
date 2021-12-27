package com.app.bitwit.view.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.app.bitwit.databinding.ItemTickerBinding;
import com.app.bitwit.domain.Tag;
import com.app.bitwit.view.adapter.TickerAdapter.TickerAdapterEvent;
import com.app.bitwit.view.adapter.common.AdapterEventType;
import com.app.bitwit.view.adapter.common.EventAdapter;
import lombok.var;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;

public class TickerAdapter extends EventAdapter<Tag, TickerAdapterEvent> {
    
    private Float tickerFontSize;
    
    @NonNull
    @Override
    public EventAdapter<Tag, TickerAdapterEvent>.EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var binding = ItemTickerBinding.inflate(LayoutInflater.from(parent.getContext( )), parent, false);
        return new ViewHolder(binding);
    }
    
    public void setTickerFontSize(float tickerFontSize) {
        this.tickerFontSize = tickerFontSize;
    }
    
    public enum TickerAdapterEvent implements AdapterEventType { }
    
    public class ViewHolder extends EventAdapter<Tag, TickerAdapterEvent>.EventViewHolder {
        
        ItemTickerBinding binding;
        
        public ViewHolder(ItemTickerBinding binding) {
            super(binding.getRoot( ));
            this.binding = binding;
        }
        
        @Override
        public void bind( ) {
            binding.setTicker(item.getName( ));
            binding.executePendingBindings( );
            
            if (tickerFontSize != null) {
                binding.ticker.setTextSize(COMPLEX_UNIT_DIP, tickerFontSize);
            }
        }
    }
}
