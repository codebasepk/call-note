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

import java.util.ArrayList;
import java.util.List;

public class ContactsAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener {

    private SparseBooleanArray mCheckStates;
    private LayoutInflater mInflater;
    private SharedPreferences mPreferences;
    private List<String> mContactNames;
    private List<String> mContactNumbers;
    private ArrayList<String> numbersForNote;

    ContactsAdapter(Context context, String noteTitle) {
        Helpers helper = new Helpers(context.getApplicationContext());
        DataBaseHelpers dataBaseHelpers = new DataBaseHelpers(context);
        numbersForNote = dataBaseHelpers.getNumberFromNote(noteTitle);
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
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = AppGlobals.getLayoutInflater();
            convertView = inflater.inflate(R.layout.listview, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.textView1);
            viewHolder.number = (TextView) convertView.findViewById(R.id.textView2);
            viewHolder.checkbox = (CheckBox) convertView.findViewById(R.id.checkBox1);
            viewHolder.checkbox.setOnCheckedChangeListener(this);
            convertView.setTag(viewHolder);
            convertView.setTag(R.id.textView1, viewHolder.name);
            convertView.setTag(R.id.textView2, viewHolder.number);
            convertView.setTag(R.id.checkBox1, viewHolder.checkbox);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.checkbox.setTag(position);
        String checkedPref = mPreferences.getString("check", "blah");
        if (checkedPref.equals("checked_all")) {
            viewHolder.checkbox.setChecked(true);
        } else if(checkedPref.equals("unchecked_all")) {
            viewHolder.checkbox.setChecked(false);
        } else {
            for (String number : numbersForNote) {
                viewHolder.checkbox.setChecked(mContactNumbers.get(position).equals(number));
            }
            viewHolder.checkbox.setChecked(mCheckStates.get(position));
        }
        viewHolder.name.setText(mContactNames.get(position));
        viewHolder.number.setText(mContactNumbers.get(position));
        return convertView;
    }

    static class ViewHolder {
        private TextView name;
        private TextView number;
        private CheckBox checkbox;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = (int) buttonView.getTag();
        mCheckStates.put(id, isChecked);
        StringBuilder checkedContacts = new StringBuilder();
        for (int i = 0; i < mContactNames.size(); i++) {
            if (mCheckStates.get(i)) {
                checkedContacts.append(mContactNumbers.get(i));
                checkedContacts.append(",");
            }
        }
        mPreferences.edit().putString("checkedContactsPrefs", checkedContacts.toString()).apply();
    }
}