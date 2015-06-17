package com.byteshaft.callnote;

import android.content.Context;
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
    private ArrayList<String> titles = new ArrayList<String>();
    private ArrayList<String> summaries = new ArrayList<String>();
    private OverlayHelpers mOverlayHelpers;

    public IncomingCallListener(Context context) {
        super();
        mContext = context;
        mOverlayHelpers = new OverlayHelpers(mContext.getApplicationContext());
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);
        dbHelpers = new DataBaseHelpers(mContext);
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                getNotesForNumber(incomingNumber);
                if (titles.size() == 1 && summaries.size() == 1) {
                    mOverlayHelpers.showSingleNoteOverlay(titles.get(0), summaries.get(0));
                } else if (titles.size() > 1 && summaries.size() > 1) {
                    mOverlayHelpers.showSingleNoteOverlay(titles, summaries, true);
                }
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                mOverlayHelpers.removePopupNote();
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                mOverlayHelpers.removePopupNote();
                break;
        }
    }

    private void getNotesForNumber(String number) {
        arrayList = dbHelpers.getAllNumbers();
        for(String contact : arrayList) {
            if (PhoneNumberUtils.compare(contact, number)) {
                ArrayList<String> noteTitles = dbHelpers.getTitleFromNumber(contact);
                ArrayList<String> noteSummaries = dbHelpers.getSummaryFromNumber(contact);
                for (String val: noteTitles) {
                    titles.add(val);
                }

                for (String value1 : noteSummaries) {
                    summaries.add(value1);
                }
                return;
            }
        }
    }
}
