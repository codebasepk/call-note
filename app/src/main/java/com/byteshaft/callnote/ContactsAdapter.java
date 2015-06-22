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

    static SparseBooleanArray mCheckStates;
    private LayoutInflater mInflater;
    private SharedPreferences mPreferences;
    private List<String> mContactNames;
    private List<String> mContactNumbers;
    private ArrayList<String> numbersForNote;
    private String[] mCheckedContactsInSP;
    static StringBuilder sCheckedContactsToSave;
    private String[] mTemporaryDB;
    private Context mContext;

    ContactsAdapter(Context context, String noteTitle) {
        mContext = context;
        Helpers helper = new Helpers(context.getApplicationContext());
        DataBaseHelpers dataBaseHelpers = new DataBaseHelpers(context);
        if (AppGlobals.isIsNoteEditModeFirst()) {
            numbersForNote = dataBaseHelpers.getNumberFromNote(noteTitle);
            putDatabaseContactsToTemporarySP(numbersForNote);
            AppGlobals.setIsNoteEditModeFirst(false);
        }
        mTemporaryDB = getTemporarySP();
        mPreferences = AppGlobals.getSharedPreferences();
        mContactNames = helper.getAllContactNames();
        mContactNumbers = helper.getAllContactNumbers();
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mCheckStates = new SparseBooleanArray(mContactNumbers.size());
        int i = 0;
        for (String contactNumber : mContactNumbers) {
            for (String number : mTemporaryDB) {
                if (contactNumber.equals(number)) {
                    mCheckStates.put(i, true);
                }
            }
            i++;
        }
    }

    public List<String> getContactNumbers() {
        return mContactNumbers;
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
        viewHolder.checkbox.setChecked(mCheckStates.get(position));
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
        sCheckedContactsToSave = new StringBuilder();
        for (int i = 0; i < mContactNames.size(); i++) {
            if (mCheckStates.get(i)) {
                sCheckedContactsToSave.append(mContactNumbers.get(i));
                sCheckedContactsToSave.append(",");
            }
        }
    }

    private String[] getTempContacts() {
        String out = mPreferences.getString("checkedContactsTemp", null);
        if (out != null) {
            return out.split(",");
        } else {
            return null;
        }
    }

    private void putDatabaseContactsToTemporarySP(ArrayList<String> numbers) {
        StringBuilder builder = new StringBuilder();
        for (String number: numbers) {
            builder.append(number);
            builder.append(",");
        }
        mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mPreferences.edit().putString("checkedContactsTemp", builder.toString()).apply();
    }

    private String[] getTemporarySP() {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String output = mPreferences.getString("checkedContactsTemp", null);
        return output.split(",");
    }
}