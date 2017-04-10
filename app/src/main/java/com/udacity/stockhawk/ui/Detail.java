package com.udacity.stockhawk.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.objects.History;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Detail extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = Detail.class.getSimpleName();

    private int mIdQuote;
    private ArrayList<History> mHistoryList;
    private static final int STOCK_LOADER = 0;

    public static final String ID_QUOTE_TAG = "quote_tag";


    @BindView(R.id.detail_chart) LineChart mLineChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        configChart();

        // no description text

        Intent intent = getIntent();
        if (null != intent) {
            mIdQuote = intent.getIntExtra(ID_QUOTE_TAG, 0);
        }

        getSupportLoaderManager().initLoader(STOCK_LOADER, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case STOCK_LOADER: {
                return new CursorLoader(this,
                        Contract.HistoryEntry.CONTENT_URI,
                        null,
                        Contract.HistoryEntry.COLUMN_QUOTE_KEY + " = ? ",
                        new String[]{Integer.toString(mIdQuote)},
                        Contract.HistoryEntry.COLUMN_DATE
                );
            }
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount() > 0) {
            mHistoryList = cursorDataToHistoryList(data);
        }

        List<Entry> entries = new ArrayList<Entry>();
        if( null == mHistoryList || mHistoryList.size()<=0){
            return;
        }
        for (History his : mHistoryList) {

            // turn your data into Entry objects
            entries.add(new Entry((float) his.getDate(), (float) his.getClose()));
            Log.d(LOG_TAG, getReadableDateString(this,his.getDate()));
        }
//        entries.add(new Entry(1,2));
//        entries.add(new Entry(2,5));
//        entries.add(new Entry(4,1));





        LineDataSet dataSet = new LineDataSet(entries, "Label");
        dataSet.setColor(R.color.chart_line_pink_accent);
        LineData lineData = new LineData(dataSet);
        mLineChart.setData(lineData);
        mLineChart.invalidate();

        // get the legend (only possible after setting data)
        Legend l = mLineChart.getLegend();
        l.setEnabled(false);


        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
       // xAxis.setTypeface(mTfLight);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(Color.rgb(255, 192, 56));
        xAxis.setCenterAxisLabels(true);
        xAxis.setGranularity(1f); // one hour
        xAxis.setValueFormatter(new IAxisValueFormatter() {

            private SimpleDateFormat mFormat = new SimpleDateFormat("dd MMM");

            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                long millis = TimeUnit.HOURS.toMillis((long) value);
                return mFormat.format(new Date(millis));
            }
        });


        YAxis leftAxis = mLineChart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        //leftAxis.setTypeface(mTfLight);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(170f);
        leftAxis.setYOffset(-9f);
        leftAxis.setTextColor(Color.rgb(255, 192, 56));

        YAxis rightAxis = mLineChart.getAxisRight();
        rightAxis.setEnabled(false);

        Log.d(LOG_TAG, "Fin");
    }

    private static String getReadableDateString(Context context, long timeInMillis) {
        int flags = DateUtils.FORMAT_SHOW_DATE;

        return DateUtils.formatDateTime(context, timeInMillis, flags);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private ArrayList<History> cursorDataToHistoryList(Cursor cursor) {
        ArrayList<History> historyList = null;
        if (null != cursor && cursor.moveToFirst()) {
            historyList = new ArrayList<>();
            do {
                History history = new History(cursor, cursor.getPosition());
                historyList.add(history);
            } while (cursor.moveToNext());
        }
        return historyList;
    }


    private void configChart(){
        mLineChart.getDescription().setEnabled(false);

        // enable touch gestures
        mLineChart.setTouchEnabled(true);

        mLineChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        mLineChart.setDragEnabled(true);
        mLineChart.setScaleEnabled(true);
        mLineChart.setDrawGridBackground(false);
        mLineChart.setHighlightPerDragEnabled(true);

        // set an alternative background color
        mLineChart.setBackgroundColor(Color.WHITE);
        mLineChart.setViewPortOffsets(0f, 0f, 0f, 0f);

    }
}
