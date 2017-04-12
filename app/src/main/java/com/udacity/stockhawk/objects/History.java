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
    private int quoteKey;//foreign key
    private int quoteId;
    private long date;
    private double open;
    private double high;
    private double low;
    private double close;
    private long volume;
    private double adjClose;
    private String registryType;


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

    public int getQuoteKey() {
        return quoteKey;
    }

    public void setQuoteKey(int quoteKey) {
        this.quoteKey = quoteKey;
    }

    public String getRegistryType() {
        return registryType;
    }

    public void setRegistryType(String registryType) {
        this.registryType = registryType;
    }

    public ContentValues getContentValues(){

        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.HistoryEntry.COLUMN_QUOTE_KEY,quoteId);
        contentValues.put(Contract.HistoryEntry.COLUMN_DATE,date);
        contentValues.put(Contract.HistoryEntry.COLUMN_OPEN,open);
        contentValues.put(Contract.HistoryEntry.COLUMN_HIGH,high);
        contentValues.put(Contract.HistoryEntry.COLUMN_LOW,low);
        contentValues.put(Contract.HistoryEntry.COLUMN_CLOSE,close);
        contentValues.put(Contract.HistoryEntry.COLUMN_VOLUME,volume);
        contentValues.put(Contract.HistoryEntry.COLUMN_ADJ_CLOSE,adjClose);
        contentValues.put(Contract.HistoryEntry.COLUMN_REGISTRY_TYPE,registryType);

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
        registryType = cursor.getString(Contract.HistoryEntry.POSITION_REGISTRY_TYPE);

    }
}
