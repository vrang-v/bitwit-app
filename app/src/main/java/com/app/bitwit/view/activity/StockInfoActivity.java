package com.app.bitwit.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.app.bitwit.R;
import com.app.bitwit.databinding.ActivityStockInfoBinding;
import com.app.bitwit.constant.Colors;
import com.app.bitwit.view.adapter.PostPreviewAdapter;
import com.app.bitwit.viewmodel.StockInfoViewModel;
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.LineDataSet.Mode;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import dagger.hilt.android.AndroidEntryPoint;
import lombok.var;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.animation.AnimationUtils.loadAnimation;
import static com.app.bitwit.constant.IntentKeys.STOCK_TICKER;
import static com.app.bitwit.util.livedata.LiveDataUtils.observe;
import static com.app.bitwit.util.livedata.LiveDataUtils.observeAllNotNull;
import static com.app.bitwit.util.livedata.LiveDataUtils.observeNotEmpty;
import static com.app.bitwit.util.livedata.LiveDataUtils.observeNotNull;

@AndroidEntryPoint
public class StockInfoActivity extends AppCompatActivity implements OnTouchListener {
    
    private ActivityStockInfoBinding binding;
    private StockInfoViewModel       viewModel;
    
    private LineDataSet       lineDataSet;
    private Map<View, String> intervalMap;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init( );
        initLineChart( );
        initLineDataSet( );
        initIntervalMap( );
        
        observeNotNull(this, viewModel.getTicker( ), ticker -> {
            viewModel.loadVoteItem(ticker);
            viewModel.loadPosts(ticker).subscribe( );
        });
        
        observeAllNotNull(this, viewModel.getTicker( ), viewModel.getChartInterval( ), (ticker, interval) -> {
            viewModel.loadChart(ticker, interval);
            viewModel.refreshChart(ticker, interval).subscribe( );
        });
        
        observeNotEmpty(this, viewModel.getEntries( ), this::drawChart);
        
        observe(this, viewModel.getVoteItem( ), voteItem -> {
            binding.participantCount.startAnimation(loadAnimation(this, R.anim.anim_fade));
            binding.currentPrice.startAnimation(loadAnimation(this, R.anim.anim_fade));
            binding.currentFluctuateRate.startAnimation(loadAnimation(this, R.anim.anim_fade));
        });
        
        var adapter = new PostPreviewAdapter( );
        adapter.addAdapterEventListener(this, event -> {
            var intent = new Intent(this, PostActivity.class)
                    .putExtra("postId", event.getItem( ).getId( ));
            startActivity(intent);
            overridePendingTransition(R.anim.slide_right_to_left_enter, R.anim.slide_right_to_left_exit);
        });
        
