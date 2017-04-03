package com.udacity.stockhawk.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.udacity.stockhawk.data.Contract.Quote;


class DbHelper extends SQLiteOpenHelper {


    private static final String NAME = "StockHawk.db";
    private static final int VERSION = 1;


    DbHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String builder = "CREATE TABLE " + Quote.TABLE_NAME + " ("
                + Quote._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Quote.COLUMN_SYMBOL + " TEXT NOT NULL, "
                + Quote.COLUMN_PRICE + " REAL NOT NULL, "
                + Quote.COLUMN_ABSOLUTE_CHANGE + " REAL NOT NULL, "
                + Quote.COLUMN_PERCENTAGE_CHANGE + " REAL NOT NULL, "
                + "UNIQUE (" + Quote.COLUMN_SYMBOL + ") ON CONFLICT REPLACE);";

        db.execSQL(builder);

        String historyBuilder =  "CREATE TABLE " + Contract.HistoryEntry.TABLE_NAME + " ( " +
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
                Quote.TABLE_NAME + " (" + Quote._ID + ") ON DELETE CASCADE " +
                ");";
        db.execSQL(historyBuilder);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(" DROP TABLE IF EXISTS " + Contract.HistoryEntry.TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + Quote.TABLE_NAME);

        onCreate(db);
    }
}
