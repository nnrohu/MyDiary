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
package com.example.nnroh.mydiary.Contract;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


/**
 * API Contract for the Money app.
 */
public final class LoanContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private LoanContract() {}

    // Authority of database
    public static final String CONTENT_AUTHORITY = "com.example.nnroh.loanprovider";

    //Base contents Uri
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //This constants stores the path for each of the tables which will be appended to the base content URI.
    public static final String PATH_MONEY = "money";


    public static final class MoneyEntry implements BaseColumns {

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of data.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MONEY;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single data.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MONEY;


        /** The content URI to access the money data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MONEY);

        /** Name of database table for Money*/
        public final static String TABLE_NAME = "money";

        /**
         * Unique ID number for the customer (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the customer.
         *
         * Type: TEXT
         */
        public final static String COLUMN_NAME ="name";

        /**
         * Money taken by the customer.
         *
         * Type: INTEGER
         */

        public final static String COLUMN_MONEY = "money";

        /**
         * Purpose of money.
         *
         * Type: INTEGER
         */

        public final static String COLUMN_PURPOSE = "purpose";

        /**
         * mode of payment.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_DATE = "date";

        /**
         * mode of payment.
         *
         * Type: INTEGER
         */

        public final static String COLUMN_PAYMENY_MODE = "mode";

        /**
         * Possible values for the payment mode of the transaction.
         */
        public static final int MODE_CASH = 0;
        public static final int MODE_ONLINE = 1;
        public static final int MODE_PAYTM = 2;
        public static final int MODE_TEZ = 3;
        public static final int MODE_PHONEPE = 4;
    }

}

