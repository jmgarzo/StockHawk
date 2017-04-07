package com.udacity.stockhawk.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.udacity.stockhawk.data.Contract.QuoteEntry;


class DbHelper extends SQLiteOpenHelper {


    private static final String NAME = "StockHawk.db";
    private static final int VERSION = 1;


    DbHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        ;

        String stockBuilder = "CREATE TABLE " + Contract.StockEntry.TABLE_NAME + " ("
                + Contract.StockEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Contract.StockEntry.COLUMN_CURRENCY + " TEXT NOT NULL, "
                + Contract.StockEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + Contract.StockEntry.COLUMN_STOCKEXCHANGE + " TEXT NOT NULL, "
                + Contract.StockEntry.COLUMN_SYMBOL + " TEXT NOT NULL, "
                + "UNIQUE (" + Contract.StockEntry.COLUMN_SYMBOL + ") ON CONFLICT REPLACE);";

        db.execSQL(stockBuilder);


        String builder = "CREATE TABLE " + QuoteEntry.TABLE_NAME + " ("
                + QuoteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + QuoteEntry.COLUMN_SYMBOL + " TEXT NOT NULL, "
                + QuoteEntry.COLUMN_PRICE + " REAL NOT NULL, "
                + QuoteEntry.COLUMN_ABSOLUTE_CHANGE + " REAL NOT NULL, "
                + QuoteEntry.COLUMN_PERCENTAGE_CHANGE + " REAL NOT NULL, " +
                " FOREIGN KEY (" + Contract.QuoteEntry.QUOTE_KEY + ") REFERENCES " +
                QuoteEntry.TABLE_NAME + " (" + QuoteEntry._ID + ") ON DELETE CASCADE " +
                "UNIQUE (" + Contract.QuoteEntry.COLUMN_SYMBOL + ") ON CONFLICT REPLACE);";

        db.execSQL(builder);

        String historyBuilder = "CREATE TABLE " + Contract.HistoryEntry.TABLE_NAME + " ( " +
                Contract.HistoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
                Contract.HistoryEntry.QUOTE_KEY + " INTEGER NOT NULL , " +
                Contract.HistoryEntry.DATE + " REAL NOT NULL , " +
                Contract.HistoryEntry.OPEN + " REAL NOT NULL, " +
                Contract.HistoryEntry.HIGH + " REAL NOT NULL, " +
                Contract.HistoryEntry.LOW + " REAL NOT NULL, " +
                Contract.HistoryEntry.CLOSE + " REAL NOT NULL, " +
                Contract.HistoryEntry.VOLUME + " INTEGER NOT NULL, " +
                Contract.HistoryEntry.ADJ_CLOSE + " REAL NOT NULL, " +
                " FOREIGN KEY (" + Contract.HistoryEntry.QUOTE_KEY + ") REFERENCES " +
                QuoteEntry.TABLE_NAME + " (" + QuoteEntry._ID + ") ON DELETE CASCADE " +
                ");";
        db.execSQL(historyBuilder);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(" DROP TABLE IF EXISTS " + Contract.HistoryEntry.TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + QuoteEntry.TABLE_NAME);

        onCreate(db);
    }
}
