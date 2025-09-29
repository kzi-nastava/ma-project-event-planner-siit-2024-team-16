package com.example.evenmate.auth;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.evenmate.models.user.User;

public class AuthManager {
    private static final String PREFS_NAME = "AUTH_PREFS";
    private static final String TOKEN_KEY = "jwt_token";
    private static AuthManager instance;
    private final SharedPreferences prefs;
    public static User loggedInUser;

    private AuthManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized AuthManager getInstance(Context context) {
        if (instance == null) {
            instance = new AuthManager(context);
        }
        return instance;
    }

    public void saveToken(String token) {
        prefs.edit().putString(TOKEN_KEY, token).apply();
    }

    public String getToken() {
        return prefs.getString(TOKEN_KEY, null);
    }

    public void logout() {
        loggedInUser = null;
        prefs.edit().remove(TOKEN_KEY).apply();
    }

    public boolean isLoggedIn() {
        return loggedInUser != null && getToken() != null;
    }
}

