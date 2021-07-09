package com.app.bitwit.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import com.app.bitwit.R;
import com.app.bitwit.adapter.MyRecyclerAdapter.ViewHolder;
import com.app.bitwit.domain.Vote;
import lombok.var;

import java.util.List;

public class MyRecyclerAdapter extends Adapter<ViewHolder>
{
    private List<Vote> votes;
    
    private IncrementClickListener incrementClickListener = null;
    private DecrementClickListener decrementClickListener = null;
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        var view = LayoutInflater.from(parent.getContext( ))
                                 .inflate(R.layout.item_recyclerview, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.onBind(votes.get(position));
    }
    
    public Vote getVote(int position)
    {
        return votes.get(position);
    }
    
    public void setVotes(List<Vote> votes)
    {
        this.votes = votes;
        notifyDataSetChanged( );
    }
    
    @Override
    public int getItemCount( )
    {
        return votes.size( );
    }
    
    public void setOnIncrementClickListener(IncrementClickListener listener)
    {
        this.incrementClickListener = listener;
    }
    
    public void setOnDecrementClickListener(DecrementClickListener listener)
    {
        this.decrementClickListener = listener;
    }
    
    public interface IncrementClickListener
    {
        void onIncrementClick(View view, int position);
    }
    
    public interface DecrementClickListener
    {
        void onDecrementClick(View view, int position);
    }
    
    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView name;
        TextView message;
        Button   buttonIncrement;
        Button   buttonDecrement;
        
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            name            = itemView.findViewById(R.id.name);
            message         = itemView.findViewById(R.id.message);
            buttonIncrement = itemView.findViewById(R.id.button_increment);
            buttonDecrement = itemView.findViewById(R.id.button_decrement);
            
            buttonIncrement.setOnClickListener(view -> {
                int position = getAdapterPosition( );
                if (position != RecyclerView.NO_POSITION) {
                    incrementClickListener.onIncrementClick(view, position);
                }
            });
            buttonDecrement.setOnClickListener(view -> {
                int position = getAdapterPosition( );
                if (position != RecyclerView.NO_POSITION) {
                    decrementClickListener.onDecrementClick(view, position);
                }
            });
        }
        
        void onBind(Vote vote)
        {
            name.setText(vote.getStock( ).getName( ));
            message.setText(vote.getParticipantsCount( ) + "명이 참여 중");
        }
    }
}