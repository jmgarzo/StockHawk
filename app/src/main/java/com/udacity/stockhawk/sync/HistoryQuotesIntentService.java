package com.udacity.stockhawk.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.udacity.stockhawk.ui.DetailFragment;

/**
 * Created by jmgarzo on 05/04/17.
 */

public class HistoryQuotesIntentService extends IntentService {

    public HistoryQuotesIntentService() {
        super("HistoryQuotesIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        int idQuote = intent.getIntExtra(DetailFragment.ID_QUOTE_TAG,0);
        QuoteSyncJob.addHistoryQuotes(this, idQuote);
    }
}
