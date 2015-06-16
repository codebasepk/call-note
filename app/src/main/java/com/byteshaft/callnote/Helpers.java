package com.byteshaft.callnote;

import android.content.Context;
import android.content.ContextWrapper;
import android.telephony.TelephonyManager;
import android.util.TypedValue;

public class Helpers extends ContextWrapper {

    public Helpers(Context base) {
        super(base);
    }

    public static final String LOG_TAG = "";

    static String logTag(Class presentClass) {
        return LOG_TAG + "/" + presentClass.getSimpleName();
    }

    TelephonyManager getTelephonyManager() {
        return (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
    }

    float getDensityPixels(int pixels) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, pixels, getResources().getDisplayMetrics());
    }
}
