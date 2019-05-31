package com.dreamteam.httprequest;

import android.content.Context;
import android.preference.PreferenceManager;

public class QueryPreferences {

    //=================================================Обработка id пользователя=========================//
    public static void saveSharedPreferences(String userID, Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("userID", userID).apply();
    }

    public static String getUserIdPreferences(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString("userID", "");
    }

    public static void exitLoginPreferences(Context context){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("userID", "").apply();
    }

    //==========================================Обработка результатов для сервиса================================//

    public static String getLastResultId(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString("lastResult", null);
    }

    public static void setLastResult(Context context, String jsonObjects){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("lastResult", jsonObjects).apply();
    }
}
