package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.sync.QuoteSyncJob;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback  {

    private boolean mTwoPane;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (findViewById(R.id.stock_detail_container) != null) {

            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.stock_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
            if (savedInstanceState == null) {

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_main, new MainActivityFragment())
                        .commit();


            }

            QuoteSyncJob.initialize(this);
        }




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


    @Override
    public void onItemSelected(String symbol) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putString(Intent.EXTRA_TEXT, symbol);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.stock_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, Detail.class);
            intent.putExtra(Intent.EXTRA_TEXT,symbol);
            startActivity(intent);
        }
    }
}
