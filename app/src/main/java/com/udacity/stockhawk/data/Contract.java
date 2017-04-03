package com.udacity.stockhawk.data;


import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import com.google.common.collect.ImmutableList;

public final class Contract {

    static final String AUTHORITY = "com.udacity.stockhawk";
    static final String PATH_QUOTE = "quote";
    static final String PATH_QUOTE_WITH_SYMBOL = "quote/*";
    static final String PATH_QUOTE_WITH_ID = "quote/#";


    static final String PATH_HISTORY = "history";
    static final String PATH_HISTORY_HITH_ID = PATH_HISTORY + "/#";
    private static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    private Contract() {
    }

    @SuppressWarnings("unused")
    public static final class Quote implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_QUOTE).build();
        public static final String COLUMN_SYMBOL = "symbol";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_ABSOLUTE_CHANGE = "absolute_change";
        public static final String COLUMN_PERCENTAGE_CHANGE = "percentage_change";
        public static final int POSITION_ID = 0;
        public static final int POSITION_SYMBOL = 1;
        public static final int POSITION_PRICE = 2;
        public static final int POSITION_ABSOLUTE_CHANGE = 3;
        public static final int POSITION_PERCENTAGE_CHANGE = 4;
        public static final ImmutableList<String> QUOTE_COLUMNS = ImmutableList.of(
                _ID,
                COLUMN_SYMBOL,
                COLUMN_PRICE,
                COLUMN_ABSOLUTE_CHANGE,
                COLUMN_PERCENTAGE_CHANGE
        );
        static final String TABLE_NAME = "quotes";

        public static Uri makeUriForStock(String symbol) {
            return URI.buildUpon().appendPath(symbol).build();
        }

        static String getStockFromUri(Uri queryUri) {
            return queryUri.getLastPathSegment();
        }


    }

    public static final class HistoryEntry implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_HISTORY).build();
        public static final String TABLE_NAME = "history";
        public static final String QUOTE_KEY = "id_quote";
        public static final String DATE = "date";
        public static final String OPEN = "open";
        public static final String HIGH = "high";
        public static final String LOW = "low";
        public static final String CLOSE = "close";
        public static final String VOLUME = "volume";
        public static final String ADJ_CLOSE = "adj_close";

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + TABLE_NAME;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + TABLE_NAME;


        public static final int POSITION_ID = 0;
        public static final int POSITION_QUOTE_KEY = 1;
        public static final int POSITION_DATE = 2;
        public static final int POSITION_OPEN = 3;
        public static final int POSITION_HIGH = 4;
        public static final int POSITION_LOW = 5;
        public static final int POSITION_CLOSE = 6;
        public static final int POSITION_VOLUME = 7;
        public static final int POSITION_ADJ_CLOSE = 8;


        public static final ImmutableList<String> HISTORY_COLUMNS = ImmutableList.of(
                _ID,
                QUOTE_KEY,
                DATE,
                OPEN,
                HIGH,
                LOW,
                CLOSE,
                VOLUME,
                ADJ_CLOSE
        );

    }

}
