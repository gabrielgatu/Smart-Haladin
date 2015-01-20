package com.gabrielgatu.haladin.providers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.gabrielgatu.haladin.beans.DataFlow;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by gabrielgatu on 19/01/15.
 */
public class DataProvider {

    public static final String KEY_DATA = "com.gabrielgatu.haladin.DATA";

    public static void saveData(Context context, ArrayList<DataFlow> flows) {
        Gson gson = new Gson();
        String data = gson.toJson(flows);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(KEY_DATA, data).commit();
    }

    public static ArrayList<DataFlow> getData(Context context, String defaultValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(KEY_DATA, defaultValue);

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<DataFlow>>(){}.getType();
        return gson.fromJson(json, type);
    }

}
