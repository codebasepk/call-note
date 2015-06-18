package com.byteshaft.callnote;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class ContactsAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener {

    private static SparseBooleanArray mCheckStates;
    private LayoutInflater mInflater;
    private SharedPreferences mPreferences;
    private List<String> mContactNames;
    private List<String> mContactNumbers;

    ContactsAdapter(Context context, String noteTitle) {
        Helpers helper = new Helpers(context.getApplicationContext());
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        mContactNames = helper.getAllContactNames();
        mContactNumbers = helper.getAllContactNumbers();
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mCheckStates = new SparseBooleanArray(mContactNumbers.size());
    }

    @Override
    public int getCount() {
        return mContactNames.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.listview, null);
        }
        TextView name = (TextView) view.findViewById(R.id.textView1);
        TextView number = (TextView) view.findViewById(R.id.textView2);
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox1);
        checkBox.setOnCheckedChangeListener(this);
        checkBox.setTag(position);
        checkBox.setChecked(mCheckStates.get(position, false));
        name.setText(mContactNames.get(position));
        number.setText(mContactNumbers.get(position));
        return view;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = (int) buttonView.getTag();
        mCheckStates.put(id, isChecked);
    }
}