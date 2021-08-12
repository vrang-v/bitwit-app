package com.app.bitwit.binding;

import android.view.View;
import android.widget.LinearLayout;
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;
import androidx.databinding.BindingAdapter;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;
import com.app.bitwit.adapter.VoteAdapter;
import com.app.bitwit.data.source.local.entity.VoteItem;

import java.util.List;

public class BindingAdapters {
    
    @BindingAdapter("bind:votes")
    public static void bindVotes(RecyclerView recyclerView, LiveData<List<VoteItem>> voteViews) {
        VoteAdapter adapter = (VoteAdapter)recyclerView.getAdapter( );
        if (adapter != null) {
            adapter.updateVoteViews(voteViews.getValue( ));
        }
    }
    
    @BindingAdapter("bind:layout_weight")
    public static void setLayoutWeight(View view, int weight) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)view.getLayoutParams( );
        layoutParams.weight = weight;
        view.setLayoutParams(layoutParams);
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
}
