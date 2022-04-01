package com.app.bitwit.view.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
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
import com.app.bitwit.constant.Colors;
import com.app.bitwit.databinding.ActivityStockInfoBinding;
import com.app.bitwit.view.adapter.PostPreviewAdapter;
import com.app.bitwit.viewmodel.StockInfoViewModel;
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.data.LineDataSet.Mode;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import dagger.hilt.android.AndroidEntryPoint;
import lombok.var;

import java.util.List;
import java.util.Objects;

import static android.view.animation.AnimationUtils.loadAnimation;
import static com.app.bitwit.constant.IntentKeys.STOCK_TICKER;
import static com.app.bitwit.util.livedata.LiveDataUtils.observe;
import static com.app.bitwit.util.livedata.LiveDataUtils.observeAllNotNull;
import static com.app.bitwit.util.livedata.LiveDataUtils.observeNotEmpty;
import static com.app.bitwit.util.livedata.LiveDataUtils.observeNotNull;

@AndroidEntryPoint
public class StockInfoActivity extends AppCompatActivity implements OnTouchListener {
    
    public static final String CANDLE_STICK = "candleStick";
    public static final String LINE         = "line";
    
    private ActivityStockInfoBinding binding;
    private StockInfoViewModel       viewModel;
    
    private LineDataSet   lineDataSet;
    private CandleDataSet candleDataSet;
    
    private boolean isChartSwiped = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init( );
        initLineChart( );
        initLineDataSet( );
        initCandleStickChart( );
        initCandleDataSet( );
        
        observeNotNull(this, viewModel.getTicker( ), ticker -> {
            viewModel.loadVoteItem(ticker);
            viewModel.loadPostPreviews(ticker)
                     .subscribe( );
        });
        
        observe(this, viewModel.getVoteItem( ), voteItem -> {
            var fadeAnimation = loadAnimation(this, R.anim.anim_fade);
            binding.participantCount.startAnimation(fadeAnimation);
            binding.currentPrice.startAnimation(fadeAnimation);
            binding.currentFluctuateRate.startAnimation(fadeAnimation);
        });
        
        observeAllNotNull(this, viewModel.getTicker( ), viewModel.getChartInterval( ), (ticker, interval) -> {
            viewModel.loadChartData(ticker, interval);
            viewModel.refreshChartData(ticker, interval)
                     .subscribe( );
        });
        
        observeNotEmpty(this, viewModel.getLineEntries( ), this::drawLineChart);
        observeNotEmpty(this, viewModel.getCandleEntries( ), this::drawCandleStickChart);
        
        binding.lineChartBtn.setOnClickListener(v -> viewModel.setChartType(LINE));
        binding.candleStickChartBtn.setOnClickListener(v -> viewModel.setChartType(CANDLE_STICK));
        
        binding.button1m.setOnClickListener(v -> viewModel.setChartInterval("1m"));
        binding.button3m.setOnClickListener(v -> viewModel.setChartInterval("3m"));
        binding.button5m.setOnClickListener(v -> viewModel.setChartInterval("5m"));
        binding.button10m.setOnClickListener(v -> viewModel.setChartInterval("10m"));
        binding.button30m.setOnClickListener(v -> viewModel.setChartInterval("30m"));
        binding.button1h.setOnClickListener(v -> viewModel.setChartInterval("1h"));
        binding.button6h.setOnClickListener(v -> viewModel.setChartInterval("6h"));
        binding.button12h.setOnClickListener(v -> viewModel.setChartInterval("12h"));
        binding.button1d.setOnClickListener(v -> viewModel.setChartInterval("1d"));
        
        var adapter = new PostPreviewAdapter( );
        adapter.addAdapterEventListener(this, event -> {
            var intent = new Intent(this, PostActivity.class).putExtra("postId", event.getItem( ).getId( ));
            startActivity(intent);
            overridePendingTransition(R.anim.slide_right_to_left_enter, R.anim.slide_right_to_left_exit);
        });
        
