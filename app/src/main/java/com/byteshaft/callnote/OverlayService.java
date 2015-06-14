package com.byteshaft.callnote;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class OverlayService extends Service {

    private View view;
    private WindowManager mWindowManager;
    private IncomingCallListener mIncomingCallListener;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mIncomingCallListener = new IncomingCallListener(getApplicationContext());
        TelephonyManager telephonyManager = getTelephonyManager();
        telephonyManager.listen(mIncomingCallListener, PhoneStateListener.LISTEN_CALL_STATE);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeView();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createSystemOverlayForPreview(View view) {
        mWindowManager = getWindowManager();
        WindowManager.LayoutParams params = getCustomWindowManagerParameters();
        mWindowManager.addView(view, params);
    }

    private WindowManager getWindowManager() {
        return (WindowManager) getSystemService(Context.WINDOW_SERVICE);
    }

    private WindowManager.LayoutParams getCustomWindowManagerParameters() {
        final int ONE_PIXEL = 200;
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.height = ONE_PIXEL;
        params.width = ONE_PIXEL;
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        params.format = PixelFormat.TRANSLUCENT;
        return params;
    }

    private void removeView() {
        if (mWindowManager != null && view != null) {
            mWindowManager.removeView(view);
        }
    }

    private TelephonyManager getTelephonyManager() {
        return (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
    }
}
