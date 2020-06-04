package com.example.tumtracker;

import android.provider.BaseColumns;

public final class FeedReaderContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private FeedReaderContract() {}

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "Meals";
        public static final String COLUMN_NAME_TITLE = "MealTitle";
        public static final String COLUMN_FOODS = "Foods";
        public static final String COLUMN_PAIN = "Pain";
    }
}
