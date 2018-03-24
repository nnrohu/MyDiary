/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.nnroh.mydiary.DatabaseHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.nnroh.mydiary.Contract.LoanContract.MoneyEntry;
import com.example.nnroh.mydiary.Contract.MonthlyContract.MonthlyEntry;

/**
 * Database helper for Money app. Manages database creation and version management.
 */
public class DbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = DbHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DATABASE_NAME = "loan.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link DbHelper}.
     *
     * @param context of the app
     */
    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the money table
        String SQL_CREATE_MONEY_TABLE =  "CREATE TABLE " + MoneyEntry.TABLE_NAME + " ("
                + MoneyEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MoneyEntry.COLUMN_DATE + " TEXT NOT NULL, "
                + MoneyEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + MoneyEntry.COLUMN_MONEY + " INTEGER NOT NULL, "
                + MoneyEntry.COLUMN_PURPOSE + " TEXT, "
                + MoneyEntry.COLUMN_PAYMENY_MODE + " INTEGER NOT NULL DEFAULT 0);";
        // Execute the SQL statement
        db.execSQL(SQL_CREATE_MONEY_TABLE);

        // Create a String that contains the SQL statement to create the money table
        String SQL_CREATE_MONTHLY_TABLE =  "CREATE TABLE " + MonthlyEntry.TABLE_NAME + " ("
                + MonthlyEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MonthlyEntry.COLUMN_DATE + " TEXT NOT NULL, "
                + MonthlyEntry.COLUMN_ITEM + " TEXT NOT NULL, "
                + MonthlyEntry.COLUMN_PRICE + " INTEGER NOT NULL, "
                + MonthlyEntry.COLUMN_PAYMENY_MODE + " INTEGER NOT NULL DEFAULT 0);";
        // Execute the SQL statement
        db.execSQL(SQL_CREATE_MONTHLY_TABLE);

    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
        Log.w(DbHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + MoneyEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MonthlyEntry.TABLE_NAME);

        onCreate(db);
    }
}