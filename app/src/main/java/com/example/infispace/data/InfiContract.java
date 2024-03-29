package com.example.infispace.data;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class InfiContract {
    public static final String CONTENT_AUTHORITY = "com.example.infispace";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_USER = "user";

    public static final class TABLE_USER implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_USER).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USER;

        public static final String TABLE_NAME = "user";

        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_FIRST_NAME = "first_name";
        public static final String COLUMN_LAST_NAME = "last_name";
        public static final String COLUMN_PROFILE_URL = "profile_url";

        public static Uri buildUserUri(long user_id) {
            return ContentUris.withAppendedId(CONTENT_URI, user_id);
        }

        public static String getUserIdFromUserUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }
}
