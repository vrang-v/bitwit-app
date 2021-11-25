package com.app.bitwit.view.adapter.common;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import com.app.bitwit.util.livedata.MutableLiveEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public abstract class EventAdapter<T, E extends RecyclerViewEvent> extends Adapter<EventAdapter<T, E>.EventViewHolder> {
    
    protected final List<T> items = new ArrayList<>( );
    
    protected final MutableLiveEvent<AdapterEvent<T, E>> adapterEvent = new MutableLiveEvent<>( );
    
    @Override
    public void onBindViewHolder(@NonNull EventAdapter<T, E>.EventViewHolder holder, int position) {
        holder.bind(items.get(position));
    }
    
    @Override
    public int getItemCount( ) {
        return items.size( );
    }
    
    public void updateItems(Collection<T> items) {
        this.items.clear( );
        this.items.addAll(items);
        notifyDataSetChanged( );
    }
    
    public void addAdapterEventListener(LifecycleOwner lifecycleOwner, Consumer<AdapterEvent<T, E>> consumer) {
        adapterEvent.observe(lifecycleOwner, consumer::accept);
    }
    
    public abstract class EventViewHolder extends RecyclerView.ViewHolder {
        
        protected T item;
        
        protected EventViewHolder(View view) {
            super(view);
        }
        
        protected void publishEvent(E e, View view) {
            adapterEvent.postValue(new AdapterEvent<>(item, e, view, getAdapterPosition( )));
        }
        
        public abstract void bind( );
        
        void bind(T item) {
            this.item = item;
            bind( );
        }
    }
}