        binding.postRecyclerview.setAdapter(adapter);
        binding.postRecyclerview.setLayoutManager(new LinearLayoutManager(this));
    }
    
    private void drawLineChart(List<Entry> entries) {
        float dy = entries.get(entries.size( ) - 1).getY( ) - entries.get(0).getY( );
        lineDataSet.setValues(entries);
        lineDataSet.setColor(dy >= 0 ? Colors.PRIMARY_RED : Colors.PRIMARY_BLUE);
        lineDataSet.setFillColor(dy >= 0 ? Colors.PRIMARY_RED_LIGHT : Colors.PRIMARY_BLUE_LIGHT);
        binding.lineChart.setData(new LineData(lineDataSet));
        binding.lineChart.invalidate( );
    }
    
    private void drawCandleStickChart(List<CandleEntry> candleEntries) {
        candleDataSet.setValues(candleEntries);
        binding.candleStickChart.setData(new CandleData(candleDataSet));
        binding.candleStickChart.invalidate( );
    }
    
    private void init( ) {
        binding   = DataBindingUtil.setContentView(this, R.layout.activity_stock_info);
        viewModel = new ViewModelProvider(this).get(StockInfoViewModel.class);
        viewModel.setTicker(getIntent( ).getStringExtra(STOCK_TICKER));
        
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
        binding.lineChart.setOnLongClickListener(v -> {
            vibrate(50L, 100);
            lineDataSet.setHighLightColor(Colors.BLACK);
            binding.lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener( ) {
                @Override
                public void onValueSelected(Entry entry, Highlight highlight) {
                    if (isChartSwiped) {
                        binding.selectedLineInfo.setVisibility(View.VISIBLE);
                        viewModel.setSelectedCandlestick((int)entry.getX( ));
                    }
                    isChartSwiped = true;
                }
                
                @Override
                public void onNothingSelected( ) {
                    binding.selectedLineInfo.setVisibility(View.INVISIBLE);
                }
            });
            return false;
        });
    }
    
    private void initCandleStickChart( ) {
        binding.candleStickChart.setViewPortOffsets(0f, 0f, 0f, 0f);
        binding.candleStickChart.setPinchZoom(false);
        binding.candleStickChart.setDoubleTapToZoomEnabled(false);
        binding.candleStickChart.getXAxis( ).setDrawLabels(false);
        binding.candleStickChart.getXAxis( ).setDrawAxisLine(false);
        binding.candleStickChart.getXAxis( ).setDrawGridLines(false);
        binding.candleStickChart.getAxisLeft( ).setLabelCount(7);
        binding.candleStickChart.getAxisLeft( ).setDrawAxisLine(false);
        binding.candleStickChart.getAxisLeft( ).setDrawGridLines(false);
        binding.candleStickChart.getAxisLeft( ).setPosition(YAxisLabelPosition.INSIDE_CHART);
        binding.candleStickChart.getAxisRight( ).setDrawLabels(false);
        binding.candleStickChart.getAxisRight( ).setDrawAxisLine(false);
        binding.candleStickChart.getAxisRight( ).setDrawGridLines(false);
        binding.candleStickChart.getLegend( ).setEnabled(false);
        binding.candleStickChart.getDescription( ).setEnabled(false);
        binding.candleStickChart.animateX(750);
        binding.candleStickChart.setNoDataText("차트를 그리고 있어요! 잠시만 기다려주세요");
        binding.candleStickChart.setNoDataTextColor(Colors.BLACK);
        binding.candleStickChart.setOnTouchListener(this);
        binding.candleStickChart.setOnLongClickListener(v -> {
            vibrate(50L, 100);
            candleDataSet.setHighLightColor(Colors.BLACK);
            binding.candleStickChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener( ) {
                @Override
                public void onValueSelected(Entry entry, Highlight highlight) {
                    if (isChartSwiped) {
                        binding.selectedCandleInfo.setVisibility(View.VISIBLE);
                        viewModel.setSelectedCandlestick((int)entry.getX( ));
                    }
                    isChartSwiped = true;
                }
                
                @Override
                public void onNothingSelected( ) {
                    binding.selectedCandleInfo.setVisibility(View.INVISIBLE);
                }
            });
            return true;
        });
    }
    
    private void initLineDataSet( ) {
        lineDataSet = new LineDataSet(null, "CHART");
        lineDataSet.setDrawValues(false);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        lineDataSet.setLineWidth(2f);
        lineDataSet.setHighLightColor(Color.TRANSPARENT);
        lineDataSet.setHighlightLineWidth(1f);
        lineDataSet.setMode(Mode.CUBIC_BEZIER);
        lineDataSet.setCubicIntensity(0.2f);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFillAlpha(40);
    }
    
    private void initCandleDataSet( ) {
        candleDataSet = new CandleDataSet(null, "CHART");
        candleDataSet.setDrawValues(false);
        candleDataSet.setDrawHorizontalHighlightIndicator(false);
        candleDataSet.setDrawIcons(false);
        candleDataSet.setColor(Colors.GRAY);
        candleDataSet.setShadowColor(Color.GRAY);
        candleDataSet.setShadowWidth(0.5f);
        candleDataSet.setHighLightColor(Color.TRANSPARENT);
        candleDataSet.setHighlightLineWidth(1f);
        candleDataSet.setIncreasingColor(Colors.PRIMARY_RED);
        candleDataSet.setIncreasingPaintStyle(Style.FILL);
        candleDataSet.setDecreasingColor(Colors.PRIMARY_BLUE);
        candleDataSet.setDecreasingPaintStyle(Paint.Style.FILL);
        candleDataSet.setNeutralColor(Colors.GRAY);
    }
    
    private void vibrate(long time, int strength) {
        var vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        if (vibrator == null) {
            return;
        }
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(time, strength));
        }
        else {
            vibrator.vibrate(time);
        }
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
            binding.lineChart.highlightValue(null);
            binding.candleStickChart.highlightValue(null);
            lineDataSet.setHighLightColor(Color.TRANSPARENT);
            candleDataSet.setHighLightColor(Color.TRANSPARENT);
            binding.lineChart.setOnChartValueSelectedListener(null);
            binding.candleStickChart.setOnChartValueSelectedListener(null);
            if (isChartSwiped) {
                isChartSwiped = false;
                var fadeOutAnimation = loadAnimation(this, R.anim.anim_fade_out_fast);
                fadeOutAnimation.setAnimationListener(new AnimationListener( ) {
                    public void onAnimationStart(Animation animation) { }
                    
                    public void onAnimationRepeat(Animation animation) { }
                    
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        binding.selectedLineInfo.setVisibility(View.INVISIBLE);
                        binding.selectedCandleInfo.setVisibility(View.INVISIBLE);
                    }
                });
                if (Objects.equals(viewModel.getChartType( ).getValue( ), LINE)) {
                    binding.selectedLineInfo.startAnimation(fadeOutAnimation);
                }
                else if (Objects.equals(viewModel.getChartType( ).getValue( ), CANDLE_STICK)) {
                    binding.selectedCandleInfo.startAnimation(fadeOutAnimation);
                }
            }
        }
        return false;
    }
}
