package com.udacity.stockhawk.data;


import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import com.google.common.collect.ImmutableList;

public final class Contract {

    static final String AUTHORITY = "com.udacity.stockhawk";


    static final String PATH_STOCK = "stock";
    static final String PATH_STOCK_WITH_SYMBOL = PATH_STOCK + "/*";
    static final String PATH_STOCK_WITH_ID =  PATH_STOCK + "/#";

    static final String PATH_QUOTE = "quotes";
    static final String PATH_QUOTE_WITH_SYMBOL = PATH_QUOTE + "/*";
    static final String PATH_QUOTE_WITH_ID =  PATH_QUOTE + "/#";


    static final String PATH_HISTORY = "history";
    static final String PATH_HISTORY_WITH_ID = PATH_HISTORY + "/#";

    static final String PATH_STOCK_QUOTE = "stock_quote";

    private static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    private Contract() {
    }


    public static final class StockEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH_STOCK).build();

        public static final String TABLE_NAME = "stock";

        public static final String COLUMN_CURRENCY = "currency";
        public static final String COLUMN_NAME ="name";
        public static final String COLUMN_STOCKEXCHANGE = "stockexchange";
        public static final String COLUMN_SYMBOL = "symbol";
        public static final int POSITION_ID = 0;
        public static final int POSITION_NAME = 1;
        public static final int POSITION_STOCKEXCHANGE = 2;
        public static final int POSITION_SYMBOL = 3;
        public static final ImmutableList<String> STOCK_COLUMNS = ImmutableList.of(
                _ID,
                COLUMN_CURRENCY,
                COLUMN_NAME,
                COLUMN_STOCKEXCHANGE,
                COLUMN_SYMBOL
        );
    }


    //@SuppressWarnings("unused")
    public static final class QuoteEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH_QUOTE).build();

        public static final String TABLE_NAME = "quotes";

        public static final String COLUMN_STOCK_KEY = "id_stock";
        public static final String COLUMN_SYMBOL = "symbol";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_ABSOLUTE_CHANGE = "absolute_change";
        public static final String COLUMN_PERCENTAGE_CHANGE = "percentage_change";
        public static final int POSITION_ID = 0;
        public static final int POSITION_STOCK_KEY = 1;
        public static final int POSITION_SYMBOL = 2;
        public static final int POSITION_PRICE = 3;
        public static final int POSITION_ABSOLUTE_CHANGE = 4;
        public static final int POSITION_PERCENTAGE_CHANGE = 5;
        public static final ImmutableList<String> QUOTE_COLUMNS = ImmutableList.of(
                _ID,
                COLUMN_STOCK_KEY,
                COLUMN_SYMBOL,
                COLUMN_PRICE,
                COLUMN_ABSOLUTE_CHANGE,
                COLUMN_PERCENTAGE_CHANGE
        );



        public static Uri makeUriForStock(String symbol) {
            return CONTENT_URI.buildUpon().appendPath(symbol).build();
        }

        static String getStockFromUri(Uri queryUri) {
            return queryUri.getLastPathSegment();
        }


    }

    public static final class HistoryEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH_HISTORY).build();
        public static final String TABLE_NAME = "history";
        public static final String COLUMN_QUOTE_KEY = "id_quote";
        public static final String COLUMN_SYMBOL = "symbol";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_OPEN = "open";
        public static final String COLUMN_HIGH = "high";
        public static final String COLUMN_LOW = "low";
        public static final String COLUMN_CLOSE = "close";
        public static final String COLUMN_VOLUME = "volume";
        public static final String COLUMN_ADJ_CLOSE = "adj_close";
        public static final String COLUMN_REGISTRY_TYPE = "registry_type";

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + TABLE_NAME;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + TABLE_NAME;


        public static final int POSITION_ID = 0;
        public static final int POSITION_QUOTE_KEY = 1;
        public static final int POSITION_SYMBOL=2;
        public static final int POSITION_DATE = 3;
        public static final int POSITION_OPEN = 4;
        public static final int POSITION_HIGH = 5;
        public static final int POSITION_LOW = 6;
        public static final int POSITION_CLOSE = 7;
        public static final int POSITION_VOLUME = 8;
        public static final int POSITION_ADJ_CLOSE = 9;
        public static final int POSITION_REGISTRY_TYPE = 10;


        public static final ImmutableList<String> HISTORY_COLUMNS = ImmutableList.of(
                _ID,
                COLUMN_QUOTE_KEY,
                COLUMN_SYMBOL,
                COLUMN_DATE,
                COLUMN_OPEN,
                COLUMN_HIGH,
                COLUMN_LOW,
                COLUMN_CLOSE,
                COLUMN_VOLUME,
                COLUMN_ADJ_CLOSE,
                COLUMN_REGISTRY_TYPE
        );

    }

    public static final class StockQuoteEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH_STOCK_QUOTE).build();

        public static final String TABLE_NAME = Contract.StockEntry.TABLE_NAME +
                " INNER JOIN " + Contract.QuoteEntry.TABLE_NAME +
                " ON " +
                Contract.StockEntry.TABLE_NAME + "."+ Contract.StockEntry._ID  +
                " = "+ Contract.QuoteEntry.TABLE_NAME + "."+ Contract.QuoteEntry.COLUMN_STOCK_KEY;

        public static final String COLUMN_STOCK_ID = StockEntry.TABLE_NAME + "." +
                StockEntry._ID;
        public static final String COLUMN_QUOTE_ID = QuoteEntry.TABLE_NAME + "." +
                QuoteEntry._ID;
        public static final String COLUMN_CURRENCY = StockEntry.COLUMN_CURRENCY;
        public static final String COLUMN_NAME =StockEntry.COLUMN_NAME;
        public static final String COLUMN_STOCKEXCHANGE = StockEntry.COLUMN_STOCKEXCHANGE;
        public static final String COLUMN_SYMBOL = QuoteEntry.TABLE_NAME + "." +
                QuoteEntry.COLUMN_SYMBOL;
        public static final String COLUMN_PRICE = QuoteEntry.COLUMN_PRICE;
        public static final String COLUMN_ABSOLUTE_CHANGE = QuoteEntry.COLUMN_ABSOLUTE_CHANGE;
        public static final String COLUMN_PERCENTAGE_CHANGE = QuoteEntry.COLUMN_PERCENTAGE_CHANGE;
        public static final int POSITION_STOCK_ID = 0;
        public static final int POSITION_QUOTE_ID = 1;
        public static final int POSITION_CURRENCY = 2;
        public static final int POSITION_NAME = 3;
        public static final int POSITION_STOCKEXCHANGE  = 4;
        public static final int POSITION_SYMBOL = 5;
        public static final int POSITION_PRICE = 6;
        public static final int POSITION_ABSOLUTE_CHANGE = 7;
        public static final int POSITION_PERCENTAGE_CHANGE = 8;


        public static final ImmutableList<String> STOCK_QUOTE_COLUMNS = ImmutableList.of(
                COLUMN_STOCK_ID,
                COLUMN_QUOTE_ID,
                COLUMN_CURRENCY,
                COLUMN_NAME,
                COLUMN_STOCKEXCHANGE,
                COLUMN_SYMBOL,
                COLUMN_PRICE,
                COLUMN_ABSOLUTE_CHANGE,
                COLUMN_PERCENTAGE_CHANGE
        );
    }

}
