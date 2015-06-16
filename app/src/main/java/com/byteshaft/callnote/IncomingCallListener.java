package com.byteshaft.callnote;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class IncomingCallListener extends PhoneStateListener {

    public IncomingCallListener(Context context) {
        super();
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                OverlayHelpers.showPopupNoteForContact(incomingNumber);
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
