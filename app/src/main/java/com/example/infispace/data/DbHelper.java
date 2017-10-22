package com.example.infispace.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Helps managing database
 */
public class DbHelper extends SQLiteOpenHelper {

    // change database version when schema is changed
    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "planup.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_USER_TABLE = "CREATE TABLE " + InfiContract.TABLE_USER.TABLE_NAME + " (" +
                InfiContract.TABLE_USER.COLUMN_USER_ID + " INTEGER PRIMARY KEY," +
                InfiContract.TABLE_USER.COLUMN_FIRST_NAME + " TEXT NOT NULL," +
                InfiContract.TABLE_USER.COLUMN_LAST_NAME + " TEXT NOT NULL," +
                InfiContract.TABLE_USER.COLUMN_PROFILE_URL + " TEXT NOT NULL);";

        db.execSQL(SQL_CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + InfiContract.TABLE_USER.TABLE_NAME);
        onCreate(db);
    }
}
