package com.byteshaft.callnote;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.telephony.PhoneNumberUtils;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IncomingCallListener extends PhoneStateListener {
    ArrayList<String> arrayList;
    DataBaseHelpers dbHelpers;
    Context mContext;
    private ArrayList<String> titles;
    private ArrayList<String> summaries;
    private ArrayList<String> mImages;
    private OverlayHelpers mOverlayHelpers;
    private SQLiteDatabase mDbHelper;
    private SqliteHelpers mSqliteHelpers;

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
                getAllNotesForNumber(incomingNumber);
                if (titles.size() > 0 && summaries.size() > 0) {
                    mOverlayHelpers.showNoteOverlay(titles, summaries, mImages);
                }
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                mOverlayHelpers.removePopupNote();
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
//                mOverlayHelpers.removePopupNote();
                break;
        }
    }

    void getAllNotesForNumber(String number) {
        mDbHelper = mSqliteHelpers.getWritableDatabase();
        titles = new ArrayList<>();
        summaries = new ArrayList<>();
        mImages = new ArrayList<>();
        String query = String.format(
                "SELECT * FROM %s", SqliteHelpers.TABLE_NAME);
        Cursor cursor = mDbHelper.rawQuery(query, null);
        while (cursor.moveToNext()) {
            String numbers = cursor.getString(cursor.getColumnIndex(SqliteHelpers.NUMBER_COLUMN));
            String[] numbersArray = numbers.split(",");
            for (int i = 0; i < numbersArray.length; i++) {
                if (PhoneNumberUtils.compare(numbersArray[i], number)) {
                    String title = cursor.getString(cursor.getColumnIndex(SqliteHelpers.NOTES_COLUMN));
                    String summary = cursor.getString(cursor.getColumnIndex(SqliteHelpers.DESCRIPTION));
                    String image = cursor.getString(cursor.getColumnIndex(SqliteHelpers.PICTURE_COLUMN));
                    titles.add(title);
                    summaries.add(summary);
                    mImages.add(image);
                }
            }
        }
    }

    BroadcastReceiver mOutgoingCallListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            getAllNotesForNumber(number);
                if (titles.size() > 0 && summaries.size() > 0) {
                    mOverlayHelpers.showNoteOverlay(titles, summaries, mImages);
                }
        }
    };
}
