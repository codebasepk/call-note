package com.byteshaft.callnote;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity implements Switch.OnCheckedChangeListener,
        AdapterView.OnItemClickListener {

    Helpers mHelpers;
    private boolean mViewCreated;
    DataBaseHelpers mDbHelpers;
    ArrayList<String> arrayList;
    ListView listView;
    TextView textViewTitle;
    private ArrayList<String> mNoteSummaries;
    private OverlayHelpers mOverlayHelpers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#689F39")));
        textViewTitle = (TextView) findViewById(R.id.title);
        mHelpers = new Helpers(getApplicationContext());
        Switch toggleSwitch = (Switch) findViewById(R.id.aSwitch);
        mDbHelpers = new DataBaseHelpers(getApplicationContext());
        toggleSwitch.setOnCheckedChangeListener(this);
        mOverlayHelpers = new OverlayHelpers(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        arrayList = mDbHelpers.getAllPresentNotes();
        mNoteSummaries = mDbHelpers.getDescriptions();
        ArrayAdapter<String> mModeAdapter = new NotesArrayList(this, R.layout.row, arrayList);
        listView = (ListView) findViewById(R.id.listView_main);
        listView.setAdapter(mModeAdapter);
        listView.setOnItemClickListener(this);
        listView.setDivider(null);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_overlay:
                if (mViewCreated) {
                    mOverlayHelpers.removePopupNote();
                    mViewCreated = false;
                } else {
                    mOverlayHelpers.showSingleNoteOverlay("Hey yo", "Get some eggs");
                    mViewCreated = true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#689F39")));
        return super.onCreateOptionsMenu(menu);
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
        Intent intent = new Intent(getApplicationContext(), NoteActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(new Intent(this, NoteActivity.class));
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra("note_title", arrayList.get(position));
        intent.putExtra("note_summary", mNoteSummaries.get(position));
        startActivity(intent);
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
                    holder.thumbnail = (ImageView) convertView.findViewById(R.id.Thumbnail);
                    holder.summary = (TextView) convertView.findViewById(R.id.summary);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                    holder.title.setText(arrayList.get(position));
                    holder.summary.setText(mNoteSummaries.get(position));
                }
                holder.title.setText(arrayList.get(position));
                holder.summary.setText(mNoteSummaries.get(position));
                holder.thumbnail.setImageResource(R.drawable.character_1);
                return convertView;
            }
        }

    static class ViewHolder {
        public TextView title;
        public TextView summary;
        public ImageView thumbnail;
    }
}