package com.example.infispace.util;

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
}
