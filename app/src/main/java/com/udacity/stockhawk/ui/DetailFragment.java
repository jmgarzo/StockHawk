package com.udacity.stockhawk.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.objects.History;
import com.udacity.stockhawk.sync.HistoryQuotesIntentService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();


    private String mSymbol;
    private ArrayList<History> mHistoryList;
    private static final int STOCK_LOADER = 11;

//    public static final String ID_QUOTE_TAG = "quote_tag";

    private float mXMax;
    private float mYMax;
    private float mXMin;
    private float mYMin;

    private String mTimeIntervalPreference;


    @BindView(R.id.detail_chart)
    LineChart mLineChart;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState==null){
            getActivity().getSupportLoaderManager().initLoader(STOCK_LOADER, null, this);

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_detail, container, false);

        mTimeIntervalPreference = PrefUtils.getTimeInterval(getActivity());

        ButterKnife.bind(this, root);

        configChart();

        // no description text

        Intent intent = getActivity().getIntent();
        if (null != intent) {
            mSymbol = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (mSymbol != null) {
                Intent addHistoryIntent = new Intent(getActivity(), HistoryQuotesIntentService.class);
                addHistoryIntent.putExtra(Intent.EXTRA_TEXT, mSymbol);
                getActivity().startService(addHistoryIntent);
                //getActivity().getSupportLoaderManager().initLoader(STOCK_LOADER, null, this);
                getLoaderManager().restartLoader(STOCK_LOADER, null, this);

                mLoadingIndicator.setVisibility(View.VISIBLE);
            }

            Bundle arguments = getArguments();
            if (arguments != null) {
                mSymbol = arguments.getString(Intent.EXTRA_TEXT);
                Intent addHistoryIntent = new Intent(getActivity(), HistoryQuotesIntentService.class);
                addHistoryIntent.putExtra(Intent.EXTRA_TEXT, mSymbol);
                getActivity().startService(addHistoryIntent);
                //getActivity().getSupportLoaderManager().initLoader(STOCK_LOADER, null, this);
                getLoaderManager().restartLoader(STOCK_LOADER, null, this);


                mLoadingIndicator.setVisibility(View.VISIBLE);
            }

        }




        return root;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

       // mLoadingIndicator.setVisibility(View.VISIBLE);

        switch (id) {
            case STOCK_LOADER: {
                if (null != mSymbol && !mSymbol.equals("")) {
                    return new CursorLoader(getActivity(),
                            Contract.HistoryEntry.CONTENT_URI,
                            Contract.HistoryEntry.HISTORY_COLUMNS.toArray(new String[]{}),
                            Contract.HistoryEntry.COLUMN_SYMBOL + " = ? ",
                            new String[]{mSymbol},
                            Contract.HistoryEntry.COLUMN_DATE
                    );
                }
            }
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == STOCK_LOADER) {

            initMaxAndMinValues();
            configChart();

            //TODO: Controlar casos de error.
//        if (data == null || data.getCount() <= 0) {
//            //showError(getContext().getString(R.string.error_no_history));
//            mLineChart.setNoDataText(getContext().getString(R.string.error_no_history));
//            mLoadingIndicator.setVisibility(View.GONE);
//            return;
//        }
            mHistoryList = cursorDataToHistoryList(data);


            List<Entry> entries = new ArrayList<Entry>();
            if (null == mHistoryList || mHistoryList.size() <= 0) {
                return;
            }
            for (History his : mHistoryList) {

                setMaxAndMinValues(his);
                // turn your data into Entry objects
                entries.add(new Entry((float) his.getDate(), (float) his.getClose()));
                Log.d(LOG_TAG, getReadableDateString(getActivity(), his.getDate()));
            }

            LineDataSet dataSet = new LineDataSet(entries, "Label");
            dataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));

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
            //xAxis.setTextColor(Color.WHITE);
            xAxis.setCenterAxisLabels(true);
            //xAxis.setGranularity(1f); // one hour
            xAxis.setAxisMinimum(mXMin - ((mXMax - mXMin) * 0.05f));
            xAxis.setAxisMaximum(mXMax + ((mXMax - mXMin) * 0.05f));
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
            leftAxis.setTextColor(Color.WHITE);
            leftAxis.setDrawGridLines(true);
            leftAxis.setGranularityEnabled(true);
            leftAxis.setAxisMinimum(mYMin - ((mYMax - mYMin) * 0.05f));
            leftAxis.setAxisMaximum(mYMax + ((mYMax - mYMin) * 0.05f));
            leftAxis.setYOffset(-9f);
            //leftAxis.setTextColor(Color.rgb(255, 192, 56));

            YAxis rightAxis = mLineChart.getAxisRight();
            rightAxis.setEnabled(false);

            mLineChart.invalidate();
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            mLineChart.refreshDrawableState();


        }
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


    private void configChart() {
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
        // mLineChart.setBackgroundColor(getResources().getColor(R.color.chart_line_pink_accent));
        mLineChart.setViewPortOffsets(0f, 0f, 0f, 0f);

    }

    @Override
    public void onResume() {

        super.onResume();
        getLoaderManager().restartLoader(STOCK_LOADER, null, this);


        if (!PrefUtils.getTimeInterval(getActivity()).equalsIgnoreCase(mTimeIntervalPreference)) {
            mLoadingIndicator.setVisibility(View.VISIBLE);
            Intent addHistoryIntent = new Intent(getActivity(), HistoryQuotesIntentService.class);
            addHistoryIntent.putExtra(Intent.EXTRA_TEXT, mSymbol);
            getActivity().startService(addHistoryIntent);
            mTimeIntervalPreference = PrefUtils.getTimeInterval(getActivity());

        }
    }

    private void initMaxAndMinValues() {
        mXMax = Float.MIN_VALUE;
        mYMax = Float.MIN_VALUE;
        mXMin = Float.MAX_VALUE;
        mYMin = Float.MAX_VALUE;
    }

    private void setMaxAndMinValues(History his) {
        if (his.getDate() > mXMax) {
            mXMax = his.getDate();
        }
        if (his.getClose() > mYMax) {
            mYMax = (float) his.getClose();
        }
        if (his.getDate() < mXMin) {
            mXMin = his.getDate();
        }
        if (his.getClose() < mYMin) {
            mYMin = (float) his.getClose();
        }

    }
}
