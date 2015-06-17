package com.byteshaft.callnote;

import android.graphics.PixelFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class OverlayHelpers {

    private static WindowManager mWindowManager;
    private static boolean isViewCreated;
    private static View mNotePopup;

    private static void createSystemOverlayForPreview(View view) {
        mWindowManager = AppGlobals.getWindowManager();
        WindowManager.LayoutParams params = getCustomWindowManagerParameters();
        mWindowManager.addView(view, params);
        isViewCreated = true;
    }

    private static WindowManager.LayoutParams getCustomWindowManagerParameters() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        params.format = PixelFormat.TRANSPARENT;
        return params;
    }

    static void removePopupNote() {
        if (mWindowManager != null && mNotePopup != null && isViewCreated) {
            mWindowManager.removeView(mNotePopup);
            isViewCreated = false;
        }
    }

    static void showPopupNoteForContact(String noteTitle, String noteSummary) {
        LayoutInflater inflater = AppGlobals.getLayoutInflator();
        mNotePopup = inflater.inflate(R.layout.overlay, null);
        TextView textView = (TextView) mNotePopup.findViewById(R.id.memo_id);
        TextView summary = (TextView) mNotePopup.findViewById(R.id.summary_text);
        textView.setText(noteTitle);
        summary.setText(noteSummary);
        createSystemOverlayForPreview(mNotePopup);
    }
}
