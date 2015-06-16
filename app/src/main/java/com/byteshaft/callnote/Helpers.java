package com.byteshaft.callnote;

import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.TypedValue;

import java.util.ArrayList;
import java.util.List;

public class Helpers extends ContextWrapper {

    public Helpers(Context base) {
        super(base);
    }

    public static final String LOG_TAG = "";

    static String logTag(Class presentClass) {
        return LOG_TAG + "/" + presentClass.getSimpleName();
    }

    TelephonyManager getTelephonyManager() {
        return (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
    }

    float getDensityPixels(int pixels) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, pixels, getResources().getDisplayMetrics());
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

    public boolean contactExists(String number, ContentResolver contentResolver) {
        Cursor phones = getAllContacts(contentResolver);
        while (phones.moveToNext()){
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if(PhoneNumberUtils.compare(number, phoneNumber)){
                return true;
            }
        }
        return false;
    }

    boolean contactExistsInWhitelist(String number, String checkedContacts) {
        boolean contactExistsInWhitelist = false;
        String[] checkContactsArray = getCheckedContacts(checkedContacts);
        for(String contact : checkContactsArray) {
            if (PhoneNumberUtils.compare(contact, number)) {
                contactExistsInWhitelist = true;
            }
        }
        return contactExistsInWhitelist;
    }

    private String[] getCheckedContacts(String checkedContacts) {
        return checkedContacts.split(",");
    }

    void getCheckedContactsFromSharedPrefrence(List<String> contactNumber) {
        String[] checkedContacts = getCheckedContacts();
        int i = 0;
        for (String contact : contactNumber) {
            for (String checkedContact: checkedContacts) {
                if (contact.equals(checkedContact)) {
                    ContactsAdapter.mCheckStates.put(i, true);
                }
            }
            i++;
        }
    }

    String[] getCheckedContacts() {
        String string = getPrefrenceManager().getString("checkedContactsPrefs", " ");
        return string.split(",");
    }

    SharedPreferences getPrefrenceManager() {
        return PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

    }


}