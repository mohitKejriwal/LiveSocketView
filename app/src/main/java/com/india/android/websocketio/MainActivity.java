package com.india.android.websocketio;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    SocketViewModel socketViewModel;
    TabLayout tlRange;
    LineChart lineChart;
    List<Entry> entries;
    TextView liveStockPrice,tvOpen,tvClose,tvHigh,tvLow,tvVol;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lineChart=findViewById(R.id.stockChart);
        liveStockPrice=findViewById(R.id.tvCurrentStockPrice);
        tvHigh=findViewById(R.id.tvDayHighValue);
        tvLow=findViewById(R.id.tvDayLowValue);
        tvClose=findViewById(R.id.tvCloseValue);
        tvVol=findViewById(R.id.tvVolValue);
        tvOpen=findViewById(R.id.tvOpenValue);
        lineChart=findViewById(R.id.stockChart);
        tlRange=findViewById(R.id.tlRange);
        setupTabs();

        //Getting ViewModel for current activity
        socketViewModel = ViewModelProviders.of(this).get(SocketViewModel.class);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Listener for historic api response
        socketViewModel.getPastStockData().observe(this, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged( ArrayList<String> stockData) {
                entries = new ArrayList<>();
                for (int i=0;i<stockData.size();i++){
                    float volume=Float.parseFloat(stockData.get(i).split(",")[5]);
                    entries.add(new Entry(i,volume));
                }
                setupChart();
            }
        });

        //Establish socket connection
        socketViewModel.connectSocket();

        //Listener for real time server response, data updates in 5 seconds
        socketViewModel.getLiveStockData().observe(this, new Observer<String[]>() {
            @Override
            public void onChanged(@Nullable String[] strings) {
                tvOpen.setText(strings[1]);
                tvHigh.setText(strings[2]);
                tvLow.setText(strings[3]);
                tvClose.setText(strings[4]);
                tvVol.setText(strings[5]);
                liveStockPrice.setText(strings[2]);
                entries.add(new Entry(entries.size(),Float.parseFloat(strings[5])));
                entries=entries.subList(1,entries.size()-1);
                lineChart.invalidate();
            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        socketViewModel.getPastStockData().removeObservers(this);
        socketViewModel.getLiveStockData().removeObservers(this);
        socketViewModel.disconnectSocket();
    }


    private void setupTabs(){
        tlRange.addTab(tlRange.newTab().setText("1D"));
        tlRange.addTab(tlRange.newTab().setText("1M"));
        tlRange.addTab(tlRange.newTab().setText("3M"));
        tlRange.addTab(tlRange.newTab().setText("YTD"));
        tlRange.addTab(tlRange.newTab().setText("1Y"));
        tlRange.addTab(tlRange.newTab().setText("ALL"));
    }

    private void setupChart(){
        LineDataSet dataSet = new LineDataSet(entries, "Volume"); // add entries to dataset
        dataSet.setDrawValues(true);
        dataSet.setLineWidth(1f);
        dataSet.setDrawCircles(false);
        dataSet.setValueTextSize(12f);
        dataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        dataSet.setFillAlpha(0);
        dataSet.setDrawFilled(true);
        dataSet.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGradientStart));
        Drawable drawable = (ContextCompat.getDrawable(getApplicationContext(), R.drawable.color_graph_gradient));
        dataSet.setFillDrawable(drawable);
        lineChart.setDrawGridBackground(false);
        lineChart.setDrawBorders(false);

        lineChart.setAutoScaleMinMaxEnabled(false);
        lineChart.setDescription(null);
        lineChart.setTouchEnabled(false);
        // remove axis
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setEnabled(false);
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setEnabled(true);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // to have draw animaion
        lineChart.animateY(600);
        lineChart.setViewPortOffsets(0f, 0f, 0f, 0f);
        lineChart.invalidate();
    }
}
