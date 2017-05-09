package com.udacity.stockhawk.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.sync.DeleteQuoteIntentService;
import com.udacity.stockhawk.sync.QuoteSyncJob;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;


public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        SwipeRefreshLayout.OnRefreshListener, SharedPreferences.OnSharedPreferenceChangeListener,
        StockAdapter.StockAdapterOnClickHandler {


    public interface Callback {

        void onItemSelected(String symbol);
    }

    private static final int STOCK_LOADER = 0;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.recycler_view)
    RecyclerView stockRecyclerView;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.error)
    TextView tvError;
    @BindView(R.id.fab)
    FloatingActionButton mFab;

    public StockAdapter mAdapter;

    @Override
    public void onClick(String symbol) {
        Timber.d("Symbol clicked: %s", symbol);
        ((Callback) getActivity()).onItemSelected(symbol);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main_activity, container, false);

        ButterKnife.bind(this, rootView);

        mAdapter = new StockAdapter(getActivity(), this);
        stockRecyclerView.setAdapter(mAdapter);
        stockRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(true);

        getActivity().getSupportLoaderManager().initLoader(STOCK_LOADER, null, this);


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                swipeRefreshLayout.setRefreshing(true);

                String symbol = mAdapter.getSymbolAtPosition(viewHolder.getAdapterPosition());

                Intent deleteQuoteIntent = new Intent(getActivity(), DeleteQuoteIntentService.class);
                deleteQuoteIntent.putExtra(Intent.EXTRA_TEXT, symbol);
                getActivity().startService(deleteQuoteIntent);
//                PrefUtils.removeStock(getContext(), symbol);
//                getActivity().getContentResolver().delete(Contract.QuoteEntry.makeUriForStock(symbol), null, null);


                QuoteSyncJob.syncImmediately(getActivity());

            }
        }).attachToRecyclerView(stockRecyclerView);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button(v);
            }
        });

        updateEmptyView();

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(getActivity(),
                Contract.StockQuoteEntry.CONTENT_URI,
                Contract.StockQuoteEntry.STOCK_QUOTE_COLUMNS.toArray(new String[]{}),
                null,
                null,
                Contract.StockQuoteEntry.COLUMN_SYMBOL);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        swipeRefreshLayout.setRefreshing(false);

        if (data.getCount() != 0) {
            tvError.setVisibility(View.GONE);
        }

        mAdapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        swipeRefreshLayout.setRefreshing(false);
        mAdapter.setCursor(null);
    }

    private boolean networkUp() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    void addStock(String symbol) {
        if (symbol != null && !symbol.isEmpty()) {

            if (networkUp()) {
                swipeRefreshLayout.setRefreshing(true);
            } else {
                String message = getString(R.string.toast_stock_added_no_connectivity, symbol);
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }

            PrefUtils.addStock(getActivity(), symbol);
            QuoteSyncJob.syncImmediately(getActivity());
        }
    }

    @Override
    public void onRefresh() {

        if (!networkUp()) {
            Toast.makeText(getActivity(), R.string.toast_no_connectivity, Toast.LENGTH_LONG).show();
            swipeRefreshLayout.setRefreshing(false);
        }
        updateEmptyView();
        QuoteSyncJob.syncImmediately(getActivity());
    }


    public void button(@SuppressWarnings("UnusedParameters") View view) {
        new AddStockDialog().show(getActivity().getFragmentManager(), "StockDialogFragment");
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_connection_status_key))) {
            updateEmptyView();
        }
    }

    private void updateEmptyView() {
        if (mAdapter.getItemCount() == 0) {
            if (null != tvError) {
                // if cursor is empty, why? do we have an invalid location
                int message = R.string.error_no_stock_data;
                @QuoteSyncJob.ConnectionStatus int status = PrefUtils.getConnectionStatus(getActivity());
                switch (status) {
                    case QuoteSyncJob.CONNECTION_STATUS_SERVER_DOWN:
                        message = R.string.empty_stock_list_server_down;
                        break;
                    case QuoteSyncJob.CONNECTION_STATUS_SERVER_INVALID:
                        message = R.string.empty_stock_list_server_error;
                        break;
                    case QuoteSyncJob.CONNECTION_STATUS_NO_STOCKS:
                        message = R.string.empty_stock_list_no_selected;
                        break;
                    default:
                        if (!PrefUtils.isNetworkAvailable(getActivity())) {
                            message = R.string.empty_stock_list_no_network;
                        }
                }
                swipeRefreshLayout.setRefreshing(false);
                tvError.setVisibility(View.VISIBLE);
                tvError.setText(message);
            } else {
                tvError.setVisibility(View.GONE);
            }
        }
    }
}
