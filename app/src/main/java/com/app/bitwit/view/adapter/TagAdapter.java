package com.app.bitwit.view.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import com.app.bitwit.databinding.ItemTagBinding;
import lombok.RequiredArgsConstructor;
import lombok.var;

import java.util.List;

@RequiredArgsConstructor
public class TagAdapter extends Adapter<TagAdapter.ViewHolder> {
    
    private final List<String> tags;
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var binding = ItemTagBinding.inflate(LayoutInflater.from(parent.getContext( )), parent, false);
        return new ViewHolder(binding);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(tags.get(position));
    }
    
    @Override
    public int getItemCount( ) {
        return tags.size( );
    }
    
    class ViewHolder extends RecyclerView.ViewHolder {
        
        private ItemTagBinding binding;
        
        public ViewHolder(ItemTagBinding binding) {
            super(binding.getRoot( ));
            this.binding = binding;
        }
        
        public void bind(String ticker) {
            binding.deleteBtn.setOnClickListener(v -> tags.remove(getAdapterPosition( )));
            binding.setTicker(ticker);
            binding.executePendingBindings( );
        }
    }
}
