package com.udacity.stockhawk.ui;


import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.StockViewHolder> {

    private final Context context;
    private final DecimalFormat dollarFormatWithPlus;
    private final DecimalFormat dollarFormat;
    private final DecimalFormat percentageFormat;
    private Cursor cursor;
    private final StockAdapterOnClickHandler clickHandler;
    private int selectedPos = 0;

    StockAdapter(Context context, StockAdapterOnClickHandler clickHandler) {
        this.context = context;
        this.clickHandler = clickHandler;

        dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus.setPositivePrefix("+$");
        percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setPositivePrefix("+");
    }

    void setCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    String getSymbolAtPosition(int position) {

        cursor.moveToPosition(position);
        return cursor.getString(Contract.StockQuoteEntry.POSITION_SYMBOL);
    }

    @Override
    public StockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View item = LayoutInflater.from(context).inflate(R.layout.list_item_quote, parent, false);

        return new StockViewHolder(item);
    }

    @Override
    public void onBindViewHolder(StockViewHolder holder, int position) {
        holder.itemView.setSelected(selectedPos == position);
        cursor.moveToPosition(position);

        if(cursor.getDouble(Contract.StockQuoteEntry.POSITION_PRICE) ==- 1 && cursor.getDouble(Contract.StockQuoteEntry.POSITION_PERCENTAGE_CHANGE) == -1){
            holder.name.setText(context.getString(R.string.non_existent_value));
            holder.symbol.setText(cursor.getString(Contract.StockQuoteEntry.POSITION_SYMBOL));
            holder.price.setText("");
            holder.change.setText("");
        }else {
            holder.name.setText(cursor.getString(Contract.StockQuoteEntry.POSITION_NAME));
            holder.symbol.setText(cursor.getString(Contract.StockQuoteEntry.POSITION_SYMBOL));
            holder.price.setText(dollarFormat.format(cursor.getFloat(Contract.StockQuoteEntry.POSITION_PRICE)));


            float rawAbsoluteChange = cursor.getFloat(Contract.StockQuoteEntry.POSITION_ABSOLUTE_CHANGE);
            float percentageChange = cursor.getFloat(Contract.StockQuoteEntry.POSITION_PERCENTAGE_CHANGE);

            if (rawAbsoluteChange > 0) {
                holder.change.setBackgroundResource(R.drawable.percent_change_pill_green);
            } else {
                holder.change.setBackgroundResource(R.drawable.percent_change_pill_red);
            }

            String change = dollarFormatWithPlus.format(rawAbsoluteChange);
            String percentage = percentageFormat.format(percentageChange / 100);

            if (PrefUtils.getDisplayMode(context)
                    .equals(context.getString(R.string.pref_display_mode_absolute_key))) {
                holder.change.setText(change);
            } else {
                holder.change.setText(percentage);
            }
        }


    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (cursor != null) {
            count = cursor.getCount();
        }
        return count;
    }

     interface StockAdapterOnClickHandler {
        void onClick(int position,String mSymbol,String Name);
    }



    class StockViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        @BindView(R.id.name)
        TextView name;

        @BindView(R.id.symbol)
        TextView symbol;

        @BindView(R.id.price)
        TextView price;

        @BindView(R.id.change)
        TextView change;

        StockViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            notifyItemChanged(selectedPos);
            selectedPos = getLayoutPosition();
            notifyItemChanged(selectedPos);
            int adapterPosition = getAdapterPosition();
            cursor.moveToPosition(adapterPosition);
            String mSymbol= cursor.getString(Contract.StockQuoteEntry.POSITION_SYMBOL);
            String name = cursor.getString(Contract.StockQuoteEntry.POSITION_NAME);

            clickHandler.onClick(selectedPos,mSymbol,name);

        }


    }

    public void selectView(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof StockViewHolder) {
            StockViewHolder svh = (StockViewHolder) viewHolder;
            svh.onClick(svh.itemView);
        }
    }

}
