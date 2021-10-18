package com.app.bitwit.binding;

import android.graphics.Typeface;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;
import androidx.databinding.BindingAdapter;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;
import com.app.bitwit.data.source.local.entity.VoteItem;
import com.app.bitwit.domain.Comment;
import com.app.bitwit.domain.Post;
import com.app.bitwit.domain.Stock;
import com.app.bitwit.view.adapter.*;
import lombok.var;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BindingAdapters {
    
    @BindingAdapter("bind:votes")
    public static void bindVotes(RecyclerView recyclerView, LiveData<List<VoteItem>> voteViews) {
        VoteItemAdapter adapter = (VoteItemAdapter)recyclerView.getAdapter( );
        if (adapter != null) {
            adapter.updateVoteViews(voteViews.getValue( ));
        }
    }
    
    @BindingAdapter("bind:searchItems")
    public static void bindStocks(RecyclerView recyclerView, LiveData<List<SearchItem>> searchItems) {
        SearchAdapter adapter = (SearchAdapter)recyclerView.getAdapter( );
        if (adapter != null) {
            adapter.updateVoteViews(searchItems.getValue( ));
        }
    }
    
    @BindingAdapter("bind:postPreivewItems")
    public static void bindPostPreviewItems(RecyclerView recyclerView, LiveData<List<PostPreviewItem>> postPreviewItems) {
        PostPreviewAdapter adapter = (PostPreviewAdapter)recyclerView.getAdapter( );
        if (adapter != null) {
            adapter.updateVoteViews(postPreviewItems.getValue( ));
        }
    }
    
    @BindingAdapter("bind:comments")
    public static void bindComments(RecyclerView recyclerView, LiveData<List<Comment>> comments) {
        CommentAdapter adapter = (CommentAdapter)recyclerView.getAdapter( );
        if (adapter != null) {
            adapter.updateComments(comments.getValue( ));
        }
    }
    
    @BindingAdapter("bind:posts")
    public static void bindPosts(RecyclerView recyclerView, LiveData<List<Post>> posts) {
        PostsAdapter adapter = (PostsAdapter)recyclerView.getAdapter( );
        if (adapter != null) {
            adapter.updatePosts(posts.getValue( ));
        }
    }
    
    @BindingAdapter("bind:tickers")
    public static void bindTickers(RecyclerView recyclerView, Collection<Stock> stocks) {
        if (stocks == null) {
            return;
        }
        var tickers = stocks.stream( )
                            .map(Stock::getTicker)
                            .collect(Collectors.toList( ));
        TickerAdapter adapter = (TickerAdapter)recyclerView.getAdapter( );
        if (adapter != null) {
            adapter.updateTickers(tickers);
        }
    }
    
    @BindingAdapter("bind:editableTickers")
    public static void bindEditableTickers(RecyclerView recyclerView, LiveData<List<String>> tickers) {
        EditableTickerAdapter adapter = (EditableTickerAdapter)recyclerView.getAdapter( );
        if (adapter != null) {
            adapter.updateTickers(tickers.getValue( ));
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
    
    @BindingAdapter("bind:isBold")
    public static void setTextStyleBold(TextView view, boolean isBold) {
        if (isBold) {
            view.setTypeface(null, Typeface.BOLD);
        }
        else {
            view.setTypeface(null, Typeface.NORMAL);
        }
    }
}
