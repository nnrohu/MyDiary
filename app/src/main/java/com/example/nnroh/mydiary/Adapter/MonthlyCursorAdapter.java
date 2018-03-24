package com.example.nnroh.mydiary.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nnroh.mydiary.Contract.MonthlyContract.MonthlyEntry;
import com.example.nnroh.mydiary.R;

/**
 * Created by nnroh on 23-02-2018.
 */

public class MonthlyCursorAdapter extends CursorAdapter {

    public MonthlyCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_monthly_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView price = (TextView) view.findViewById(R.id.Monthly_price_view);
        TextView item = (TextView) view.findViewById(R.id.Monthly_item_name_view);
        TextView date = (TextView) view.findViewById(R.id.Monthly_date_view);
        TextView mode = (TextView) view.findViewById(R.id.Monthly_payment_mode);
        
        //find these column in database
        int priceColumnIndex = cursor.getColumnIndex(MonthlyEntry.COLUMN_PRICE);
        int itemNameColumnIndex = cursor.getColumnIndex(MonthlyEntry.COLUMN_ITEM);
        int dateColumnIndex = cursor.getColumnIndex(MonthlyEntry.COLUMN_DATE);
        int modeColumnIndex = cursor.getColumnIndex(MonthlyEntry.COLUMN_PAYMENY_MODE);

        //get the value at index
        int priceTaken = cursor.getInt(priceColumnIndex);
        String itemName = cursor.getString(itemNameColumnIndex);
        String dateAtWhich = cursor.getString(dateColumnIndex);
        int modeByWhich = cursor.getInt(modeColumnIndex);

        //set the data to UI
        price.setText("" + priceTaken);
        item.setText(itemName);
        date.setText(dateAtWhich);

        switch (modeByWhich) {
            case MonthlyEntry.MODE_ONLINE:
                mode.setText("ONLINE");
                break;
            case MonthlyEntry.MODE_PAYTM:
                mode.setText("PAYTM");
                break;
            case MonthlyEntry.MODE_TEZ:
                mode.setText("TEZ");
                break;
            case MonthlyEntry.MODE_PHONEPE:
                mode.setText("PHONEPE");
                break;
            default:
                mode.setText("CASH");
                break;
        }


    }
}
