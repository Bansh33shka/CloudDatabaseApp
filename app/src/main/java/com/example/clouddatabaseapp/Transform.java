package com.example.clouddatabaseapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Vibrator;

import static com.example.clouddatabaseapp.UserStaticInfo.LOGIN;
import static com.example.clouddatabaseapp.UserStaticInfo.PASSWORD;

public class Transform {
    public static final String APP_PREFERENCES = "mysettings";

    public static int parseIntOrDefault(String whatParse, int defaultValue) {
        int parse;
        try {
            parse = Integer.parseInt(whatParse);
        } catch (Exception NumberFormatException) {
            parse = defaultValue;
        }
        return parse;
    }

    public static Boolean StringNoNull(String string) {
        if (string == null) {
            return false;
        } else {
            return string.trim().length() != 0;
        }
    }

    public static void Vibrate(Context context) {
        long mills = 1000L;
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(mills);
        }
    }

    public static void SaveUser(SharedPreferences sp, String login, String password) {
        SharedPreferences.Editor e = sp.edit();
        e.putString(LOGIN, login);
        e.putString(PASSWORD, password);
        e.apply();
    }
}
