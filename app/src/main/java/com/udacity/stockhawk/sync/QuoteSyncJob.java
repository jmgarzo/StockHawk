package com.udacity.stockhawk.sync;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.objects.History;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.Interval;

public final class QuoteSyncJob {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({CONNECTION_STATUS_OK, CONNECTION_STATUS_SERVER_DOWN, CONNECTION_STATUS_SERVER_INVALID,
            CONNECTION_STATUS_UNKNOWN, CONNECTION_STATUS_NO_STOCKS})
    public @interface ConnectionStatus {}

    public static final int CONNECTION_STATUS_OK = 0;
    public static final int CONNECTION_STATUS_SERVER_DOWN = 1;
    public static final int CONNECTION_STATUS_SERVER_INVALID = 2;
    public static final int CONNECTION_STATUS_UNKNOWN = 3;
    public static final int CONNECTION_STATUS_NO_STOCKS = 4;

    private static final String LOG_TAG = QuoteSyncJob.class.getSimpleName();

    private static final int ONE_OFF_ID = 2;
    public static final String ACTION_DATA_UPDATED = "com.udacity.stockhawk.ACTION_DATA_UPDATED";
    private static final int PERIOD = 300000;
    private static final int INITIAL_BACKOFF = 10000;
    private static final int PERIODIC_ID = 1;

    private static final int YEARS_OF_HISTORY = 2;

    private QuoteSyncJob() {
    }

    static void getQuotesAndStocks(Context context) {

        Timber.d("Running sync job");

        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();
        from.add(Calendar.YEAR, -YEARS_OF_HISTORY);

        try {

            Set<String> stockPref = PrefUtils.getStocks(context);
            Set<String> stockCopy = new HashSet<>();
            stockCopy.addAll(stockPref);
            String[] stockArray = stockPref.toArray(new String[stockPref.size()]);

            Timber.d(stockCopy.toString());

            if (stockArray.length == 0) {
                setConnectionStatus(context,CONNECTION_STATUS_NO_STOCKS);
                return;
            }

            Map<String, Stock> quotes = YahooFinance.get(stockArray);
            Iterator<String> iterator = stockCopy.iterator();

            Timber.d(quotes.toString());

            if (iterator.hasNext()) {
                SyncUtils.cleanDB(context);
            }else{
                setConnectionStatus(context,CONNECTION_STATUS_SERVER_DOWN);
            }

            while (iterator.hasNext()) {
                String symbol = iterator.next();
                Stock stock = quotes.get(symbol);
                if (null != stock)
                    SyncUtils.addStockAndQuote(context, stock);
            }

            setConnectionStatus(context,CONNECTION_STATUS_OK);
            updateWidgets(context);


        } catch (IOException exception) {
            Timber.e(exception, "Error fetching stock quotes");
        }
    }


    public static void addHistoryQuotes(Context context, String symbol) {
        SyncUtils.addHistory(context, symbol);

    }

    private static void schedulePeriodic(Context context) {
        Timber.d("Scheduling a periodic task");

        JobInfo.Builder builder = new JobInfo.Builder(PERIODIC_ID, new ComponentName(context, QuoteJobService.class));

        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(PERIOD)
                .setBackoffCriteria(INITIAL_BACKOFF, JobInfo.BACKOFF_POLICY_EXPONENTIAL);

        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        scheduler.schedule(builder.build());
    }


    public static synchronized void initialize(final Context context) {
        schedulePeriodic(context);
        syncImmediately(context);
    }

    public static synchronized void syncImmediately(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            Intent nowIntent = new Intent(context, QuoteIntentService.class);
            context.startService(nowIntent);
        } else {

            JobInfo.Builder builder = new JobInfo.Builder(ONE_OFF_ID, new ComponentName(context, QuoteJobService.class));

            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setBackoffCriteria(INITIAL_BACKOFF, JobInfo.BACKOFF_POLICY_EXPONENTIAL);

            JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

            scheduler.schedule(builder.build());
        }
    }

    public static  void deleteQuoteDBandPreferences(Context context,String symbol){
        PrefUtils.removeStock(context, symbol);

        context.getContentResolver().delete(Contract.HistoryEntry.CONTENT_URI,
                Contract.HistoryEntry.COLUMN_SYMBOL + "= ?",
                new String[]{symbol});
        context.getContentResolver().delete(Contract.QuoteEntry.CONTENT_URI,
                Contract.QuoteEntry.COLUMN_SYMBOL + "= ?",
                new String[]{symbol});
        context.getContentResolver().delete(Contract.StockEntry.CONTENT_URI,
                Contract.StockEntry.COLUMN_SYMBOL + "= ?",
                new String[]{symbol});
    }

        private static void updateWidgets(Context context) {
        // Setting the package ensures that only components in our app will receive the broadcast
        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED)
                .setPackage(context.getPackageName());
        context.sendBroadcast(dataUpdatedIntent);
    }

    static private void setConnectionStatus(Context c, @ConnectionStatus int connectionStatus){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt(c.getString(R.string.pref_connection_status_key), connectionStatus);
        spe.commit();
    }
}
