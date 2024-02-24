package ro.spykids.clientapp.util;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {
    private static final String TOKEN_PREFS = "token_prefs";
    private static final String TOKEN_KEY = "token_key";
    private SharedPreferences mPrefs;
    private static TokenManager mInstance;

    public TokenManager(Context context) {
        // Initialize SharedPreferences with a unique name
        mPrefs = context.getSharedPreferences(TOKEN_PREFS, Context.MODE_PRIVATE);
    }

    public static synchronized TokenManager getInstance(Context context) {
        if (mInstance == null) {
            // Create a new instance if it doesn't exist
            mInstance = new TokenManager(context.getApplicationContext());
        }
        return mInstance;
    }

    public void saveToken(String token) {
        // Save the token in SharedPreferences using the specified key
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(TOKEN_KEY, token);
        editor.apply();
    }

    public String getToken() {
        // Retrieve the token from SharedPreferences using the specified key
        return mPrefs.getString(TOKEN_KEY, null);
    }

    public void clearToken() {
        // Remove the token from SharedPreferences
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.remove(TOKEN_KEY);
        editor.apply();
    }
}

