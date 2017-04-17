package com.udacity.stockhawk.sync;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.objects.MyQuote;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;
import yahoofinance.quotes.stock.StockQuote;


/**
 * Created by jmgarzo on 07/04/17.
 */

public class SyncUtils {

    private static String LOG_TAG = SyncUtils.class.getSimpleName();


    public static void addStockAndQuote(Context context, Stock stock) {

        if (null == stock.getStockExchange()) {
            ContentValues cvStock = new ContentValues();
            cvStock.put(Contract.StockEntry.COLUMN_CURRENCY, context.getString(R.string.non_existent_value));
            cvStock.put(Contract.StockEntry.COLUMN_NAME, context.getString(R.string.non_existent_value));
            cvStock.put(Contract.StockEntry.COLUMN_STOCKEXCHANGE, -1);
            cvStock.put(Contract.StockEntry.COLUMN_SYMBOL, stock.getSymbol());

            Uri quoteUri = context.getContentResolver().insert(Contract.StockEntry.CONTENT_URI, cvStock);
            String quoteId = quoteUri.getLastPathSegment();

            StockQuote quote = stock.getQuote();


            ContentValues cvQuote = new ContentValues();
            cvQuote.put(Contract.QuoteEntry.COLUMN_STOCK_KEY, quoteId);
            cvQuote.put(Contract.QuoteEntry.COLUMN_SYMBOL, quote.getSymbol() + " " +
                    context.getString(R.string.non_existent_value));
            cvQuote.put(Contract.QuoteEntry.COLUMN_PRICE, -1);
            cvQuote.put(Contract.QuoteEntry.COLUMN_ABSOLUTE_CHANGE, -1);
            cvQuote.put(Contract.QuoteEntry.COLUMN_PERCENTAGE_CHANGE, -1);

            context.getContentResolver().insert(Contract.QuoteEntry.CONTENT_URI, cvQuote);

        } else {
            ContentValues cvStock = new ContentValues();
            cvStock.put(Contract.StockEntry.COLUMN_CURRENCY, stock.getCurrency());
            cvStock.put(Contract.StockEntry.COLUMN_NAME, stock.getName());
            cvStock.put(Contract.StockEntry.COLUMN_STOCKEXCHANGE, stock.getStockExchange());
            cvStock.put(Contract.StockEntry.COLUMN_SYMBOL, stock.getSymbol());

            Uri quoteUri = context.getContentResolver().insert(Contract.StockEntry.CONTENT_URI, cvStock);
            String quoteId = quoteUri.getLastPathSegment();

            StockQuote quote = stock.getQuote();


            ContentValues cvQuote = new ContentValues();
            cvQuote.put(Contract.QuoteEntry.COLUMN_STOCK_KEY, quoteId);
            cvQuote.put(Contract.QuoteEntry.COLUMN_SYMBOL, quote.getSymbol());
            cvQuote.put(Contract.QuoteEntry.COLUMN_PRICE, quote.getPrice().doubleValue());
            cvQuote.put(Contract.QuoteEntry.COLUMN_ABSOLUTE_CHANGE, quote.getChange().doubleValue());
            cvQuote.put(Contract.QuoteEntry.COLUMN_PERCENTAGE_CHANGE,
                    quote.getChangeInPercent().doubleValue());

            context.getContentResolver().insert(Contract.QuoteEntry.CONTENT_URI, cvQuote);
        }

    }

    public static int cleanDB(Context context) {

        int deletedStock = context.getContentResolver().delete(Contract.StockEntry.CONTENT_URI, null, null);
        Log.d(LOG_TAG, deletedStock + " Stocks Deleted. ");

        int deletedQuote = context.getContentResolver().delete(Contract.QuoteEntry.CONTENT_URI, null, null);
        Log.d(LOG_TAG, deletedQuote + " Quotes Deleted. ");

        int deletedHistory = context.getContentResolver().delete(Contract.HistoryEntry.CONTENT_URI, null, null);
        Log.d(LOG_TAG, deletedHistory + " Histories Deleted. ");

        return deletedStock + deletedQuote + deletedHistory;
    }


