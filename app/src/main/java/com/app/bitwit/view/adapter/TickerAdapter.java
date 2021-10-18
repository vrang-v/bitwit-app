package com.app.bitwit.view.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import com.app.bitwit.databinding.ItemTickerBinding;
import lombok.var;

import java.util.ArrayList;
import java.util.List;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;

public class TickerAdapter extends Adapter<TickerAdapter.ViewHolder> {
    
    private final List<String> tickers = new ArrayList<>( );
    
    private Float tickerSize;
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var binding = ItemTickerBinding.inflate(LayoutInflater.from(parent.getContext( )), parent, false);
        return new ViewHolder(binding);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(tickers.get(position));
    }
    
    @Override
    public int getItemCount( ) {
        return tickers.size( );
    }
    
    public void updateTickers(List<String> tickers) {
        this.tickers.clear( );
        this.tickers.addAll(tickers);
        notifyDataSetChanged( );
    }
    
    public void setTickerSize(float size) {
        this.tickerSize = size;
    }
    
    class ViewHolder extends RecyclerView.ViewHolder {
        
        private ItemTickerBinding binding;
        
        public ViewHolder(ItemTickerBinding binding) {
            super(binding.getRoot( ));
            this.binding = binding;
        }
        
        public void bind(String ticker) {
            binding.setTicker(ticker);
            binding.executePendingBindings( );
            
            if (tickerSize != null) {
                binding.ticker.setTextSize(COMPLEX_UNIT_DIP, tickerSize);
            }
        }
    }
}
