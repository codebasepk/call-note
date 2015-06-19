package com.byteshaft.callnote;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.PixelFormat;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
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
    private boolean mHasMoreNotes;
    private String whichViewIsVisible;

    public OverlayHelpers(Context base) {
        super(base);
    }

    void removePopupNote() {
        if (mWindowManager != null && isViewCreated) {
            switch (whichViewIsVisible) {
                case "single":
                    mWindowManager.removeView(mSimpleLayout);
                    System.out.println("Removed View SIMPLE");
                    break;
                case "multi":
                    mWindowManager.removeView(mScrollableLayout);
                    System.out.println("Removed View COMPLEX");
                    break;
            }
        }
        isViewCreated = false;
    }

    void showSingleNoteOverlay(String noteTitle, String noteSummary) {
        LayoutInflater inflater = AppGlobals.getLayoutInflater();
        mSimpleLayout = (RelativeLayout) inflater.inflate(R.layout.overlay_simple, null);
        LinearLayout bubbleLayout = (LinearLayout) mSimpleLayout.findViewById(R.id.linear_layout);
        bubbleLayout.setOnClickListener(this);
        TextView title = (TextView) mSimpleLayout.findViewById(R.id.title);
        TextView summary = (TextView) mSimpleLayout.findViewById(R.id.summary);
        Button moreButton = (Button) mSimpleLayout.findViewById(R.id.more_button);
        if (mHasMoreNotes) {
            moreButton.setVisibility(View.VISIBLE);
            moreButton.setOnClickListener(this);
        } else {
            moreButton.setVisibility(View.GONE);
        }
        title.setText(noteTitle);
        summary.setText(noteSummary);
        addViewToWindowManager(mSimpleLayout);
        whichViewIsVisible = "single";
    }

    void showSingleNoteOverlay(ArrayList<String> titles, ArrayList<String> summaries, boolean hasMore) {
        mTitles = titles;
        mSummaries = summaries;
        mHasMoreNotes = hasMore;
        showSingleNoteOverlay(mTitles.get(0), mSummaries.get(0));
    }

    private void showMultiNoteOverlay() {
        mArrayAdapter = new CustomBubbleAdapter(getApplicationContext(), R.layout.row, mTitles);
        LayoutInflater inflater = AppGlobals.getLayoutInflater();
        mScrollableLayout = (RelativeLayout) inflater.inflate(R.layout.overlay, null);
        ListView mList = (ListView)  mScrollableLayout.findViewById(R.id.left_drawer);
        mList.setOnItemClickListener(this);
        mList.setAdapter(mArrayAdapter);
        Button aButton = (Button) mScrollableLayout.findViewById(R.id.less_button);
        aButton.setOnClickListener(this);
        addViewToWindowManager(mScrollableLayout);
        whichViewIsVisible = "multi";
    }

    private void addViewToWindowManager(View view) {
        mWindowManager = AppGlobals.getWindowManager();
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS|WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSPARENT;
        params.gravity = Gravity.TOP;
        params.y = getTwentyPercentDownScreenHeight();
        mWindowManager.addView(view, params);
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
            case R.id.less_button:
                removePopupNote();
                showSingleNoteOverlay(mTitles.get(0), mSummaries.get(0));
                break;
            case R.id.linear_layout:
                removePopupNote();
                break;
            case R.id.more_button:
                removePopupNote();
                showMultiNoteOverlay();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        removePopupNote();
    }

    class CustomBubbleAdapter extends ArrayAdapter<String> {

        private Context mContext;

        public CustomBubbleAdapter(Context context, int resource, ArrayList<String> notes) {
            super(context, resource, notes);
            mContext = context;
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
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.title.setText(mTitles.get(position));
            holder.summary.setText(mSummaries.get(position));
            return convertView;
        }
    }

    static class ViewHolder {
        public TextView title;
        public TextView summary;
    }
}
