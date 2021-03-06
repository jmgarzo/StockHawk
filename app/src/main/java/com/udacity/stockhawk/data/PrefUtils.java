package com.udacity.stockhawk.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.sync.QuoteSyncJob;
import com.udacity.stockhawk.ui.StockAdapter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.quotes.stock.StockQuotesData;

public final class PrefUtils {

    private PrefUtils() {
    }

    public static Set<String> getStocks(Context context) {
        String stocksKey = context.getString(R.string.pref_stocks_key);
        String initializedKey = context.getString(R.string.pref_stocks_initialized_key);
        String[] defaultStocksList = context.getResources().getStringArray(R.array.default_stocks);

        HashSet<String> defaultStocks = new HashSet<>(Arrays.asList(defaultStocksList));
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);


        boolean initialized = prefs.getBoolean(initializedKey, false);

        if (!initialized) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(initializedKey, true);
            editor.putStringSet(stocksKey, defaultStocks);
            editor.apply();
            return defaultStocks;
        }
        return prefs.getStringSet(stocksKey, new HashSet<String>());

    }

    private static void editStockPref(Context context, String symbol, Boolean add) {
        String key = context.getString(R.string.pref_stocks_key);
        Set<String> stocks = getStocks(context);

        if (add) {
            stocks.add(symbol);
        } else {
            stocks.remove(symbol);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(key, stocks);
        editor.apply();
    }

    public static void addStock(Context context, String symbol) {
        editStockPref(context, symbol, true);
    }

    public static void removeStock(Context context, String symbol) {
        editStockPref(context, symbol, false);
    }

    public static String getDisplayMode(Context context) {
        String key = context.getString(R.string.pref_display_mode_key);
        String defaultValue = context.getString(R.string.pref_display_mode_default);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(key, defaultValue);
    }

    public static void toggleDisplayMode(Context context) {
        String key = context.getString(R.string.pref_display_mode_key);
        String absoluteKey = context.getString(R.string.pref_display_mode_absolute_key);
        String percentageKey = context.getString(R.string.pref_display_mode_percentage_key);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String displayMode = getDisplayMode(context);

        SharedPreferences.Editor editor = prefs.edit();

        if (displayMode.equals(absoluteKey)) {
            editor.putString(key, percentageKey);
        } else {
            editor.putString(key, absoluteKey);
        }

        editor.apply();
    }

    public static String getTimeInterval(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String result = prefs.getString(context.getString(R.string.pref_time_interval_key),
                context.getString(R.string.pref_time_interval_default));

        return result;
    }

    public static boolean is5Days(Context context) {
        if (getTimeInterval(context).equalsIgnoreCase(context.getString(R.string.pref_time_interval_value_5_days))) {
            return true;
        }
        return false;
    }

    public static boolean is1Month(Context context) {
        if (getTimeInterval(context).equalsIgnoreCase(context.getString(R.string.pref_time_interval_value_1_month))) {
            return true;
        }
        return false;
    }

    public static boolean is3Month(Context context) {
        if (getTimeInterval(context).equalsIgnoreCase(context.getString(R.string.pref_time_interval_value_3_months))) {
            return true;
        }
        return false;
    }

    public static boolean is6Month(Context context) {
        if (getTimeInterval(context).equalsIgnoreCase(context.getString(R.string.pref_time_interval_value_6_months))) {
            return true;
        }
        return false;
    }

    public static boolean is1Year(Context context) {
        if (getTimeInterval(context).equalsIgnoreCase(context.getString(R.string.pref_time_interval_value_1_year))) {
            return true;
        }
        return false;
    }

    public static boolean is2Year(Context context) {
        if (getTimeInterval(context).equalsIgnoreCase(context.getString(R.string.pref_time_interval_value_2_years))) {
            return true;
        }
        return false;
    }

    public static boolean is5Year(Context context) {
        if (getTimeInterval(context).equalsIgnoreCase(context.getString(R.string.pref_time_interval_value_5_years))) {
            return true;
        }
        return false;
    }


    static public boolean isNetworkAvailable(Context c) {
        ConnectivityManager cm =
                (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    @SuppressWarnings("ResourceType")
    static public @QuoteSyncJob.ConnectionStatus
    int getConnectionStatus(Context c){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getInt(c.getString(R.string.pref_connection_status_key), QuoteSyncJob.CONNECTION_STATUS_UNKNOWN);
    }

}
