package com.byteshaft.callnote;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity implements Switch.OnCheckedChangeListener
        , Button.OnClickListener, AdapterView.OnItemClickListener {

    Helpers mHelpers;
    private ArrayAdapter<String> mModeAdapter;
    private boolean mViewCreated;
    DataBaseHelpers mDbHelpers;
    ArrayList<String> arrayList;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHelpers = new Helpers(getApplicationContext());
        Switch toggleSwitch = (Switch) findViewById(R.id.aSwitch);
        mDbHelpers = new DataBaseHelpers(getApplicationContext());
        toggleSwitch.setOnCheckedChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        arrayList = mDbHelpers.getAllPresentNotes();
        mModeAdapter = new NotesArrayList(this, R.layout.row, arrayList);
        listView = (ListView) findViewById(R.id.listView_main);
        listView.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, arrayList));
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Intent intent = new Intent(getApplicationContext(), OverlayService.class);
        if (isChecked) {
            startService(intent);
        } else {
            stopService(intent);
        }
    }

    public void openActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), ContactsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(new Intent(this, ContactsActivity.class));
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_overlay:
                if (mViewCreated) {
                    OverlayHelpers.removePopupNote();
                    mViewCreated = false;
                } else {
                    OverlayHelpers.showPopupNoteForContact("+923422347000");
                    mViewCreated = true;
                }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, NoteDetails.class);
        startActivity(intent);
        System.out.println(parent.getItemAtPosition(position));
    }

    class NotesArrayList extends ArrayAdapter<String> {

            public NotesArrayList(Context context, int resource, ArrayList<String> videos) {
                super(context, resource, videos);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder holder;
                if (convertView == null) {
                    LayoutInflater inflater = getLayoutInflater();
                    convertView = inflater.inflate(R.layout.row, parent, false);
                    holder = new ViewHolder();
                    holder.title = (TextView) convertView.findViewById(R.id.FilePath);
                    holder.time = (TextView) convertView.findViewById(R.id.tv);
                    holder.thumbnail = (ImageView) convertView.findViewById(R.id.Thumbnail);
                    convertView.setTag(holder);
                }
//                else {
//                    holder = (ViewHolder) convertView.getTag();
//                }
//                holder.title.setText(mVideosTitles[position]);
//                holder.time.setText(
//                        mHelper.getFormattedTime((mHelper.getDurationForVideo(position))));
//                holder.position = position;
//                if (BitmapCache.getBitmapFromMemCache(String.valueOf(position)) == null) {
//                    holder.thumbnail.setImageURI(null);
//                    new ThumbnailCreationTask(getApplicationContext(),
//                            holder, position).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//                } else {
//                    holder.thumbnail.setImageBitmap(BitmapCache.getBitmapFromMemCache
//                            (String.valueOf(position)));
//                }
                return convertView;
            }
        }

    static class ViewHolder {
        public TextView title;
        public TextView time;
        public ImageView thumbnail;
        public int position;
    }
}