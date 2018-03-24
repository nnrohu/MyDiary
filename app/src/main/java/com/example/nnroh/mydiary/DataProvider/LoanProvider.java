package com.example.nnroh.mydiary.DataProvider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.nnroh.mydiary.Contract.LoanContract;
import com.example.nnroh.mydiary.Contract.LoanContract.MoneyEntry;
import com.example.nnroh.mydiary.DatabaseHelper.DbHelper;

import static com.example.nnroh.mydiary.Contract.LoanContract.CONTENT_AUTHORITY;

/**
 * Created by nnroh on 23-02-2018.
 */

public class LoanProvider extends ContentProvider {

    private DbHelper mDbHelper;

    /** Tag for the log messages */
    public static final String LOG_TAG = LoanProvider.class.getSimpleName();

    /** URI matcher code for the content URI for the pets table */
    private static final int MONEY = 100;

    /** URI matcher code for the content URI for a single pet in the pets table */
    private static final int MONEY_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.
        sUriMatcher.addURI(CONTENT_AUTHORITY, LoanContract.PATH_MONEY, MONEY);
        sUriMatcher.addURI(CONTENT_AUTHORITY, LoanContract.PATH_MONEY + "/#", MONEY_ID);

        // TODO: Add 2 content URIs to URI matcher
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new DbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;
        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case MONEY:

                cursor = database.query(MoneyEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case MONEY_ID:

                selection = MoneyEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(MoneyEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        //set Notification uri on cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MONEY:
                return MoneyEntry.CONTENT_LIST_TYPE;
            case MONEY_ID:
                return MoneyEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case MONEY:
                return insertMoney(uri, values);
            default:
                throw new IllegalArgumentException("insertion is not supported for " + uri);
        }
    }

    private Uri insertMoney(Uri uri, ContentValues values){
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new pet with the given values
        long id = database.insert(MoneyEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        //notify all listener that data has been changed
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MONEY:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(MoneyEntry.TABLE_NAME, selection, selectionArgs);
                // If 1 or more rows were deleted, then notify all listeners that the data at the
                // given URI has changed
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;

            case MONEY_ID:
                // Delete a single row given by the ID in the URI
                selection = MoneyEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                rowsDeleted = database.delete(MoneyEntry.TABLE_NAME, selection, selectionArgs);
                // If 1 or more rows were deleted, then notify all listeners that the data at the
                // given URI has changed
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;

            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
       final int match = sUriMatcher.match(uri);
       switch (match){
           case MONEY:
               return updateMoney(uri, values, selection, selectionArgs);
           case MONEY_ID:
               selection = MoneyEntry._ID + "=?";
               selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
               return updateMoney(uri, values, selection, selectionArgs);
           default:
               throw new IllegalArgumentException("Update is not supported for " + uri);
       }
    }

    private int updateMoney(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(MoneyEntry.COLUMN_NAME)){
            String name = values.getAsString(MoneyEntry.COLUMN_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Requires a name");
            }
        }
        if (values.containsKey(MoneyEntry.COLUMN_MONEY)){
            Integer money = values.getAsInteger(MoneyEntry.COLUMN_MONEY);
            if (money == null) {
                throw new IllegalArgumentException("Requires some money");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(MoneyEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
