package com.udacity.stockhawk.sync;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.objects.History;

import java.io.IOException;
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

    private static final String LOG_TAG = QuoteSyncJob.class.getSimpleName();

    private static final int ONE_OFF_ID = 2;
    private static final String ACTION_DATA_UPDATED = "com.udacity.stockhawk.ACTION_DATA_UPDATED";
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
                return;
            }

            Map<String, Stock> quotes = YahooFinance.get(stockArray);
            Iterator<String> iterator = stockCopy.iterator();

            Timber.d(quotes.toString());

            if (iterator.hasNext()) {
                SyncUtils.cleanDB(context);
            }

            while (iterator.hasNext()) {
                String symbol = iterator.next();
                Stock stock = quotes.get(symbol);
                if (null != stock)
                    SyncUtils.addStockAndQuote(context, stock);
            }

//                StockQuote quote = stock.getQuote();
//
//                float price = quote.getPrice().floatValue();
//                float change = quote.getChange().floatValue();
//                float percentChange = quote.getChangeInPercent().floatValue();

            // WARNING! Don't request historical data for a stock that doesn't exist!
            // The request will hang forever X_x
//                List<HistoricalQuote> history = stock.getHistory(from, to, Interval.WEEKLY);
//
//                ArrayList<History> historyList = new ArrayList<>();
//                for (HistoricalQuote it : history) {
//                    History newHistory = new History(it);
//                    historyList.add(newHistory);
//                }

//                ContentValues quoteCV = new ContentValues();
//                quoteCV.put(Contract.QuoteEntry.COLUMN_SYMBOL, symbol);
//                quoteCV.put(Contract.QuoteEntry.COLUMN_PRICE, price);
//                quoteCV.put(Contract.QuoteEntry.COLUMN_PERCENTAGE_CHANGE, percentChange);
//                quoteCV.put(Contract.QuoteEntry.COLUMN_ABSOLUTE_CHANGE, change);

//                quoteCVs.add(quoteCV);
//                Uri insertUri = context.getContentResolver().insert(Contract.QuoteEntry.CONTENT_URI, quoteCV);
//
//                if (insertUri != null) {
//                    String idQuote = insertUri.getLastPathSegment();
//                    ContentValues[] historyContentValues = new ContentValues[historyList.size()];
//                    for (int i = 0; i < historyList.size(); i++) {
//                        historyList.get(i).setQuoteId(Integer.valueOf(idQuote));
//                        historyContentValues[i] = historyList.get(i).getContentValues();
//                    }
//                    context.getContentResolver().bulkInsert(Contract.HistoryEntry.CONTENT_URI,
//                            historyContentValues);
//                }


//            }

//            context.getContentResolver()
//                    .bulkInsert(
//                            Contract.QuoteEntry.CONTENT_URI,
//                            quoteCVs.toArray(new ContentValues[quoteCVs.size()]));
//
//            Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED);
//            context.sendBroadcast(dataUpdatedIntent);

        } catch (IOException exception) {
            Timber.e(exception, "Error fetching stock quotes");
        }
    }



    public static void addHistoryQuotes(Context context, int idQuote) {
        SyncUtils.addHistory(context, idQuote);

    }

    public static void addHistoryQuotes(Context context, String symbol) {
        SyncUtils.addHistory(context, symbol);

    }
    private static ArrayList<History> getHistoricalQuotes(String symbol, Calendar from, Calendar to,
                                                          Interval interval) {
        ArrayList<History> historyList = null;

        try {
            Stock stock = YahooFinance.get(symbol, from, to, interval);
        } catch (IOException e) {
            Timber.e(LOG_TAG + e);
            e.printStackTrace();
        }
        return historyList;
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


}
