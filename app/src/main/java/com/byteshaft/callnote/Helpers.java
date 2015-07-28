package com.byteshaft.callnote;

import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Helpers extends ContextWrapper {

    public Helpers(Context base) {
        super(base);
    }

    public static final String LOG_TAG = "";

    TelephonyManager getTelephonyManager() {
        return (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
    }

    private Cursor getAllContacts(ContentResolver cr) {
        return cr.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        );
    }

    List<String> getAllContactNames() {
        List<String> contactNames = new ArrayList<>();
        Cursor cursor = getAllContacts(getContentResolver());
        while (cursor.moveToNext()) {
            String name = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            contactNames.add(name);
        }
        cursor.close();
        return contactNames;
    }

    List<String> getAllContactNumbers() {
        List<String> contactNumbers = new ArrayList<>();
        Cursor cursor = getAllContacts(getContentResolver());
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

    void putTemporaryPreferenceToPermanent() {
        SharedPreferences preferences = getPreferenceManager();
        String temp = preferences.getString("checkedContactsTemp", null);
        preferences.edit().putString("checkedContactsPrefs", temp).commit();

    }

    void putPermanentPreferenceToTemporary() {
        SharedPreferences preferences = getPreferenceManager();
        String permanent = preferences.getString("checkedContactsPrefs", null);
        preferences.edit().putString("checkedContactsTemp", permanent).commit();
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
}