package com.byteshaft.callnote;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class OverlayHelpers extends ContextWrapper implements View.OnClickListener, AdapterView.OnItemClickListener {

    private WindowManager mWindowManager;
    private boolean isViewCreated;
    private RelativeLayout mSimpleLayout;
    private RelativeLayout mScrollableLayout;
    private ArrayAdapter<String> mArrayAdapter;
    private ArrayList<String> mTitles;
    private ArrayList<String> mSummaries;
    private ArrayList<String> mIcons;
    private boolean mHasMoreNotes;
    private String whichViewIsVisible;
    private RelativeLayout mBubbleLayout;
    private LayoutInflater mLayoutInflater;
    private CustomScrollView mListView;
    private WindowManager.LayoutParams mLayoutParams;
    private ArrayList<String> mLatestNote;
    private boolean isPopupExpanded;
    private Button aButton;

    public OverlayHelpers(Context base) {
        super(base);
        mWindowManager = AppGlobals.getWindowManager();
        mLayoutInflater = AppGlobals.getLayoutInflater();
    }

    void removePopupNote() {
        if (isViewCreated) {
            mWindowManager.removeView(mBubbleLayout);
        }
        isViewCreated = false;
    }

    void showNoteOverlay(ArrayList<String> titles, ArrayList<String> summaries, ArrayList<String> icons) {
        mTitles = titles;
        mSummaries = summaries;
        mIcons = icons;
        mLatestNote = new ArrayList<>();
        mLatestNote.add(mTitles.get(0));
        mArrayAdapter = new CustomBubbleAdapter(getApplicationContext(), R.layout.row, mLatestNote);
        mBubbleLayout = (RelativeLayout) mLayoutInflater.inflate(R.layout.overlay, null);
        mListView = (CustomScrollView)  mBubbleLayout.findViewById(R.id.left_drawer);
        mListView.setMaxHeight(220);
        mListView.setOnItemClickListener(this);
        mListView.setAdapter(mArrayAdapter);
        aButton = (Button) mBubbleLayout.findViewById(R.id.button_less_more);
        aButton.setOnClickListener(this);
        if (titles.size() == 1) {
            aButton.setVisibility(View.GONE);
        } else if (titles.size() > 1) {
            aButton.setBackgroundResource(R.drawable.ic_expand);
            aButton.setVisibility(View.VISIBLE);
        }
        addViewToWindowManager(mBubbleLayout);
    }

    private void expandNoteOverlay() {
        mArrayAdapter = new CustomBubbleAdapter(getApplicationContext(), R.layout.row, mTitles);
        mListView.setAdapter(mArrayAdapter);
        aButton.setBackgroundResource(R.drawable.ic_contract);
        mWindowManager.updateViewLayout(mBubbleLayout, mLayoutParams);
        isPopupExpanded = true;
    }

    private void collapseNoteOverlay() {
        mArrayAdapter = new CustomBubbleAdapter(getApplicationContext(), R.layout.row, mLatestNote);
        mListView.setAdapter(mArrayAdapter);
        aButton.setBackgroundResource(R.drawable.ic_expand);
        mWindowManager.updateViewLayout(mBubbleLayout, mLayoutParams);
        isPopupExpanded = false;
    }

    private void addViewToWindowManager(View view) {
        mWindowManager = AppGlobals.getWindowManager();
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS|WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.format = PixelFormat.TRANSPARENT;
        mLayoutParams.gravity = Gravity.TOP;
        mLayoutParams.y = getTwentyPercentDownScreenHeight();
        mWindowManager.addView(view, mLayoutParams);
        isViewCreated = true;
    }

    private int getTwentyPercentDownScreenHeight() {
        WindowManager wm = AppGlobals.getWindowManager();
        Display display = wm.getDefaultDisplay();
        return display.getHeight() / 5;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_less_more:
                if (isPopupExpanded) {
                    collapseNoteOverlay();

                } else {
                    expandNoteOverlay();
                }
                break;
            case R.id.left_drawer:
                removePopupNote();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        removePopupNote();
    }

    class CustomBubbleAdapter extends ArrayAdapter<String> {

        public CustomBubbleAdapter(Context context, int resource, ArrayList<String> notes) {
            super(context, resource, notes);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = AppGlobals.getLayoutInflater();
                convertView = inflater.inflate(R.layout.overlay_row, parent, false);
                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.note_title);
                holder.summary = (TextView) convertView.findViewById(R.id.note_summary);
                holder.image = (ImageView) convertView.findViewById(R.id.icon_overlay_row);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.title.setText(mTitles.get(position));
            holder.summary.setText(mSummaries.get(position));
            holder.image.setImageURI(Uri.parse(mIcons.get(position)));
            return convertView;
        }
    }

    static class ViewHolder {
        public TextView title;
        public TextView summary;
        private ImageView image;
    }
}
