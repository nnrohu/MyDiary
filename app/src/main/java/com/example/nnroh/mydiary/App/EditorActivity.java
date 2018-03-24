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

import com.example.nnroh.mydiary.R;
import com.example.nnroh.mydiary.Contract.LoanContract.MoneyEntry;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class  EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_MONEY_LOADER = 0;

    private Uri mCurrentMoneyUri;

    private TextInputLayout NameLayout, MoneyLayout, DateLayout;

    private EditText Name, Money, Purpose, Date;

    private Spinner PaymentMode;

    private Calendar myCalendar = Calendar.getInstance();

    private int mMode = MoneyEntry.MODE_CASH;

    private boolean mDataHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mDataHasChanged = true;
            return false;
        }
    };

    @Override
    public void onBackPressed() {
        if (!mDataHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        if (intent != null) {
            mCurrentMoneyUri = intent.getData();

            if (mCurrentMoneyUri == null){
                setTitle(getString(R.string.add_new));
            }
            else {
                setTitle(getString(R.string.editor_update_activity_title));
                getSupportLoaderManager().initLoader(EXISTING_MONEY_LOADER, null, this);
            }
        }

        NameLayout = (TextInputLayout) findViewById(R.id.Loan_name_layout);
        MoneyLayout = (TextInputLayout) findViewById(R.id.Loan_money_layout);
        DateLayout = (TextInputLayout) findViewById(R.id.Loan_date_layout);

        Name = (EditText) findViewById(R.id.Loan_name);
        Money = (EditText) findViewById(R.id.Loan_money);
        Purpose = (EditText) findViewById(R.id.Loan_purpose);
        Date = (EditText) findViewById(R.id.Loan_date);
        PaymentMode = (Spinner) findViewById(R.id.Loan_payment_mode);


        Name.setOnTouchListener(mTouchListener);
        Money.setOnTouchListener(mTouchListener);
        Purpose.setOnTouchListener(mTouchListener);
        Date.setOnTouchListener(mTouchListener);
        PaymentMode.setOnTouchListener(mTouchListener);

        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.DATE, dayOfMonth);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.YEAR, year);
                updateLabel();
            }
        };

        Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(EditorActivity.this, dateSetListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        setUpSpinner();
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
                        mMode = MoneyEntry.MODE_TEZ;
                    } else if (selection.equals(getString(R.string.mode_online))) {
                        mMode = MoneyEntry.MODE_ONLINE;
                    } else if (selection.equals(getString(R.string.mode_paytm))) {
                        mMode = MoneyEntry.MODE_PAYTM;
                    } else if (selection.equals(getString(R.string.mode_Phonepe))) {
                        mMode = MoneyEntry.MODE_PHONEPE;
                    } else {
                        mMode = MoneyEntry.MODE_CASH;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mMode = MoneyEntry.MODE_CASH;
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
        if (mCurrentMoneyUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save:
                if (validateName() && validateMoney() && validateDate()){
                    insertData();
                    finish();
                }
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mDataHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
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
        String nameString = Name.getText().toString().trim();
        String moneyString = Money.getText().toString().trim();
        String purposeString = Purpose.getText().toString().trim();
        String dateString = Date.getText().toString().trim();
        int moneyInt = Integer.parseInt(moneyString);

        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(MoneyEntry.COLUMN_DATE, dateString);
        values.put(MoneyEntry.COLUMN_NAME, nameString);
        values.put(MoneyEntry.COLUMN_MONEY, moneyInt);
        values.put(MoneyEntry.COLUMN_PURPOSE, purposeString);
        values.put(MoneyEntry.COLUMN_PAYMENY_MODE, mMode);

        // Insert a new pet into the provider, returning the content URI for the new pet.
        if (mCurrentMoneyUri == null) {
            Uri newUri = getContentResolver().insert(MoneyEntry.CONTENT_URI, values);

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
            int rowsAffected = getContentResolver().update(mCurrentMoneyUri, values, null, null);
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
        if (mCurrentMoneyUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentMoneyUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_data_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_data_successful), Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }


    private boolean validateName() {
        if (Name.getText().toString().isEmpty()) {
            NameLayout.setError("This Field is required");
            return false;
        } else {
            NameLayout.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateMoney() {
        if (Money.getText().toString().isEmpty()) {
            MoneyLayout.setError("This Field is required");
            return false;
        } else {
            MoneyLayout.setErrorEnabled(false);
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                MoneyEntry._ID,
                MoneyEntry.COLUMN_DATE,
                MoneyEntry.COLUMN_NAME,
                MoneyEntry.COLUMN_MONEY,
                MoneyEntry.COLUMN_PURPOSE,
                MoneyEntry.COLUMN_PAYMENY_MODE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentMoneyUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data.moveToFirst()){
            // Find the columns of Money attributes that we're interested in
            int nameColumnIndex = data.getColumnIndex(MoneyEntry.COLUMN_NAME);
            int moneyColumnIndex = data.getColumnIndex(MoneyEntry.COLUMN_MONEY);
            int purposeColumnIndex = data.getColumnIndex(MoneyEntry.COLUMN_PURPOSE);
            int dateColumnIndex = data.getColumnIndex(MoneyEntry.COLUMN_DATE);
            int modeColumnIndex = data.getColumnIndex(MoneyEntry.COLUMN_PAYMENY_MODE);

            // Extract out the value from the Cursor for the given column index
            String nameText = data.getString(nameColumnIndex);
            int moneyText = data.getInt(moneyColumnIndex);
            String purposeText = data.getString(purposeColumnIndex);
            String dateText = data.getString(dateColumnIndex);
            int modeText = data.getInt(modeColumnIndex);

            // Update the views on the screen with the values from the database
            Name.setText(nameText);
            Money.setText(Integer.toString(moneyText));
            Date.setText(dateText);
            Purpose.setText(purposeText);

            /** /**
             * Possible values for the payment mode of the transaction.
            public static final int MODE_CASH = 0;
            public static final int MODE_ONLINE = 1;
            public static final int MODE_PAYTM = 2;
            public static final int MODE_TEZ = 3;
            public static final int MODE_PHONEPE = 4;

             */
            switch (modeText){
                case MoneyEntry.MODE_ONLINE:
                    PaymentMode.setSelection(1);
                    break;
                case MoneyEntry.MODE_PAYTM:
                    PaymentMode.setSelection(2);
                    break;
                case MoneyEntry.MODE_TEZ:
                    PaymentMode.setSelection(3);
                    break;
                case MoneyEntry.MODE_PHONEPE:
                    PaymentMode.setSelection(4);
                    break;
                default:
                    PaymentMode.setSelection(0);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Name.setText("");
        Money.setText("");
        Purpose.setText("");
        Date.setText("");
        PaymentMode.setSelection(0);
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
}
