package com.byteshaft.callnote;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageButton;

public class UpgradeDialog {

    private static View sUpgradeDialog;
    private static WindowManager sWindowManager;
    private static boolean isShown;

    public static void show(Activity activty) {
        LayoutInflater inflater = activty.getLayoutInflater();
        sUpgradeDialog = inflater.inflate(R.layout.upgrade_dialog, null);
        ImageButton yesButton = (ImageButton) sUpgradeDialog.findViewById(R.id.upgrade_button_yes);
        ImageButton noButton = (ImageButton) sUpgradeDialog.findViewById(R.id.upgrade_button_no);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        LayoutParams params = new LayoutParams();
        params.type = LayoutParams.TYPE_PHONE;
        params.height = LayoutParams.MATCH_PARENT;
        params.width = LayoutParams.MATCH_PARENT;
        params.format = PixelFormat.TRANSPARENT;
        sWindowManager = (WindowManager)
                AppGlobals.getContext().getSystemService(Context.WINDOW_SERVICE);
        sWindowManager.addView(sUpgradeDialog, params);
        isShown = true;
    }

    public static void dismiss() {
        if (isShown) {
            sWindowManager.removeView(sUpgradeDialog);
        }
        isShown = false;
    }
}
