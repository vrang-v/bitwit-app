package com.app.bitwit.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.app.bitwit.R;
import com.app.bitwit.data.source.local.entity.VoteItem;
import com.app.bitwit.databinding.ItemVoteBinding;
import com.app.bitwit.dto.VoteItemDto;
import com.app.bitwit.view.adapter.VoteItemAdapter.VoteItemAdapterEvent;
import com.app.bitwit.view.adapter.common.EventAdapter;
import com.app.bitwit.view.adapter.common.AdapterEventType;
import lombok.var;

import static android.view.animation.AnimationUtils.loadAnimation;
import static com.app.bitwit.view.adapter.VoteItemAdapter.VoteItemAdapterEvent.DECREMENT_BTN_CLICK;
import static com.app.bitwit.view.adapter.VoteItemAdapter.VoteItemAdapterEvent.INCREMENT_BTN_CLICK;
import static com.app.bitwit.view.adapter.VoteItemAdapter.VoteItemAdapterEvent.LAYOUT_CLICK;

public class VoteItemAdapter extends EventAdapter<VoteItem, VoteItemAdapterEvent> {
    
    @Override
    public long getItemId(int position) {
        return items.get(position).getId( );
    }
    
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var binding = ItemVoteBinding.inflate(LayoutInflater.from(parent.getContext( )), parent, false);
        return new VoteItemHolder(binding);
    }
    
    public enum VoteItemAdapterEvent implements AdapterEventType {
        INCREMENT_BTN_CLICK, DECREMENT_BTN_CLICK, LAYOUT_CLICK
    }
    
    public class VoteItemHolder extends EventViewHolder {
        
        private final ItemVoteBinding binding;
        
        protected VoteItemHolder(ItemVoteBinding binding) {
            super(binding.getRoot( ));
            this.binding = binding;
            
            binding.layout.setOnClickListener(v -> publishEvent(LAYOUT_CLICK, v));
            binding.buttonIncrement.setOnClickListener(v -> publishEvent(INCREMENT_BTN_CLICK, v));
            binding.buttonDecrement.setOnClickListener(v -> publishEvent(DECREMENT_BTN_CLICK, v));
        }
        
        @Override
        public void bind( ) {
            var voteItem = new VoteItemDto(item);
            binding.setVoteItem(voteItem);
            binding.koreanName.setSelected(true);
            binding.executePendingBindings( );
            
            startAnimation(itemView.getContext( ), item);
        }
        
        private void startAnimation(Context context, VoteItem voteItem) {
            binding.participantCount.startAnimation(loadAnimation(context, R.anim.anim_fade));
            binding.stockPrice.startAnimation(loadAnimation(context, R.anim.anim_fade));
            binding.realTimeFluctuation.startAnimation(loadAnimation(context, R.anim.anim_fade_out));
            
            if (voteItem.isApplyAnim( )) {
                binding.layout.startAnimation(loadAnimation(context, R.anim.anim_fade_long));
                binding.incrementBar.startAnimation(loadAnimation(context, R.anim.anim_fade_left));
                binding.decrementBar.startAnimation(loadAnimation(context, R.anim.anim_fade_right));
                voteItem.setApplyAnim(false);
            }
        }
    }
}
