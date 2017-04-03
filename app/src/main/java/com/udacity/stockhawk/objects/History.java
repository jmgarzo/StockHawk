package com.udacity.stockhawk.objects;

import android.content.ContentValues;
import android.database.Cursor;

import com.udacity.stockhawk.data.Contract;

import yahoofinance.histquotes.HistoricalQuote;

/**
 * Created by jmgarzo on 03/04/17.
 */

public class History {
    private int id;
    private int quoteId;
    private long date;
    private double open;
    private double high;
    private double low;
    private double close;
    private long volume;
    private double adjClose;


    public History(){};

    public History(HistoricalQuote it){

        setDate(it.getDate().getTimeInMillis());
        setOpen(it.getOpen().doubleValue());
        setHigh(it.getHigh().doubleValue());
        setLow(it.getLow().doubleValue());
        setClose(it.getClose().doubleValue());
        setVolume(it.getVolume());
        setAdjClose(it.getAdjClose().doubleValue());
    }

    public History(Cursor cursor, int position){
        if(cursor != null && cursor.moveToPosition(position)){
            cursorToHistory(cursor);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(int quoteId) {
        this.quoteId = quoteId;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public long getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }

    public double getAdjClose() {
        return adjClose;
    }

    public void setAdjClose(double adjClose) {
        this.adjClose = adjClose;
    }

    public ContentValues getContentValues(){

        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.HistoryEntry.QUOTE_KEY,quoteId);
        contentValues.put(Contract.HistoryEntry.DATE,date);
        contentValues.put(Contract.HistoryEntry.OPEN,open);
        contentValues.put(Contract.HistoryEntry.HIGH,high);
        contentValues.put(Contract.HistoryEntry.LOW,low);
        contentValues.put(Contract.HistoryEntry.CLOSE,close);
        contentValues.put(Contract.HistoryEntry.VOLUME,volume);
        contentValues.put(Contract.HistoryEntry.ADJ_CLOSE,adjClose);

        return contentValues;
    }

    private void cursorToHistory(Cursor cursor){

        id = cursor.getInt(Contract.HistoryEntry.POSITION_ID);
        quoteId = cursor.getInt(Contract.HistoryEntry.POSITION_QUOTE_KEY);
        date = cursor.getLong(Contract.HistoryEntry.POSITION_DATE);
        open = cursor.getDouble(Contract.HistoryEntry.POSITION_OPEN);
        high = cursor.getDouble(Contract.HistoryEntry.POSITION_HIGH);
        low = cursor.getDouble(Contract.HistoryEntry.POSITION_LOW);
        close = cursor.getDouble(Contract.HistoryEntry.POSITION_CLOSE);
        volume = cursor.getLong(Contract.HistoryEntry.POSITION_VOLUME);
        adjClose = cursor.getDouble(Contract.HistoryEntry.POSITION_ADJ_CLOSE);

    }
}
