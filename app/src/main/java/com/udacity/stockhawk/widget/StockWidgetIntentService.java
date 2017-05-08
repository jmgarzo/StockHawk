package com.udacity.stockhawk.widget;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.ui.MainActivity;

import static android.R.attr.description;

/**
 * Created by jmgarzo on 08/05/17.
 */

public class StockWidgetIntentService extends IntentService {


    public StockWidgetIntentService() {
        super("StockWidgetIntentServices");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                StockWidgetProvider.class));

        Cursor data = getContentResolver().query(
                Contract.QuoteEntry.CONTENT_URI,
                Contract.QuoteEntry.QUOTE_COLUMNS.toArray(new String[]{}),
                null,
                null,
                Contract.QuoteEntry.COLUMN_SYMBOL + " ASC");
        if (data == null) {
            return;
        }
        if (!data.moveToFirst()) {
            data.close();
            return;
        }

        String symbol = data.getString(Contract.QuoteEntry.POSITION_SYMBOL);
        float price = data.getFloat(Contract.QuoteEntry.POSITION_PRICE);
        float rawAbsoluteChange = data.getFloat(Contract.QuoteEntry.POSITION_ABSOLUTE_CHANGE);


        for (int appWidgetId : appWidgetIds) {
            int layoutId = R.layout.widget_stock;
            RemoteViews views = new RemoteViews(getPackageName(), layoutId);

            // Add the data to the RemoteViews
            views.setTextViewText(R.id.symbol, symbol);
            views.setTextViewText(R.id.price, Float.toString(price));
            views.setTextViewText(R.id.change,Float.toString(rawAbsoluteChange));



            // Create an Intent to launch MainActivity
            Intent launchIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }


}
