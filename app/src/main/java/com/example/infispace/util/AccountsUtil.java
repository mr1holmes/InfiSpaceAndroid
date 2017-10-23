package com.example.infispace.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.infispace.R;
import com.facebook.AccessToken;

import java.util.Arrays;
import java.util.HashSet;

public class AccountsUtil {

    public static final String FACEBOOK_PERMISSIONS[] = {"email", "user_friends"};

    public static boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return (accessToken != null &&
                accessToken.getPermissions().containsAll(new HashSet<>(Arrays.asList(FACEBOOK_PERMISSIONS))));
    }

    public static String getUserId(Context mContext) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        String user_id = sharedPref.getString(mContext.getString(R.string.my_id), "0");
        return user_id;
    }

    public static String unescapeString(String string) {
        if (string == null)
            return null;

        StringBuilder sb = new StringBuilder(string);

        sb = sb.deleteCharAt(0);
        sb = sb.deleteCharAt(sb.length() - 1);

        return sb.toString().replaceAll("''", "'");
    }
}
