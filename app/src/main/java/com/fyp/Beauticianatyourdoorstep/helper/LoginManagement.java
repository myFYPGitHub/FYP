package com.fyp.Beauticianatyourdoorstep.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public final class LoginManagement {
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    private static final String SHARED_PREF_NAME = "loginMgt";
    private final String login_key, category_key;

    @SuppressLint("CommitPrefEdits")
    public LoginManagement(Context context) {
        login_key = "login_key";
        category_key = "category_key";
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setLoginEmail(String email_id) {
        editor.putString(login_key, email_id).commit();
    }

    public String getLoginEmail() {
        return sharedPreferences.getString(login_key, null);
    }

    public void setLoginCategory(String category) {
        editor.putString(category_key, category).commit();
    }

    public String getLoginCategory() {
        return sharedPreferences.getString(category_key, null);
    }

    public String getEmailIdentifier() {
        return StringHelper.removeInvalidCharsFromIdentifier(getLoginEmail());
    }

    public void removeLoginFromDevice() {
        editor.putString(login_key, null).commit();
        editor.putString(category_key, null).commit();
    }

    public boolean isLoginActive() {
        return getLoginEmail() != null;
    }
}
