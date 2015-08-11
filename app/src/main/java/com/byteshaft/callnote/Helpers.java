package com.byteshaft.callnote;


import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Helpers extends ContextWrapper {

    public static final String LOG_TAG = "";
    private static Vibrator mVibrator;

    public Helpers(Context base) {
        super(base);
    }

    TelephonyManager getTelephonyManager() {
        return (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
    }

    private static Cursor getAllContacts(ContentResolver cr) {
        return cr.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null,
                "upper("+ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + ") ASC"
        );
    }

    public static List<String> getAllContactNames() {
        List<String> contactNames = new ArrayList<>();
        Cursor cursor = getAllContacts(AppGlobals.getContext().getContentResolver());
        while (cursor.moveToNext()) {
            String name = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            contactNames.add(name);
        }
        cursor.close();
        return contactNames;
    }

    public static List<String> getAllContactNumbers() {
        List<String> contactNumbers = new ArrayList<>();
        Cursor cursor = getAllContacts(AppGlobals.getContext().getContentResolver());
        while (cursor.moveToNext()) {
            String number = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contactNumbers.add(number);
        }
        cursor.close();
        return contactNumbers;
    }

    String getCurrentDateandTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyy h:mm a zz");
        return sdf.format(new Date());
    }

    void saveServiceStateEnabled(boolean enable) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putBoolean("enabled", enable).apply();
    }

    boolean isServiceSettingEnabled() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                getApplicationContext());
        return sharedPreferences.getBoolean("enabled", true);
    }

    SharedPreferences getPreferenceManager() {
        return PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    void saveSpinnerState(String key, int value) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putInt(key, value).apply();
    }

    int getSpinnerValue(String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                getApplicationContext());
        return sharedPreferences.getInt(key, 0);
    }

    static boolean isVibrationEnabled() {
        Context context = AppGlobals.getContext();
        boolean vibrateValue = false;
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        switch (audio.getRingerMode()) {
            case AudioManager.RINGER_MODE_VIBRATE:
                vibrateValue = true;
        }
        return vibrateValue;
    }

    void saveVibrationState(String key, boolean value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                getApplicationContext());
        sharedPreferences.edit().putBoolean(key+"_vibration", value).apply();
    }

    static boolean getVibrationState(String key) {
        Context context = AppGlobals.getContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context.getApplicationContext());
        return sharedPreferences.getBoolean(key + "_vibration", false);
    }

    static void vibrate() {
        Context context = AppGlobals.getContext();
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (mVibrator.hasVibrator()) {
            long[] pattern = {0, 200, 400, 200, 400, 200, 800};
            mVibrator.vibrate(pattern, 0);
        }
    }

    static void cancelVibration() {
        if (mVibrator != null) {
            mVibrator.cancel();
        }
    }
}