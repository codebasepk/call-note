package com.byteshaft.callnote;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    private ArrayList<String> mImages;
    private OverlayHelpers mOverlayHelpers;
    private SqliteHelpers mSqliteHelpers;
    private boolean isOutGoingCall = false;
    BroadcastReceiver mOutgoingCallListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            getAllNotesForNumber(number, Note.SHOW_OUTGOING_CALL);
            isOutGoingCall = true;
            if (mTitles.size() > 0) {
                mOverlayHelpers.showNoteOverlay(mTitles, mImages);
            }
        }
    };

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
                    getAllNotesForNumber(incomingNumber, Note.SHOW_INCOMING_CALL);
                    if (mTitles.size() > 0) {
                        mOverlayHelpers.showNoteOverlay(mTitles, mImages);
                    }
                }
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                OverlayHelpers.removePopupNote();
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                if (!isOutGoingCall) {
                    OverlayHelpers.removePopupNote();
                }
                break;
        }
    }

    private boolean getAllNotesForNumber(String number, int showWhen) {
        SQLiteDatabase database = mSqliteHelpers.getWritableDatabase();
        ArrayList<String> titles = new ArrayList<>();
        ArrayList<String> images = new ArrayList<>();
        String query = String.format(
                "SELECT * FROM %s", SqliteHelpers.TABLE_NAME);
        Cursor cursor = database.rawQuery(query, null);
        while (cursor.moveToNext()) {
            String numbers = cursor.getString(cursor.getColumnIndex(SqliteHelpers.NUMBER_COLUMN));
            String[] numbersArray = numbers.split(",");
            for (String aNumbersArray : numbersArray) {
                if (PhoneNumberUtils.compare(aNumbersArray, number)) {
                    String title = cursor.getString(cursor.getColumnIndex(SqliteHelpers.NOTES_COLUMN));
                    String image = cursor.getString(cursor.getColumnIndex(SqliteHelpers.PICTURE_COLUMN));
                    titles.add(title);
                    images.add(image);
                }
            }
        }
        // Filter notes that are enabled;
        mTitles = new ArrayList<>();
        mImages = new ArrayList<>();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        for (int i = 0; i < titles.size(); i++) {
            int noteShowPreference = preferences.getInt(titles.get(i), Note.TURN_OFF);
            if (noteShowPreference == showWhen || noteShowPreference == Note.SHOW_INCOMING_OUTGOING) {
                mTitles.add(titles.get(i));
                mImages.add(images.get(i));
            }
        }

        return true;
    }

    public static class Note {
        static final int SHOW_INCOMING_CALL = 0;
        static final int SHOW_OUTGOING_CALL = 1;
        static final int SHOW_INCOMING_OUTGOING = 2;
        static final int TURN_OFF = 3;
    }
}
