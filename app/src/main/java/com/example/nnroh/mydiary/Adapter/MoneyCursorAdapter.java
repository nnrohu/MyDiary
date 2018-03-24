package com.example.nnroh.mydiary.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nnroh.mydiary.R;
import com.example.nnroh.mydiary.Contract.LoanContract.MoneyEntry;

/**
 * Created by nnroh on 23-02-2018.
 */

public class MoneyCursorAdapter extends CursorAdapter {

    public MoneyCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_loan_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView amount = (TextView) view.findViewById(R.id.Loan_money_view);
        TextView name = (TextView) view.findViewById(R.id.Loan_name_view);
        TextView purpose = (TextView) view.findViewById(R.id.Loan_purpose_view);
        TextView date = (TextView) view.findViewById(R.id.Loan_date_view);
        TextView mode = (TextView) view.findViewById(R.id.Loan_payment_mode_view);

        //find these column in database
        int purposeColumnIndex = cursor.getColumnIndex(MoneyEntry.COLUMN_PURPOSE);
        int amountColumnIndex = cursor.getColumnIndex(MoneyEntry.COLUMN_MONEY);
        int nameColumnIndex = cursor.getColumnIndex(MoneyEntry.COLUMN_NAME);
        int dateColumnIndex = cursor.getColumnIndex(MoneyEntry.COLUMN_DATE);
        int modeColumnIndex = cursor.getColumnIndex(MoneyEntry.COLUMN_PAYMENY_MODE);

        //get the value at index
        int amountTaken = cursor.getInt(amountColumnIndex);
        String personName = cursor.getString(nameColumnIndex);
        String dateAtWhich = cursor.getString(dateColumnIndex);
        String purposeForWhich = cursor.getString(purposeColumnIndex);
        int modeByWhich = cursor.getInt(modeColumnIndex);

        //set the data to UI
        amount.setText("" + amountTaken);
        name.setText(personName);
        purpose.setText(purposeForWhich);
        date.setText(dateAtWhich);

        switch (modeByWhich) {
            case MoneyEntry.MODE_ONLINE:
                mode.setText("ONLINE");
                break;
            case MoneyEntry.MODE_PAYTM:
                mode.setText("PAYTM");
                break;
            case MoneyEntry.MODE_TEZ:
                mode.setText("TEZ");
                break;
            case MoneyEntry.MODE_PHONEPE:
                mode.setText("PHONEPE");
                break;
            default:
                mode.setText("CASH");
                break;
        }


    }
}
