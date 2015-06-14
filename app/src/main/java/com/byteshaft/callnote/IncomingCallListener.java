package com.byteshaft.callnote;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class IncomingCallListener extends PhoneStateListener {

    private WindowManager mWindowManager;
    private Context mContext;
    private View view;

    public IncomingCallListener(Context context) {
        super();
        mContext = context;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.overlay, null);
                TextView textView = (TextView) view.findViewById(R.id.memo_id);
                textView.setText("Hey ".concat(incomingNumber));
                createSystemOverlayForPreview(view);
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                removeView();
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                removeView();
                break;
        }
    }

    private void createSystemOverlayForPreview(View view) {
        mWindowManager = getWindowManager();
        WindowManager.LayoutParams params = getCustomWindowManagerParameters();
        mWindowManager.addView(view, params);
    }

    private WindowManager getWindowManager() {
        return (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
    }

    private WindowManager.LayoutParams getCustomWindowManagerParameters() {
        final int ONE_PIXEL = (int) getDensityPixels(20);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.height = (int) getDensityPixels(30);
        params.width = (int) getDensityPixels(300);
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
        return (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
    }

    float getDensityPixels(int pixels) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, pixels, mContext.getResources().getDisplayMetrics());
    }
}
