package com.byteshaft.callnote;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class IncomingCallListener extends PhoneStateListener {

    private WindowManager mWindowManager;
    private Context mContext;
    private View view;
    private String mPhotoUri;
    private boolean isViewCreated;

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
                textView.setText("Hey ".concat(getContactNameFromNumber(incomingNumber)));
                ImageView imageView = (ImageView) view.findViewById(R.id.avatar);
                if (mPhotoUri == null) {
                    imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.contact));
                } else {
                    imageView.setImageURI(Uri.parse(mPhotoUri));
                }
                createSystemOverlayForPreview(view);
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                if (isViewCreated) {
                    removeView();
                }
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                if (isViewCreated) {
                    removeView();
                }
                break;
        }
    }

    private void createSystemOverlayForPreview(View view) {
        mWindowManager = getWindowManager();
        WindowManager.LayoutParams params = getCustomWindowManagerParameters();
        mWindowManager.addView(view, params);
        isViewCreated = true;
    }

    private WindowManager getWindowManager() {
        return (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
    }

    private WindowManager.LayoutParams getCustomWindowManagerParameters() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        params.format = PixelFormat.TRANSPARENT;
        return params;
    }

    private void removeView() {
        if (mWindowManager != null && view != null) {
            mWindowManager.removeView(view);
            isViewCreated = false;
        }
    }

    String getContactNameFromNumber(String phoneNumber) {
        ContentResolver cr = mContext.getContentResolver();
        Uri uri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.PhoneLookup.PHOTO_URI}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            mPhotoUri = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.PhoneLookup.PHOTO_URI));
        }

        if (!cursor.isClosed()) {
            cursor.close();
        }

        if (contactName == null) {
            return phoneNumber;
        }

        return contactName;
    }
}
