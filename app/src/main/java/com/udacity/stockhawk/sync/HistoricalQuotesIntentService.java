package com.udacity.stockhawk.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by jmgarzo on 05/04/17.
 */

public class HistoricalQuotesIntentService extends IntentService {

    public HistoricalQuotesIntentService(){
        super("HistoricalQuotesIntentService");
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        QuoteSyncJob.addHistoricalQuotes(this);
    }
}
