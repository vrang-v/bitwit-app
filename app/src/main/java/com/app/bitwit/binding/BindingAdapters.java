package com.app.bitwit.binding;

import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;
import androidx.databinding.BindingAdapter;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;
import com.app.bitwit.domain.Post;
import com.app.bitwit.view.adapter.PostsAdapter;
import com.app.bitwit.view.adapter.common.EventAdapter;
import com.app.bitwit.view.adapter.common.AdapterEventType;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class BindingAdapters {
    
    @BindingAdapter("bind:adapter")
    public static <T, E extends AdapterEventType> void adapterCollection(RecyclerView recyclerView, Collection<T> items) {
        EventAdapter<T, E> adapter = (EventAdapter<T, E>)recyclerView.getAdapter( );
        if (adapter != null) {
            adapter.updateItems(items);
        }
    }
    
    @BindingAdapter("bind:posts")
    public static void bindPosts(RecyclerView recyclerView, LiveData<List<Post>> posts) {
        PostsAdapter adapter = (PostsAdapter)recyclerView.getAdapter( );
        if (adapter != null) {
            adapter.updatePosts(posts.getValue( ));
        }
    }
    
    @BindingAdapter("bind:layout_constraintHorizontal_weight")
    public static void setConstraintHorizontalWeight(View view, float weight) {
        LayoutParams layoutParams = (LayoutParams)view.getLayoutParams( );
        layoutParams.horizontalWeight = weight;
        view.setLayoutParams(layoutParams);
    }
    
    @BindingAdapter("bind:background")
    public static void setBackground(View view, int resource) {
        view.setBackgroundResource(resource);
    }
    
    @BindingAdapter("bind:isBold")
    public static void setTextStyleBold(TextView view, boolean isBold) {
        if (isBold) {
            view.setTypeface(null, Typeface.BOLD);
        }
        else {
            view.setTypeface(null, Typeface.NORMAL);
        }
    }
    
    @BindingAdapter("binding:selected")
    public static void setSelected(View view, boolean selected) {
        view.setSelected(selected);
    }
}
