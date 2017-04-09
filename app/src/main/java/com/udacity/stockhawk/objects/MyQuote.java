package com.udacity.stockhawk.objects;

import android.content.ContentValues;
import android.database.Cursor;

import com.udacity.stockhawk.data.Contract;

import java.math.BigDecimal;

/**
 * Created by jmgarzo on 06/04/17.
 */

public class MyQuote {
    private int id;
    private int stockId;
    private BigDecimal ask;
    private Long askSiza;
    private Long avgVolume;
    private BigDecimal bid;
    private Long bidSize;
    private BigDecimal dayHigh;
    private BigDecimal dayLow;
    private String lastTradeDateStr;
    private Long lastTradeSize;
    private Long lastTradeTime;
    private String lastTradeTimeStr;
    private BigDecimal open;
    private BigDecimal previosClose;
    private BigDecimal price;
    private BigDecimal change;
    private BigDecimal percentageChange;
    private BigDecimal priceAvg200;
    private BigDecimal priceAvg50;
    private String symbol;
    //private ZoneInfo
    private Long volume;
    private BigDecimal yearHigh;
    private BigDecimal yearLow;


    public MyQuote() {
    }

    public MyQuote(Cursor cursor, int position) {
        if (cursor != null && cursor.moveToPosition(position)) {
            cursorToMyQuote(cursor);
        }
    }

    public BigDecimal getPercentageChange() {
        return percentageChange;
    }

    public void setPercentageChange(BigDecimal percentageChange) {
        this.percentageChange = percentageChange;
    }

    public BigDecimal getChange() {
        return change;
    }

    public void setChange(BigDecimal change) {
        this.change = change;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public BigDecimal getAsk() {
        return ask;
    }

    public void setAsk(BigDecimal ask) {
        this.ask = ask;
    }

    public Long getAskSiza() {
        return askSiza;
    }

    public void setAskSiza(Long askSiza) {
        this.askSiza = askSiza;
    }

    public Long getAvgVolume() {
        return avgVolume;
    }

    public void setAvgVolume(Long avgVolume) {
        this.avgVolume = avgVolume;
    }

    public BigDecimal getBid() {
        return bid;
    }

    public void setBid(BigDecimal bid) {
        this.bid = bid;
    }

    public Long getBidSize() {
        return bidSize;
    }

    public void setBidSize(Long bidSize) {
        this.bidSize = bidSize;
    }

    public BigDecimal getDayHigh() {
        return dayHigh;
    }

    public void setDayHigh(BigDecimal dayHigh) {
        this.dayHigh = dayHigh;
    }

    public BigDecimal getDayLow() {
        return dayLow;
    }

    public void setDayLow(BigDecimal dayLow) {
        this.dayLow = dayLow;
    }

    public String getLastTradeDateStr() {
        return lastTradeDateStr;
    }

    public void setLastTradeDateStr(String lastTradeDateStr) {
        this.lastTradeDateStr = lastTradeDateStr;
    }

    public Long getLastTradeSize() {
        return lastTradeSize;
    }

    public void setLastTradeSize(Long lastTradeSize) {
        this.lastTradeSize = lastTradeSize;
    }

    public Long getLastTradeTime() {
        return lastTradeTime;
    }

    public void setLastTradeTime(Long lastTradeTime) {
        this.lastTradeTime = lastTradeTime;
    }

    public String getLastTradeTimeStr() {
        return lastTradeTimeStr;
    }

    public void setLastTradeTimeStr(String lastTradeTimeStr) {
        this.lastTradeTimeStr = lastTradeTimeStr;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public void setOpen(BigDecimal open) {
        this.open = open;
    }

    public BigDecimal getPreviosClose() {
        return previosClose;
    }

    public void setPreviosClose(BigDecimal previosClose) {
        this.previosClose = previosClose;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPriceAvg200() {
        return priceAvg200;
    }

    public void setPriceAvg200(BigDecimal priceAvg200) {
        this.priceAvg200 = priceAvg200;
    }

    public BigDecimal getPriceAvg50() {
        return priceAvg50;
    }

    public void setPriceAvg50(BigDecimal priceAvg50) {
        this.priceAvg50 = priceAvg50;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }

    public BigDecimal getYearHigh() {
        return yearHigh;
    }

    public void setYearHigh(BigDecimal yearHigh) {
        this.yearHigh = yearHigh;
    }

    public BigDecimal getYearLow() {
        return yearLow;
    }

    public void setYearLow(BigDecimal yearLow) {
        this.yearLow = yearLow;
    }

    private void cursorToMyQuote(Cursor cursor) {
        id = cursor.getInt(Contract.QuoteEntry.POSITION_ID);
        stockId = cursor.getInt(Contract.QuoteEntry.POSITION_STOCK_KEY);
        symbol = cursor.getString(Contract.QuoteEntry.POSITION_SYMBOL);
        price = BigDecimal.valueOf(cursor.getDouble(Contract.QuoteEntry.POSITION_PRICE));
        change = BigDecimal.valueOf(cursor.getDouble(Contract.QuoteEntry.POSITION_ABSOLUTE_CHANGE));
        percentageChange = BigDecimal.valueOf(cursor.getDouble(Contract.QuoteEntry.POSITION_PERCENTAGE_CHANGE));
    }

    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();

        contentValues.put(Contract.QuoteEntry.COLUMN_STOCK_KEY, getStockId());
        contentValues.put(Contract.QuoteEntry.COLUMN_SYMBOL,getSymbol());
        contentValues.put(Contract.QuoteEntry.COLUMN_PRICE,getPrice().doubleValue());
        contentValues.put(Contract.QuoteEntry.COLUMN_ABSOLUTE_CHANGE,getChange().doubleValue());
        contentValues.put(Contract.QuoteEntry.COLUMN_PERCENTAGE_CHANGE,getPercentageChange().doubleValue());

        return  contentValues;
    }
}
