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

    public IncomingCallListener(Context context) {
        super();
        mContext = context;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);
        dbHelpers = new DataBaseHelpers(mContext);
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                arrayList = dbHelpers.retrieveByNotesOrNumber((incomingNumber));
                for (String note: arrayList) {
                    OverlayHelpers.showPopupNoteForContact(note);
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
}
