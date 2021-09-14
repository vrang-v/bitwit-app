package com.app.bitwit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import com.app.bitwit.R;
import com.app.bitwit.adapter.VoteItemAdapter.ViewHolder;
import com.app.bitwit.data.source.local.entity.VoteItem;
import com.app.bitwit.databinding.VoteItemBinding;
import com.app.bitwit.viewmodel.MutableLiveEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.var;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.app.bitwit.adapter.VoteItemAdapter.EventType.DECREMENT_BTN;
import static com.app.bitwit.adapter.VoteItemAdapter.EventType.INCREMENT_BTN;
import static com.app.bitwit.adapter.VoteItemAdapter.EventType.LAYOUT;

public class VoteItemAdapter extends Adapter<ViewHolder> {
    
    private final List<VoteItem> voteItems = new ArrayList<>( );
    
    private final MutableLiveEvent<VoteItemClick> voteItemClick = new MutableLiveEvent<>( );
    
    @Override
    public long getItemId(int position) {
        return voteItems.get(position).getId( );
    }
    
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var binding = VoteItemBinding.inflate(LayoutInflater.from(parent.getContext( )), parent, false);
        return new ViewHolder(binding);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(voteItems.get(position));
    }
    
    public void updateVoteViews(List<VoteItem> newVoteItems) {
        voteItems.clear( );
        voteItems.addAll(newVoteItems);
        notifyDataSetChanged( );
    }
    
    @Override
    public int getItemCount( ) {
        return voteItems.size( );
    }
    
    public void setOnItemClickListener(LifecycleOwner lifecycleOwner, Consumer<VoteItemClick> onVoteItemClick) {
        voteItemClick.observe(lifecycleOwner, onVoteItemClick::accept);
    }
    
    public enum EventType {
        INCREMENT_BTN, DECREMENT_BTN, LAYOUT
    }
    
    @Data
    @AllArgsConstructor
    public static class VoteItemClick {
        private EventType eventType;
        private VoteItem  voteItem;
        private View      view;
        private int       position;
    }
    
    class ViewHolder extends RecyclerView.ViewHolder {
        
        private final VoteItemBinding binding;
    
        private VoteItem voteItem;
        
        public ViewHolder(VoteItemBinding binding) {
            super(binding.getRoot( ));
            this.binding = binding;
            
            binding.layout.setOnClickListener(v -> publishEvent(LAYOUT, v));
            binding.buttonIncrement.setOnClickListener(v -> publishEvent(INCREMENT_BTN, v));
            binding.buttonDecrement.setOnClickListener(v -> publishEvent(DECREMENT_BTN, v));
        }
        
        private void publishEvent(EventType eventType, View view) {
            voteItemClick.postValue(new VoteItemClick(eventType, voteItem, view, getAdapterPosition( )));
        }
        
        void bind(VoteItem voteItem) {
            this.voteItem = voteItem;
            
            var viewModel = new VoteItemViewModel(voteItem);
            binding.setViewModel(viewModel);
            binding.koreanName.setSelected(true);
            binding.executePendingBindings( );
            
            startAnimation(itemView.getContext( ), voteItem);
        }
        
        private void startAnimation(Context context, VoteItem voteItem) {
            binding.participantCount.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_fade));
            binding.stockPrice.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_fade));
            binding.realTimeFluctuation.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_fade_out));
            
            if (voteItem.isApplyAnim( )) {
                binding.layout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_fade_long));
                binding.incrementBar.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_fade_left));
                binding.decrementBar.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_fade_right));
                voteItem.setApplyAnim(false);
            }
        }
    }
}