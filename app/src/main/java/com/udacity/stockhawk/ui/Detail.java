package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.objects.History;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.udacity.stockhawk.R.color.material_blue_500;

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
                        Contract.HistoryEntry.URI,
                        null,
                        Contract.HistoryEntry.QUOTE_KEY + " = ? ",
                        new String[]{Integer.toString(mIdQuote)},
                        null
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

        for (History his : mHistoryList) {

            // turn your data into Entry objects
            entries.add(new Entry(his.getDate(), (float) his.getClose()));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Label");
        dataSet.setColor(material_blue_500);
        LineData lineData = new LineData(dataSet);
        mLineChart.setData(lineData);
        mLineChart.invalidate();

        Log.d(LOG_TAG, "Fin");
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
}
