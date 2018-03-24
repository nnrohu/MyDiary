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

import com.example.nnroh.mydiary.Contract.MonthlyContract.MonthlyEntry;
import com.example.nnroh.mydiary.Contract.MonthlyContract;
import com.example.nnroh.mydiary.DatabaseHelper.DbHelper;

import static com.example.nnroh.mydiary.Contract.MonthlyContract.CONTENT_AUTHORITY;

/**
 * Created by nnroh on 24-02-2018.
 */

public class MonthlyProvider extends ContentProvider {

    private DbHelper mDbHelper;

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = MonthlyProvider.class.getSimpleName();

    /**
     * URI matcher code for the content URI for the monthly table
     */
    private static final int MONTHLY = 200;

    /**
     * URI matcher code for the content URI for a single data in the monthly table
     */
    private static final int MONTHLY_ID = 201;

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
        sUriMatcher.addURI(CONTENT_AUTHORITY, MonthlyContract.PATH_MONTHLY, MONTHLY);
        sUriMatcher.addURI(CONTENT_AUTHORITY, MonthlyContract.PATH_MONTHLY + "/#", MONTHLY_ID);

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
            case MONTHLY:

                cursor = database.query(MonthlyEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case MONTHLY_ID:

                selection = MonthlyEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the monthly table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(MonthlyEntry.TABLE_NAME, projection, selection, selectionArgs,
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
            case MONTHLY:
                return MonthlyEntry.CONTENT_LIST_TYPE;
            case MONTHLY_ID:
                return MonthlyEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MONTHLY:
                return insertMonthly(uri, values);
            default:
                throw new IllegalArgumentException("insertion is not supported for " + uri);
        }
    }

    private Uri insertMonthly(Uri uri, ContentValues values) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new data with the given values
        long id = database.insert(MonthlyEntry.TABLE_NAME, null, values);
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
            case MONTHLY:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(MonthlyEntry.TABLE_NAME, selection, selectionArgs);
                // If 1 or more rows were deleted, then notify all listeners that the data at the
                // given URI has changed
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;

            case MONTHLY_ID:
                // Delete a single row given by the ID in the URI
                selection = MonthlyEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                rowsDeleted = database.delete(MonthlyEntry.TABLE_NAME, selection, selectionArgs);
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
        switch (match) {
            case MONTHLY:
                return updateMonthly(uri, values, selection, selectionArgs);
            case MONTHLY_ID:
                selection = MonthlyEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateMonthly(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateMonthly(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(MonthlyEntry.COLUMN_ITEM)) {
            String name = values.getAsString(MonthlyEntry.COLUMN_ITEM);
            if (name == null) {
                throw new IllegalArgumentException("Requires an item");
            }
        }
        if (values.containsKey(MonthlyEntry.COLUMN_PRICE)) {
            Integer money = values.getAsInteger(MonthlyEntry.COLUMN_PRICE);
            if (money == null) {
                throw new IllegalArgumentException("Requires some price");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(MonthlyEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
