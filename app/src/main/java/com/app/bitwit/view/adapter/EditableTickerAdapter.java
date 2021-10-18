package com.app.bitwit.view.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import com.app.bitwit.databinding.ItemEditableTickerBinding;
import lombok.var;

import java.util.List;

public class EditableTickerAdapter extends Adapter<EditableTickerAdapter.ViewHolder> {
    
    private final List<String> tickers;
    
    public EditableTickerAdapter(List<String> tickers) {
        this.tickers = tickers;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var binding = ItemEditableTickerBinding.inflate(LayoutInflater.from(parent.getContext( )), parent, false);
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
    
    class ViewHolder extends RecyclerView.ViewHolder {
        
        private ItemEditableTickerBinding binding;
        
        public ViewHolder(ItemEditableTickerBinding binding) {
            super(binding.getRoot( ));
            this.binding = binding;
        }
        
        public void bind(String ticker) {
            binding.cancelBtn.setOnClickListener(v -> tickers.remove(getAdapterPosition( )));
            binding.setTicker(ticker);
            binding.executePendingBindings( );
        }
    }
}
