package com.byteshaft.callnote;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.WindowManager;

public class AppGlobals extends Application {

    private static LayoutInflater sLayoutInflater;
    private static WindowManager sWindowManager;
    private static SharedPreferences sPreferences;
    private static Context sContext;
    private static boolean isNoteEditModeFirst;
    private static boolean isNoteVisible;

    static LayoutInflater getLayoutInflater() {
        return sLayoutInflater;
    }

    static WindowManager getWindowManager() {
        return sWindowManager;
    }

    static Context getContext() {
        return sContext;
    }

    static SharedPreferences getSharedPreferences() {
        return sPreferences;
    }

    static boolean isIsNoteEditModeFirst() {
        return isNoteEditModeFirst;
    }

    static void setIsNoteEditModeFirst(boolean first) {
        isNoteEditModeFirst = first;
    }

    static boolean isNoteVisible() {
        return isNoteVisible;
    }

    static void setIsNoteVisible(boolean visible) {
        isNoteVisible = visible;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        sLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        sWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        sPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }
}
