package com.byteshaft.callnote;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class OverlayService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Helpers helpers = new Helpers(getApplicationContext());
        IncomingCallListener incomingCallListener = new IncomingCallListener(getApplicationContext());
        TelephonyManager telephonyManager = helpers.getTelephonyManager();
        telephonyManager.listen(incomingCallListener, PhoneStateListener.LISTEN_CALL_STATE);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
