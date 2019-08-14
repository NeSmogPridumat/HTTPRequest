package com.dreamteam.httprequest;

import android.content.Context;
import android.preference.PreferenceManager;


public class QueryPreferences {

    //=================================================Обработка id пользователя=========================//
    public static void saveSharedPreferences(String userID, Context context, String token) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("userID", userID).apply();
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("token", token).apply();
    }

    public static String getUserIdPreferences(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString("userID", "");
    }

    public static void exitLoginPreferences(Context context){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("userID", "").apply();
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("token", "").apply();
    }

    //==========================================Обработка результатов для сервиса================================//

    public static String getLastResultId(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString("lastResult", null);
    }

    public static void setLastResult(Context context, String jsonObjects){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("lastResult", jsonObjects).apply();
    }

    public static void setToken(String token, Context context){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("token", token).apply();
    }

    public static String getToken (Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString("token", "");
    }
}
