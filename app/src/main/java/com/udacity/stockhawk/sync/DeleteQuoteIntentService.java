package com.udacity.stockhawk.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by jmgarzo on 07/05/17.
 */

public class DeleteQuoteIntentService extends IntentService {
    public DeleteQuoteIntentService() {
        super("DeleteQuoteIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String symbol = intent.getStringExtra(Intent.EXTRA_TEXT);
        QuoteSyncJob.deleteQuoteDBandPreferences(this, symbol);
    }
}
