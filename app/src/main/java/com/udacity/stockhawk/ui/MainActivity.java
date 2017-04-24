package com.udacity.stockhawk.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.sync.QuoteSyncJob;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity   {

//    private static final int STOCK_LOADER = 0;
//    @SuppressWarnings("WeakerAccess")
//    @BindView(R.id.recycler_view)
//    RecyclerView stockRecyclerView;
//    @SuppressWarnings("WeakerAccess")
//    @BindView(R.id.swipe_refresh)
//    SwipeRefreshLayout swipeRefreshLayout;
//    @SuppressWarnings("WeakerAccess")
//    @BindView(R.id.error)
//    TextView error;
//    private StockAdapter adapter;

//    public static final String[] VIEW_STOCk_QUOTE_COLUNMS = {
//
//            Contract.StockEntry.TABLE_NAME + "." + Contract.StockEntry._ID,
//            Contract.QuoteEntry.TABLE_NAME + "." + Contract.QuoteEntry._ID,
//            Contract.StockEntry.COLUMN_CURRENCY,
//            Contract.StockEntry.COLUMN_NAME,
//            Contract.StockEntry.COLUMN_STOCKEXCHANGE,
//            Contract.StockEntry.TABLE_NAME + "." + Contract.StockEntry.COLUMN_SYMBOL,
//            Contract.QuoteEntry.TABLE_NAME + "." + Contract.QuoteEntry.COLUMN_SYMBOL,
//            Contract.QuoteEntry.COLUMN_PRICE,
//            Contract.QuoteEntry.COLUMN_ABSOLUTE_CHANGE,
//            Contract.QuoteEntry.COLUMN_PERCENTAGE_CHANGE
//
//    };
//    public static final int COL_STOCK_ID = 0;
//    public static final int COL_QUOTE_ID = 1;
//    public static final int COL_CURRENCY = 2;
//    public static final int COL_NAME = 3;
//    public static final int COL_STOCKEXCHANGE = 4;
//    public static final int COL_STOCK_SYMBOL = 5;
//    public static final int COL_QUOTE_SYMBOL = 6;
//    public static final int COL_PRICE = 7;
//    public static final int COL_ABSOLUTE_CHANGE = 8;
//    public static final int COL_PERCENTAGE_CHANGE = 9;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //System.setProperty("yahoofinance.baseurl.histquotes", "https://ichart.yahoo.com/table.csv");

        if (savedInstanceState == null) {
            QuoteSyncJob.initialize(this);

        }

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        //onRefresh();

        //QuoteSyncJob.initialize(this);




    }





    public void button(@SuppressWarnings("UnusedParameters") View view) {
        new AddStockDialog().show(getFragmentManager(), "StockDialogFragment");
    }




    private void setDisplayModeMenuItemIcon(MenuItem item) {
        if (PrefUtils.getDisplayMode(this)
                .equals(getString(R.string.pref_display_mode_absolute_key))) {
            item.setIcon(R.drawable.ic_percentage);
        } else {
            item.setIcon(R.drawable.ic_dollar);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_settings, menu);
        MenuItem item = menu.findItem(R.id.action_change_units);
        setDisplayModeMenuItemIcon(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_change_units) {
            PrefUtils.toggleDisplayMode(this);
            setDisplayModeMenuItemIcon(item);
            MainActivityFragment mainActivityFragment = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
            mainActivityFragment.adapter.notifyDataSetChanged();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void addStock(String stock){
        MainActivityFragment mainActivityFragment = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
        mainActivityFragment.addStock(stock);

    }

}