    //TODO: Si lo quiero dejar unir con el addHistory(Context, String symbol); Sino pues borrarlo
//    public static void addHistory(Context context, int idQuote) {
//
//        ArrayList<MyQuote> quotesList = getQuoteDB(context, idQuote);
//
//
////        Calendar from = Calendar.getInstance();
////        Calendar to = Calendar.getInstance();
//
//        //from.add(Calendar.YEAR, -1); // from 5 years ago
//        //from.add(Calendar.DAY_OF_MONTH, -5);
//
//
//        if (null != quotesList && quotesList.size() > 0) {
//
//            for (MyQuote quote : quotesList) {
//                ContentValues[] cvArray = null;
//                try {
//                    Stock stock = loadStockByPreference(context, quote);
////                    Stock stock = YahooFinance.get(quote.getSymbol(), from, to, Interval.DAILY);
//                    //Stock stock = YahooFinance.get(quote.getSymbol(),true);
//                    if (null != stock) {
//                        List<HistoricalQuote> historyList = stock.getHistory();
//                        cvArray = new ContentValues[historyList.size()];
//                        for (int i = 0; historyList.size() > i; i++) {
//
//                            ContentValues cv = new ContentValues();
//
//                            cv.put(Contract.HistoryEntry.COLUMN_QUOTE_KEY, quote.getStockId());
//                            cv.put(Contract.HistoryEntry.COLUMN_SYMBOL, historyList.get(i).getSymbol());
//                            cv.put(Contract.HistoryEntry.COLUMN_DATE, historyList.get(i).getDate().getTimeInMillis());
//                            cv.put(Contract.HistoryEntry.COLUMN_OPEN, historyList.get(i).getOpen().doubleValue());
//                            cv.put(Contract.HistoryEntry.COLUMN_HIGH, historyList.get(i).getHigh().doubleValue());
//                            cv.put(Contract.HistoryEntry.COLUMN_LOW, historyList.get(i).getLow().doubleValue());
//                            cv.put(Contract.HistoryEntry.COLUMN_CLOSE, historyList.get(i).getClose().doubleValue());
//                            cv.put(Contract.HistoryEntry.COLUMN_VOLUME, historyList.get(i).getVolume());
//                            cv.put(Contract.HistoryEntry.COLUMN_ADJ_CLOSE, historyList.get(i).getAdjClose().doubleValue());
//                            cv.put(Contract.HistoryEntry.COLUMN_REGISTRY_TYPE, PrefUtils.getTimeInterval(context));
//
//                            cvArray[i] = cv;
//                        }
//                    }
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                if (null != cvArray && cvArray.length > 0) {
//                    context.getContentResolver().delete(Contract.HistoryEntry.CONTENT_URI,
//                            Contract.HistoryEntry.COLUMN_QUOTE_KEY + " = ?",
//                            new String[]{Integer.toString(quote.getId())});
//                    context.getContentResolver().bulkInsert(Contract.HistoryEntry.CONTENT_URI, cvArray);
//                }
//
//            }
//        }
//    }

    public static void addHistory(Context context, String symbol) {

        ArrayList<MyQuote> quotesList = getQuoteDB(context, symbol);

////        Calendar from = Calendar.getInstance();
////        Calendar to = Calendar.getInstance();
//
//        //from.add(Calendar.YEAR, -1); // from 5 years ago
//        //from.add(Calendar.DAY_OF_MONTH, -5);
        if (null != quotesList && quotesList.size() > 0) {

            for (MyQuote quote : quotesList) {
                ContentValues[] cvArray = null;
                try {
                    Stock stock = loadStockByPreference(context, quote);

                    if (null != stock) {
                        List<HistoricalQuote> historyList = stock.getHistory();
                        cvArray = new ContentValues[historyList.size()];
                        for (int i = 0; historyList.size() > i; i++) {

                            ContentValues cv = new ContentValues();

                            cv.put(Contract.HistoryEntry.COLUMN_QUOTE_KEY, quote.getStockId());
                            cv.put(Contract.HistoryEntry.COLUMN_SYMBOL, historyList.get(i).getSymbol());
                            cv.put(Contract.HistoryEntry.COLUMN_DATE, historyList.get(i).getDate().getTimeInMillis());
                            cv.put(Contract.HistoryEntry.COLUMN_OPEN, historyList.get(i).getOpen().doubleValue());
                            cv.put(Contract.HistoryEntry.COLUMN_HIGH, historyList.get(i).getHigh().doubleValue());
                            cv.put(Contract.HistoryEntry.COLUMN_LOW, historyList.get(i).getLow().doubleValue());
                            cv.put(Contract.HistoryEntry.COLUMN_CLOSE, historyList.get(i).getClose().doubleValue());
                            cv.put(Contract.HistoryEntry.COLUMN_VOLUME, historyList.get(i).getVolume());
                            cv.put(Contract.HistoryEntry.COLUMN_ADJ_CLOSE, historyList.get(i).getAdjClose().doubleValue());
                            cv.put(Contract.HistoryEntry.COLUMN_REGISTRY_TYPE, PrefUtils.getTimeInterval(context));

                            cvArray[i] = cv;
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (null != cvArray && cvArray.length > 0) {
                    context.getContentResolver().delete(Contract.HistoryEntry.CONTENT_URI,
                            Contract.HistoryEntry.COLUMN_QUOTE_KEY + " = ?",
                            new String[]{Integer.toString(quote.getId())});
                    context.getContentResolver().bulkInsert(Contract.HistoryEntry.CONTENT_URI, cvArray);
                }

            }
        }
    }

    public static ArrayList<String> getSymbolsDB(Context context) {
        Cursor cursor = context.getContentResolver().query(
                Contract.QuoteEntry.CONTENT_URI,
                new String[]{Contract.QuoteEntry.COLUMN_SYMBOL},
                null,
                null,
                Contract.QuoteEntry.COLUMN_SYMBOL
        );
        ArrayList<String> symbolList = null;
        if (null != cursor && cursor.moveToFirst()) {

            int index = cursor.getColumnIndex(Contract.QuoteEntry.COLUMN_SYMBOL);
            symbolList = new ArrayList<>();
            do {
                symbolList.add(cursor.getString(index));
            } while (cursor.moveToNext());

        }
        return symbolList;
    }

    public static ArrayList<Integer> getQuoteIdDB(Context context) {
        Cursor cursor = context.getContentResolver().query(
                Contract.QuoteEntry.CONTENT_URI,
                new String[]{Contract.QuoteEntry._ID},
                null,
                null,
                Contract.QuoteEntry._ID
        );
        ArrayList<Integer> quoteIdList = null;
        if (null != cursor && cursor.moveToFirst()) {

            int index = cursor.getColumnIndex(Contract.QuoteEntry._ID);
            quoteIdList = new ArrayList<>();
            do {
                quoteIdList.add(cursor.getInt(index));
            } while (cursor.moveToNext());

        }
        return quoteIdList;
    }


    public static ArrayList<MyQuote> getQuoteDB(Context context, int idQuote) {
        //String selection =

        Cursor cursor = context.getContentResolver().query(
                Contract.QuoteEntry.CONTENT_URI,
                Contract.QuoteEntry.QUOTE_COLUMNS.toArray(new String[]{}),
                Contract.QuoteEntry._ID + " = ?",
                new String[]{Integer.toString(idQuote)},
                Contract.QuoteEntry._ID
        );
        ArrayList<MyQuote> quoteList = null;

        if (null != cursor && cursor.moveToFirst()) {
            quoteList = new ArrayList<>();
            for (int i = 0; i < cursor.getCount(); i++) {
                MyQuote quote = new MyQuote(cursor, i);

                quoteList.add(quote);
            }
        }
        return quoteList;
    }

    public static ArrayList<MyQuote> getQuoteDB(Context context, String symbol) {

        Cursor cursor = context.getContentResolver().query(
                Contract.QuoteEntry.CONTENT_URI,
                Contract.QuoteEntry.QUOTE_COLUMNS.toArray(new String[]{}),
                Contract.QuoteEntry.COLUMN_SYMBOL + " = ?",
                new String[]{symbol},
                Contract.QuoteEntry._ID
        );
        ArrayList<MyQuote> quoteList = null;

        if (null != cursor && cursor.moveToFirst()) {
            quoteList = new ArrayList<>();
            for (int i = 0; i < cursor.getCount(); i++) {
                MyQuote quote = new MyQuote(cursor, i);
                quoteList.add(quote);
            }
        }
        return quoteList;
    }

    private static Stock loadStockByPreference(Context context, MyQuote quote) {
        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();
        try {
            if (PrefUtils.is5Days(context)) {
                from.add(Calendar.DAY_OF_MONTH, -5);
                return YahooFinance.get(quote.getSymbol(), from, to, Interval.DAILY);
            } else if (PrefUtils.is1Month(context)) {
                from.add(Calendar.MONTH, -1);
                return YahooFinance.get(quote.getSymbol(), from, to, Interval.DAILY);
            } else if (PrefUtils.is3Month(context)) {
                from.add(Calendar.MONTH, -3);
                return YahooFinance.get(quote.getSymbol(), from, to, Interval.DAILY);
            } else if (PrefUtils.is6Month(context)) {
                from.add(Calendar.MONTH, -6);
                return YahooFinance.get(quote.getSymbol(), from, to, Interval.DAILY);
            } else if (PrefUtils.is1Year(context)) {
                from.add(Calendar.YEAR, -1);
                return YahooFinance.get(quote.getSymbol(), from, to, Interval.DAILY);
            } else if (PrefUtils.is2Year(context)) {
                from.add(Calendar.YEAR, -2);
                return YahooFinance.get(quote.getSymbol(), from, to, Interval.DAILY);
            } else if (PrefUtils.is5Year(context)) {
                from.add(Calendar.YEAR, -5);
                return YahooFinance.get(quote.getSymbol(), from, to, Interval.DAILY);
            }

            return YahooFinance.get(quote.getSymbol(), from, to, Interval.DAILY);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
