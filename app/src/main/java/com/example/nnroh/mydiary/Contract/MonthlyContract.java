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
 * API Contract for the Data app.
 */
public final class MonthlyContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private MonthlyContract() {}

    // Authority of database
    public static final String CONTENT_AUTHORITY = "com.example.nnroh.monthlyprovider";

    //Base contents Uri
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //This constants stores the path for each of the tables which will be appended to the base content URI.
    public static final String PATH_MONTHLY = "monthly";



    public static final class MonthlyEntry implements BaseColumns {

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of monthly.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MONTHLY;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single data.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MONTHLY;


        /** The content URI to access the monthly data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MONTHLY);

        /** Name of database table for monthly */
        public final static String TABLE_NAME = "monthly";

        /** Column of table for monthly */
        public final static String _ID = BaseColumns._ID;


        public final static String COLUMN_ITEM ="name";


        public final static String COLUMN_PRICE = "price";


        public final static String COLUMN_DATE = "date";


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

