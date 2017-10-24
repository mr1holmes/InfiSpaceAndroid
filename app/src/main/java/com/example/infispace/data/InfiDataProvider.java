package com.example.infispace.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;


public class InfiDataProvider extends ContentProvider {
    private DbHelper mDbHelper;
    private static final UriMatcher mUriMatcher = buildUriMatcher();

    // codes for type of service
    public static final int USER = 200;
    public static final int USER_WITH_ID = 201;


    private static final SQLiteQueryBuilder sUserQueryBuilder;

    static {
        sUserQueryBuilder = new SQLiteQueryBuilder();
        sUserQueryBuilder.setTables(InfiContract.TABLE_USER.TABLE_NAME);
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        mUriMatcher.addURI(InfiContract.CONTENT_AUTHORITY, InfiContract.PATH_USER, USER);
        mUriMatcher.addURI(InfiContract.CONTENT_AUTHORITY, InfiContract.PATH_USER + "/#", USER_WITH_ID);

        return mUriMatcher;
    }

    // Query for selecting user
    private static final String sUserSelection = InfiContract.TABLE_USER.TABLE_NAME +
            "." + InfiContract.TABLE_USER.COLUMN_USER_ID + " = ? ";


    @Override
    public boolean onCreate() {
        mDbHelper = new DbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = mUriMatcher.match(uri);

        switch (match) {
            case USER:
                return InfiContract.TABLE_USER.CONTENT_TYPE;
            case USER_WITH_ID:
                return InfiContract.TABLE_USER.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;

        switch (mUriMatcher.match(uri)) {
            case USER: {
                retCursor = getUsers(uri, projection, sortOrder);
                break;
            }
            case USER_WITH_ID: {
                retCursor = getSingleUser(uri, projection, selection, selectionArgs, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        switch (mUriMatcher.match(uri)) {
            case USER: {
                long _id = db.insert(InfiContract.TABLE_USER.TABLE_NAME, null, values);

                if (_id == -1)
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return InfiContract.TABLE_USER.CONTENT_URI;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted = 0;
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        switch (mUriMatcher.match(uri)) {
            case USER: {
                rowsDeleted = db.delete(InfiContract.TABLE_USER.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case USER_WITH_ID: {
                String user_id = InfiContract.TABLE_USER.getUserIdFromUserUri(uri);
                selection = sUserSelection;
                selectionArgs = new String[]{user_id};
                rowsDeleted = db.delete(InfiContract.TABLE_USER.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        db.close();
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int rowsUpdated = 0;
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        switch (mUriMatcher.match(uri)) {
            case USER: {
                rowsUpdated = db.update(InfiContract.TABLE_USER.TABLE_NAME, values, selection, selectionArgs);
                if (rowsUpdated == -1) {
                    throw new android.database.SQLException("Failed to update row into " + uri);
                }
                break;
            }
            case USER_WITH_ID: {
                String user_id = InfiContract.TABLE_USER.getUserIdFromUserUri(uri);
                selection = sUserSelection;
                selectionArgs = new String[]{user_id};
                rowsUpdated = db.update(InfiContract.TABLE_USER.TABLE_NAME, values, selection, selectionArgs);
                if (rowsUpdated == -1) {
                    throw new android.database.SQLException("Failed to update row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        switch (mUriMatcher.match(uri)) {
            case USER: {
                db.beginTransaction();
                int retCount = 0;
                try {
                    for (ContentValues value : values) {
                        long resId = db.insert(InfiContract.TABLE_USER.TABLE_NAME, null, value);
                        if (resId != -1) retCount++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return retCount;
            }
            default:
                return super.bulkInsert(uri, values);
        }

    }

    private Cursor getUsers(Uri uri, String[] projection, String sortOrder) {
        return sUserQueryBuilder.query(mDbHelper.getReadableDatabase(),
                projection,
                null,
                null,
                null,
                null,
                sortOrder);
    }

    private Cursor getSingleUser(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String user_id = InfiContract.TABLE_USER.getUserIdFromUserUri(uri);
        selection = sUserSelection;
        selectionArgs = new String[]{user_id};
        return sUserQueryBuilder.query(mDbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }


    @Override
    @TargetApi(11)
    public void shutdown() {
        mDbHelper.close();
        super.shutdown();
    }
}
