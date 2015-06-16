package com.byteshaft.callnote;

import android.app.Application;
import android.view.LayoutInflater;
import android.view.WindowManager;

public class AppGlobals extends Application {

    private static LayoutInflater sLayoutInflater;
    private static WindowManager sWindowManager;

    @Override
    public void onCreate() {
        super.onCreate();
        sLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        sWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
    }

    static LayoutInflater getLayoutInflator() {
        return sLayoutInflater;
    }

    static WindowManager getWindowManager() {
        return sWindowManager;
    }
}
