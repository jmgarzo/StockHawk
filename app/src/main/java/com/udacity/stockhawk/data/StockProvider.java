package com.udacity.stockhawk.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


public class StockProvider extends ContentProvider {

    private static final int QUOTE = 100;
    private static final int QUOTE_FOR_SYMBOL = 101;
    private static final int QUOTE_WITH_ID = 102;

    private static final int HISTORY = 200;
    private static final int HISTORY_WITH_ID = 201;


    private static final int STOCK = 300;
    private static final int STOCK_FOR_SYMBOL = 301;
    private static final int STOCK_WITH_ID = 302;


    private static final UriMatcher uriMatcher = buildUriMatcher();

    private DbHelper dbHelper;

    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(Contract.AUTHORITY, Contract.PATH_QUOTE, QUOTE);
        matcher.addURI(Contract.AUTHORITY, Contract.PATH_QUOTE_WITH_SYMBOL, QUOTE_FOR_SYMBOL);
        matcher.addURI(Contract.AUTHORITY, Contract.PATH_QUOTE_WITH_ID, QUOTE_WITH_ID);

        matcher.addURI(Contract.AUTHORITY, Contract.PATH_STOCK, STOCK);
        matcher.addURI(Contract.AUTHORITY, Contract.PATH_STOCK_WITH_SYMBOL, STOCK_FOR_SYMBOL);
        matcher.addURI(Contract.AUTHORITY, Contract.PATH_STOCK_WITH_ID, STOCK_WITH_ID);

        matcher.addURI(Contract.AUTHORITY,Contract.PATH_HISTORY,HISTORY);
        matcher.addURI(Contract.AUTHORITY,Contract.PATH_HISTORY_WITH_ID,HISTORY);
        return matcher;
    }


    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor returnCursor;
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        switch (uriMatcher.match(uri)) {

            case STOCK:
                returnCursor = db.query(
                        Contract.StockEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case STOCK_FOR_SYMBOL:
                returnCursor = db.query(
                        Contract.StockEntry.TABLE_NAME,
                        projection,
                        Contract.StockEntry.COLUMN_SYMBOL + " = ?",
                        new String[]{uri.getLastPathSegment()},
                        null,
                        null,
                        sortOrder
                );

                break;

            case STOCK_WITH_ID:
                returnCursor = db.query(
                        Contract.StockEntry.TABLE_NAME,
                        projection,
                        Contract.StockEntry._ID + " = ?",
                        new String[]{uri.getPathSegments().get(1)},
                        null,
                        null,
                        sortOrder
                );
                break;


            case QUOTE:
                returnCursor = db.query(
                        Contract.QuoteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case QUOTE_FOR_SYMBOL:
                returnCursor = db.query(
                        Contract.QuoteEntry.TABLE_NAME,
                        projection,
                        Contract.QuoteEntry.COLUMN_SYMBOL + " = ?",
                        new String[]{Contract.QuoteEntry.getStockFromUri(uri)},
                        null,
                        null,
                        sortOrder
                );

                break;

            case QUOTE_WITH_ID:
                returnCursor = db.query(
                        Contract.QuoteEntry.TABLE_NAME,
                        projection,
                        Contract.QuoteEntry._ID + " = ?",
                        new String[]{uri.getPathSegments().get(1)},
                        null,
                        null,
                        sortOrder
                );
                break;

            case HISTORY:
            returnCursor = db.query(
                    Contract.HistoryEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
            );
            break;
            case HISTORY_WITH_ID:
                returnCursor = db.query(
                        Contract.HistoryEntry.TABLE_NAME,
                        projection,
                        Contract.HistoryEntry._ID + " = ?",
                        new String[]{uri.getPathSegments().get(1)},
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown CONTENT_URI:" + uri);
        }

        Context context = getContext();
        if (context != null){
            returnCursor.setNotificationUri(context.getContentResolver(), uri);
        }

        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri returnUri;
        long id;

        switch (uriMatcher.match(uri)) {

            case STOCK:
                id = db.insert(
                        Contract.StockEntry.TABLE_NAME,
                        null,
                        values
                );
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(Contract.StockEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;

            case QUOTE:
                id = db.insert(
                        Contract.QuoteEntry.TABLE_NAME,
                        null,
                        values
                );
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(Contract.QuoteEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;

            case HISTORY:
                id = db.insert(
                        Contract.HistoryEntry.TABLE_NAME,
                        null,
                        values
                );
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(Contract.QuoteEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown CONTENT_URI:" + uri);
        }

        Context context = getContext();
        if (context != null){
            context.getContentResolver().notifyChange(uri, null);
        }

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted;

        if (null == selection) {
            selection = "1";
        }
        switch (uriMatcher.match(uri)) {

            case STOCK:
                rowsDeleted = db.delete(
                        Contract.StockEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );

                break;

            case STOCK_FOR_SYMBOL:
                rowsDeleted = db.delete(
                        Contract.QuoteEntry.TABLE_NAME,
                        '"' + uri.getLastPathSegment() + '"' + " =" + Contract.StockEntry.COLUMN_SYMBOL,
                        selectionArgs
                );
                break;

            case STOCK_WITH_ID:
                rowsDeleted = db.delete(
                        Contract.StockEntry.TABLE_NAME,
                        '"' + uri.getPathSegments().get(1) + '"' + " = " + Contract.StockEntry._ID,
                        selectionArgs

                );
                break;

            case QUOTE:
                rowsDeleted = db.delete(
                        Contract.QuoteEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );

                break;

            case QUOTE_FOR_SYMBOL:
                String symbol = Contract.QuoteEntry.getStockFromUri(uri);
                rowsDeleted = db.delete(
                        Contract.QuoteEntry.TABLE_NAME,
                        '"' + symbol + '"' + " =" + Contract.QuoteEntry.COLUMN_SYMBOL,
                        selectionArgs
                );
                break;

            case QUOTE_WITH_ID:
                rowsDeleted = db.delete(
                        Contract.QuoteEntry.TABLE_NAME,
                        '"' + uri.getPathSegments().get(1) + '"' + " = " + Contract.QuoteEntry._ID,
                        selectionArgs

                );
                break;

            case HISTORY:
                rowsDeleted = db.delete(
                        Contract.HistoryEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;

            case HISTORY_WITH_ID:
                rowsDeleted = db.delete(
                        Contract.HistoryEntry.TABLE_NAME,
                        '"' + uri.getPathSegments().get(1) + '"' + " = " + Contract.HistoryEntry._ID,
                        selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown CONTENT_URI:" + uri);
        }

        if (rowsDeleted != 0) {
            Context context = getContext();
            if (context != null){
                context.getContentResolver().notifyChange(uri, null);
            }
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {

        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        int returnCount = 0;
        Context context;

        switch (uriMatcher.match(uri)) {

            case STOCK:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        db.insert(
                                Contract.StockEntry.TABLE_NAME,
                                null,
                                value
                        );
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                context = getContext();
                if (context != null) {
                    context.getContentResolver().notifyChange(uri, null);
                }

                return returnCount;

            case QUOTE:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        db.insert(
                                Contract.QuoteEntry.TABLE_NAME,
                                null,
                                value
                        );
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                context = getContext();
                if (context != null) {
                    context.getContentResolver().notifyChange(uri, null);
                }

                return returnCount;


            case HISTORY:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        db.insert(
                                Contract.HistoryEntry.TABLE_NAME,
                                null,
                                value
                        );
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                context = getContext();
                if (context != null) {
                    context.getContentResolver().notifyChange(uri, null);
                }

                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }


    }
}
