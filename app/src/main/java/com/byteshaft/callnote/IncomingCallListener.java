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
    private ArrayList<String> titles = new ArrayList<>();
    private ArrayList<String> summaries = new ArrayList<>();

    public IncomingCallListener(Context context) {
        super();
        mContext = context;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);
        System.out.println(incomingNumber);
        dbHelpers = new DataBaseHelpers(mContext);
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                getNotesForNumber(incomingNumber);
                if (titles.size() > 0 && summaries.size() > 0) {
                    OverlayHelpers.showPopupNoteForContact(titles.get(0), summaries.get(0));
                }
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                OverlayHelpers.removePopupNote();
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                OverlayHelpers.removePopupNote();
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
