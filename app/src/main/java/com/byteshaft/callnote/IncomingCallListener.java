package com.byteshaft.callnote;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.telephony.PhoneNumberUtils;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import java.util.ArrayList;

public class IncomingCallListener extends PhoneStateListener {

    private Context mContext;
    private ArrayList<String> mTitles;
    private ArrayList<String> mSummaries;
    private ArrayList<String> mImages;
    private OverlayHelpers mOverlayHelpers;
    private SqliteHelpers mSqliteHelpers;

    private static class Note {
        static int SHOW_ON_CALL = 0;
        static int SHOW_AFTER_CALL = 1;
        static int SHOW_BEFORE_AND_AFTER_CALL = 2;
        static int SHOW_NEVER = 3;
    }

    public IncomingCallListener(Context context) {
        super();
        mContext = context;
        mOverlayHelpers = new OverlayHelpers(mContext.getApplicationContext());
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);
        mSqliteHelpers = new SqliteHelpers(mContext);
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                if (incomingNumber != null) {
                    getAllNotesForNumber(incomingNumber, Note.SHOW_ON_CALL);
                    if (mTitles.size() > 0 && mSummaries.size() > 0) {
                        mOverlayHelpers.showNoteOverlay(mTitles, mSummaries, mImages);
                    }
                }
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                mOverlayHelpers.removePopupNote();
                if (incomingNumber != null) {
                    getAllNotesForNumber(incomingNumber, Note.SHOW_AFTER_CALL);
                    if (mTitles.size() > 0 && mSummaries.size() > 0) {
                        mOverlayHelpers.showNoteOverlay(mTitles, mSummaries, mImages);
                    }
                }
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                mOverlayHelpers.removePopupNote();
                break;
        }
    }

    private boolean getAllNotesForNumber(String number, int showWhen) {
        SQLiteDatabase database = mSqliteHelpers.getWritableDatabase();
        ArrayList<String> titles = new ArrayList<>();
        ArrayList<String> summaries = new ArrayList<>();
        ArrayList<String> images = new ArrayList<>();
        String query = String.format(
                "SELECT * FROM %s", SqliteHelpers.TABLE_NAME);
        Cursor cursor = database.rawQuery(query, null);
        while (cursor.moveToNext()) {
            String numbers = cursor.getString(cursor.getColumnIndex(SqliteHelpers.NUMBER_COLUMN));
            if (numbers == null) {
                return false;
            }
            String[] numbersArray = numbers.split(",");
            for (String aNumbersArray : numbersArray) {
                if (PhoneNumberUtils.compare(aNumbersArray, number)) {
                    String title = cursor.getString(cursor.getColumnIndex(SqliteHelpers.NOTES_COLUMN));
                    String summary = cursor.getString(cursor.getColumnIndex(SqliteHelpers.DESCRIPTION));
                    String image = cursor.getString(cursor.getColumnIndex(SqliteHelpers.PICTURE_COLUMN));
                    titles.add(title);
                    summaries.add(summary);
                    images.add(image);
                }
            }
        }
        // Filter notes that are enabled;
        mTitles = new ArrayList<>();
        mSummaries = new ArrayList<>();
        mImages = new ArrayList<>();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        for (int i = 0; i < titles.size(); i++) {
            int noteShowPreference = preferences.getInt(titles.get(i), Note.SHOW_NEVER);
            if (noteShowPreference == showWhen || noteShowPreference == Note.SHOW_BEFORE_AND_AFTER_CALL) {
                mTitles.add(titles.get(i));
                mSummaries.add(summaries.get(i));
                mImages.add(images.get(i));
            }
        }

        return true;
    }

//    BroadcastReceiver mOutgoingCallListener = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
//            getAllNotesForNumber(number);
//                if (titles.size() > 0 && summaries.size() > 0) {
//                    mOverlayHelpers.showNoteOverlay(titles, summaries, mImages);
//                }
//        }
//    };
}
