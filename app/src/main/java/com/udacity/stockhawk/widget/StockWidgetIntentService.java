package com.udacity.stockhawk.widget;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.ui.MainActivity;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import static android.R.attr.description;

/**
 * Created by jmgarzo on 08/05/17.
 */

public class StockWidgetIntentService extends IntentService {

    private  DecimalFormat dollarFormatWithPlus;
    private  DecimalFormat dollarFormat;

    public StockWidgetIntentService() {
        super("StockWidgetIntentServices");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus.setPositivePrefix("+$");
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                StockWidgetProvider.class));

        Cursor data = getContentResolver().query(
                Contract.StockQuoteEntry.CONTENT_URI,
                Contract.StockQuoteEntry.STOCK_QUOTE_COLUMNS.toArray(new String[]{}),
                null,
                null,
                Contract.StockQuoteEntry.COLUMN_SYMBOL + " ASC");
        if (data == null) {
            return;
        }
        if (!data.moveToFirst()) {
            data.close();
            return;
        }

        String name  = data.getString(Contract.StockQuoteEntry.POSITION_NAME);
        String symbol = data.getString(Contract.StockQuoteEntry.POSITION_SYMBOL);
        float price = data.getFloat(Contract.StockQuoteEntry.POSITION_PRICE);
        float rawAbsoluteChange = data.getFloat(Contract.StockQuoteEntry.POSITION_ABSOLUTE_CHANGE);
        String change = dollarFormatWithPlus.format(rawAbsoluteChange);

        for (int appWidgetId : appWidgetIds) {
            int widgetWidth = getWidgetWidth(appWidgetManager, appWidgetId);
            int defaultWidth = getResources().getDimensionPixelSize(R.dimen.widget_stock_default_width);
            int largeWidth = getResources().getDimensionPixelSize(R.dimen.widget_stock_large_width);
            int layoutId;
            if (widgetWidth >= largeWidth) {
                layoutId = R.layout.widget_stock_large;
            } else if (widgetWidth >= defaultWidth) {
                layoutId = R.layout.widget_stock;
            } else {
                layoutId = R.layout.widget_stock_small;
            }
            RemoteViews views = new RemoteViews(getPackageName(), layoutId);

            // Add the data to the RemoteViews
            views.setTextViewText(R.id.name,name);
            views.setTextViewText(R.id.symbol, symbol);
            views.setTextViewText(R.id.price, dollarFormat.format(data.getFloat(Contract.StockQuoteEntry.POSITION_PRICE)));
            views.setTextViewText(R.id.change,change);



            // Create an Intent to launch MainActivity
            Intent launchIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private int getWidgetWidth(AppWidgetManager appWidgetManager, int appWidgetId) {
        // Prior to Jelly Bean, widgets were always their default size
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            return getResources().getDimensionPixelSize(R.dimen.widget_stock_default_width);
        }
        // For Jelly Bean and higher devices, widgets can be resized - the current size can be
        // retrieved from the newly added App Widget Options
        return getWidgetWidthFromOptions(appWidgetManager, appWidgetId);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private int getWidgetWidthFromOptions(AppWidgetManager appWidgetManager, int appWidgetId) {
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        if (options.containsKey(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)) {
            int minWidthDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
            // The width returned is in dp, but we'll convert it to pixels to match the other widths
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, minWidthDp,
                    displayMetrics);
        }
        return  getResources().getDimensionPixelSize(R.dimen.widget_stock_default_width);
    }

}
