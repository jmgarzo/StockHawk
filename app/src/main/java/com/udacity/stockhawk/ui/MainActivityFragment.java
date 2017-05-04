package com.udacity.stockhawk.ui;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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
import com.udacity.stockhawk.sync.QuoteSyncJob;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;


public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        SwipeRefreshLayout.OnRefreshListener,
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
    TextView error;
    @BindView(R.id.fab)
    FloatingActionButton mFab;

    public StockAdapter adapter;



    @Override
    public void onClick(String symbol) {
        Timber.d("Symbol clicked: %s", symbol);

        ((Callback) getActivity()).onItemSelected(symbol);
//        Intent intent = new Intent(getActivity(), Detail.class);
//        intent.putExtra(Intent.EXTRA_TEXT, symbol);
//        startActivity(intent);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView =  inflater.inflate(R.layout.fragment_main_activity, container, false);

        ButterKnife.bind(this,rootView);

        adapter = new StockAdapter(getActivity(), this);
        stockRecyclerView.setAdapter(adapter);
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

                String symbol = adapter.getSymbolAtPosition(viewHolder.getAdapterPosition());
                PrefUtils.removeStock(getContext(), symbol);
                getActivity().getContentResolver().delete(Contract.QuoteEntry.makeUriForStock(symbol), null, null);

                QuoteSyncJob.syncImmediately(getActivity());


            }
        }).attachToRecyclerView(stockRecyclerView);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button(v);
            }
        });

        return rootView;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return  new CursorLoader(getActivity(),
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
            error.setVisibility(View.GONE);
        }
        adapter.setCursor(data);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        swipeRefreshLayout.setRefreshing(false);
        adapter.setCursor(null);
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

        QuoteSyncJob.syncImmediately(getActivity());

        if (!networkUp() && adapter.getItemCount() == 0) {
            swipeRefreshLayout.setRefreshing(false);
            error.setText(getString(R.string.error_no_network));
            error.setVisibility(View.VISIBLE);
        } else if (!networkUp()) {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getActivity(), R.string.toast_no_connectivity, Toast.LENGTH_LONG).show();
        } else if (PrefUtils.getStocks(getActivity()).size() == 0) {
            swipeRefreshLayout.setRefreshing(false);
            error.setText(getString(R.string.error_no_stocks));
            error.setVisibility(View.VISIBLE);
        } else {
            error.setVisibility(View.GONE);
        }
    }


    public void button(@SuppressWarnings("UnusedParameters") View view) {
        new AddStockDialog().show(getActivity().getFragmentManager(), "StockDialogFragment");
    }







}
