package com.byteshaft.callnote;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class OverlayService extends Service {
    private IncomingCallListener incomingCallListener;
    private TelephonyManager mTelephonyManager;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Helpers helpers = new Helpers(getApplicationContext());
        incomingCallListener = new IncomingCallListener(getApplicationContext());
        mTelephonyManager = helpers.getTelephonyManager();
        mTelephonyManager.listen(incomingCallListener, PhoneStateListener.LISTEN_CALL_STATE);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTelephonyManager.listen(incomingCallListener, PhoneStateListener.LISTEN_NONE);
    }
}
