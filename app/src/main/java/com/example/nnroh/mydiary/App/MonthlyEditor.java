package com.example.nnroh.mydiary.App;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.nnroh.mydiary.Contract.MonthlyContract.MonthlyEntry;
import com.example.nnroh.mydiary.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MonthlyEditor extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int EXISTING_MONTHLY_LOADER = 0;

    private Uri mCurrentMonthlyUri;

    private int mMode = MonthlyEntry.MODE_CASH;

    private Calendar myCalendar = Calendar.getInstance();

    private TextInputLayout ItemLayout, PriceLayout, DateLayout;
    private EditText ItemName, Price, Date;
    private Spinner PaymentMode;

    private boolean mDataHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mDataHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_editor);


        Intent intent = getIntent();
        if (intent != null) {
            mCurrentMonthlyUri = intent.getData();

            if (mCurrentMonthlyUri == null){
                setTitle(getString(R.string.add_new));
            }
            else {
                setTitle(getString(R.string.editor_update_activity_title));
                getSupportLoaderManager().initLoader(EXISTING_MONTHLY_LOADER, null, this);
            }
        }

        ItemLayout = (TextInputLayout) findViewById(R.id.Monthly_item_name_layout);
        PriceLayout = (TextInputLayout) findViewById(R.id.Monthly_price_layout);
        DateLayout = (TextInputLayout) findViewById(R.id.Monthly_date_layout);

        ItemName = (EditText) findViewById(R.id.Monthly_item_name);
        Price = (EditText) findViewById(R.id.Monthly_price);
        Date = (EditText) findViewById(R.id.Monthly_date);
        PaymentMode = (Spinner) findViewById(R.id.Monthly_payment_mode);

        ItemName.setOnTouchListener(mTouchListener);
        Price.setOnTouchListener(mTouchListener);
        Date.setOnTouchListener(mTouchListener);
        PaymentMode.setOnTouchListener(mTouchListener);
        setUpSpinner();


        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.DATE, dayOfMonth);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.YEAR, year);
                updateLabel();
            }
        } ;

        Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(MonthlyEditor.this, dateSetListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateLabel() {
        String dateFormat = "dd MMM,yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.US);
        Date.setText(simpleDateFormat.format(myCalendar.getTime()));
    }

    private void setUpSpinner() {
        ArrayAdapter paymentModeSpinner = ArrayAdapter.createFromResource(this,
                R.array.array_payment_mode_options, android.R.layout.simple_spinner_item);
        paymentModeSpinner.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        PaymentMode.setAdapter(paymentModeSpinner);

        PaymentMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.mode_tez))) {
                        mMode = MonthlyEntry.MODE_TEZ;
                    } else if (selection.equals(getString(R.string.mode_online))) {
                        mMode = MonthlyEntry.MODE_ONLINE;
                    } else if (selection.equals(getString(R.string.mode_paytm))) {
                        mMode = MonthlyEntry.MODE_PAYTM;
                    } else if (selection.equals(getString(R.string.mode_Phonepe))) {
                        mMode = MonthlyEntry.MODE_PHONEPE;
                    } else {
                        mMode = MonthlyEntry.MODE_CASH;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mMode = MonthlyEntry.MODE_CASH;
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentMonthlyUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save:
                if (validateItem() && validatePrice() && validateDate()){
                    insertData();
                    finish();
                }
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mDataHasChanged) {
                    NavUtils.navigateUpFromSameTask(MonthlyEditor.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(MonthlyEditor.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }

        return onOptionsItemSelected(item);
    }


    private void insertData() {
        //read input from fields
        //trim for remove white space
        String itemNameString = ItemName.getText().toString().trim();
        String priceString = Price.getText().toString().trim();
        String dateString = Date.getText().toString().trim();
        int priceInt = Integer.parseInt(priceString);

        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(MonthlyEntry.COLUMN_DATE, dateString);
        values.put(MonthlyEntry.COLUMN_ITEM, itemNameString);
        values.put(MonthlyEntry.COLUMN_PRICE, priceInt);
        values.put(MonthlyEntry.COLUMN_PAYMENY_MODE, mMode);

        // Insert a new pet into the provider, returning the content URI for the new pet.
        if (mCurrentMonthlyUri == null) {
            Uri newUri = getContentResolver().insert(MonthlyEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_data_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_data_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        else{
            int rowsAffected = getContentResolver().update(mCurrentMonthlyUri, values, null, null);
            if (rowsAffected == 0){
                Toast.makeText(this, getString(R.string.editor_update_data_failed), Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, getString(R.string.editor_update_data_successful), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /* Perform the deletion of the pet in the database.
    */
    private void deleteData() {
        if (mCurrentMonthlyUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentMonthlyUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_data_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_data_successful), Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }


    private boolean validateItem() {
        if (ItemName.getText().toString().isEmpty()) {
            ItemLayout.setError("This Field is required");
            return false;
        } else {
            ItemLayout.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePrice() {
        if (Price.getText().toString().isEmpty()) {
            PriceLayout.setError("This Field is required");
            return false;
        } else {
            PriceLayout.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateDate() {
        if (Date.getText().toString().isEmpty()) {
            DateLayout.setError("This Field is required");
            return false;
        } else {
            DateLayout.setErrorEnabled(false);
            return true;
        }
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteData();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                MonthlyEntry._ID,
                MonthlyEntry.COLUMN_DATE,
                MonthlyEntry.COLUMN_ITEM,
                MonthlyEntry.COLUMN_PRICE,
                MonthlyEntry.COLUMN_PAYMENY_MODE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentMonthlyUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()){
            // Find the columns of Money attributes that we're interested in
            int dateColumnIndex = data.getColumnIndex(MonthlyEntry.COLUMN_DATE);
            int itemNameColumnIndex = data.getColumnIndex(MonthlyEntry.COLUMN_ITEM);
            int priceColumnIndex = data.getColumnIndex(MonthlyEntry.COLUMN_PRICE);
            int modeColumnIndex = data.getColumnIndex(MonthlyEntry.COLUMN_PAYMENY_MODE);

            // Extract out the value from the Cursor for the given column index
            String itemNameText = data.getString(itemNameColumnIndex);
            int priceText = data.getInt(priceColumnIndex);
            String dateText = data.getString(dateColumnIndex);
            int modeText = data.getInt(modeColumnIndex);

            // Update the views on the screen with the values from the database
            ItemName.setText(itemNameText);
            Price.setText(Integer.toString(priceText));
            Date.setText(dateText);

            /** /**
             * Possible values for the payment mode of the transaction.
             public static final int MODE_CASH = 0;
             public static final int MODE_ONLINE = 1;
             public static final int MODE_PAYTM = 2;
             public static final int MODE_TEZ = 3;
             public static final int MODE_PHONEPE = 4;

             */
            switch (modeText){
                case MonthlyEntry.MODE_ONLINE:
                    PaymentMode.setSelection(1);
                    break;
                case MonthlyEntry.MODE_PAYTM:
                    PaymentMode.setSelection(2);
                    break;
                case MonthlyEntry.MODE_TEZ:
                    PaymentMode.setSelection(3);
                    break;
                case MonthlyEntry.MODE_PHONEPE:
                    PaymentMode.setSelection(4);
                    break;
                default:
                    PaymentMode.setSelection(0);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ItemName.setText("");
        Price.setText("");
        Date.setText("");
        PaymentMode.setSelection(0);
    }
}
