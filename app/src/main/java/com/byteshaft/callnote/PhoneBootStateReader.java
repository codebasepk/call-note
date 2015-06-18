package com.byteshaft.callnote;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PhoneBootStateReader extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (isServiceSettingEnabled(context)) {
            context.startService(new Intent(context, OverlayService.class));
        }
    }

    private boolean isServiceSettingEnabled(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("enabled", false);
    }
}