        binding.postRecyclerview.setAdapter(adapter);
        binding.postRecyclerview.setLayoutManager(new LinearLayoutManager(this));
    }
    
    private void drawChart(List<Entry> entries) {
        float dy = entries.get(entries.size( ) - 1).getY( ) - entries.get(0).getY( );
        lineDataSet.setValues(entries);
        lineDataSet.setColor(dy >= 0 ? Colors.PRIMARY_RED : Colors.PRIMARY_BLUE);
        lineDataSet.setFillColor(dy >= 0 ? Colors.PRIMARY_RED_LIGHT : Colors.PRIMARY_BLUE_LIGHT);
        binding.lineChart.setData(new LineData(lineDataSet));
        binding.lineChart.invalidate( );
    }
    
    public void onChartIntervalBtnClick(View view) {
        binding.button1m.setSelected(false);
        binding.button3m.setSelected(false);
        binding.button5m.setSelected(false);
        binding.button10m.setSelected(false);
        binding.button30m.setSelected(false);
        binding.button1h.setSelected(false);
        binding.button6h.setSelected(false);
        binding.button12h.setSelected(false);
        binding.button24h.setSelected(false);
        view.setSelected(true);
        var interval = intervalMap.get(view);
        viewModel.setChartInterval(interval);
    }
    
    private void init( ) {
        binding   = DataBindingUtil.setContentView(this, R.layout.activity_stock_info);
        viewModel = new ViewModelProvider(this).get(StockInfoViewModel.class);
        viewModel.setTicker(getIntent( ).getStringExtra(STOCK_TICKER));
        viewModel.setChartInterval("1m");
        
        binding.button1m.setSelected(true);
        binding.setActivity(this);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        binding.executePendingBindings( );
    }
    
    private void initLineChart( ) {
        binding.lineChart.setViewPortOffsets(0f, 0f, 0f, 0f);
        binding.lineChart.setPinchZoom(false);
        binding.lineChart.setDoubleTapToZoomEnabled(false);
        binding.lineChart.getXAxis( ).setDrawLabels(false);
        binding.lineChart.getXAxis( ).setDrawAxisLine(false);
        binding.lineChart.getXAxis( ).setDrawGridLines(false);
        binding.lineChart.getAxisLeft( ).setLabelCount(7);
        binding.lineChart.getAxisLeft( ).setDrawAxisLine(false);
        binding.lineChart.getAxisLeft( ).setDrawGridLines(false);
        binding.lineChart.getAxisLeft( ).setPosition(YAxisLabelPosition.INSIDE_CHART);
        binding.lineChart.getAxisRight( ).setDrawLabels(false);
        binding.lineChart.getAxisRight( ).setDrawAxisLine(false);
        binding.lineChart.getAxisRight( ).setDrawGridLines(false);
        binding.lineChart.getLegend( ).setEnabled(false);
        binding.lineChart.getDescription( ).setEnabled(false);
        binding.lineChart.animateX(750);
        binding.lineChart.setNoDataText("차트를 그리고 있어요! 잠시만 기다려주세요");
        binding.lineChart.setNoDataTextColor(Colors.BLACK);
        binding.lineChart.setOnTouchListener(this);
        binding.lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener( ) {
            @Override
            public void onValueSelected(Entry entry, Highlight highlight) {
                binding.selectedCandlestickInfo.setVisibility(View.VISIBLE);
                viewModel.setSelectedCandlestick((int)entry.getX( ));
            }
            
            @Override
            public void onNothingSelected( ) {
                binding.selectedCandlestickInfo.setVisibility(View.INVISIBLE);
            }
        });
    }
    
    private void initLineDataSet( ) {
        lineDataSet = new LineDataSet(null, "CHART");
        lineDataSet.setDrawValues(false);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        lineDataSet.setLineWidth(2f);
        lineDataSet.setHighLightColor(Colors.BLACK);
        lineDataSet.setHighlightLineWidth(1f);
        lineDataSet.setMode(Mode.CUBIC_BEZIER);
        lineDataSet.setCubicIntensity(0.2f);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFillAlpha(40);
    }
    
    private void initIntervalMap( ) {
        intervalMap = new HashMap<>( );
        intervalMap.put(binding.button1m, "1m");
        intervalMap.put(binding.button3m, "3m");
        intervalMap.put(binding.button5m, "5m");
        intervalMap.put(binding.button10m, "10m");
        intervalMap.put(binding.button30m, "30m");
        intervalMap.put(binding.button1h, "1h");
        intervalMap.put(binding.button6h, "6h");
        intervalMap.put(binding.button12h, "12h");
        intervalMap.put(binding.button24h, "24h");
    }
    
    @Override
    public void onEnterAnimationComplete( ) {
        binding.currentPrice.setVisibility(View.VISIBLE);
        binding.currentFluctuateRate.setVisibility(View.VISIBLE);
        binding.lineChart.setVisibility(View.VISIBLE);
        super.onEnterAnimationComplete( );
    }
    
    @Override
    public void onBackPressed( ) {
        finishAfterTransition( );
    }
    
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction( );
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            binding.lineChart.highlightValues(null);
            var animation = loadAnimation(this, R.anim.anim_fade_out_fast);
            animation.setAnimationListener(new AnimationListener( ) {
                public void onAnimationStart(Animation animation) { }
                
                public void onAnimationRepeat(Animation animation) { }
                
                @Override
                public void onAnimationEnd(Animation animation) {
                    binding.selectedCandlestickInfo.setVisibility(View.INVISIBLE);
                }
            });
            binding.selectedCandlestickInfo.startAnimation(animation);
        }
        return false;
    }
}
