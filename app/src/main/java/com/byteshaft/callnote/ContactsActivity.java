package com.byteshaft.callnote;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ContactsActivity extends ActionBarActivity {

    EditText editTextNote;
    Button addIcon;
    Button attachContacts;
    Button checkAll;
    Button uncheckAll;
    ListView lv;
    ContactsAdapter adapter;
    Helpers mHelpers;
    DataBaseHelpers dbHelpers;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_apply:
                String note = editTextNote.getText().toString();
                String[] checkedContacts = mHelpers.getCheckedContacts();
                if (!note.isEmpty()) {
                    dbHelpers.createNewEntry(checkedContacts, note,"this is a test","sdcard location",
                            mHelpers.getCurrentDateandTime());
                    this.finish();
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_contacts, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        mHelpers = new Helpers(getApplicationContext());
        dbHelpers = new DataBaseHelpers(getApplicationContext());
        editTextNote = (EditText) findViewById(R.id.editText_create_note);
        addIcon = (Button) findViewById(R.id.add_another_note);
        addIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initiateIconDialog();
            }
        });
        attachContacts = (Button) findViewById(R.id.attach_contacts);
        attachContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showContactsDialog();
            }
        });
    }

    public void showContactsDialog() {
        LayoutInflater inflater = LayoutInflater.from(ContactsActivity.this);
        View dialog_layout = inflater.inflate(R.layout.dialog, (ViewGroup) findViewById(R.id.dialogLayout));
        AlertDialog.Builder db = new AlertDialog.Builder(ContactsActivity.this);
        db.setView(dialog_layout);
        db.setTitle("Select Contacts");
        db.setPositiveButton("OK", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(ContactsActivity.this, "Checked Contacts Selected", Toast.LENGTH_SHORT).show();
                    }
                });
        db.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        lv = (ListView) dialog_layout.findViewById(R.id.lv);
        ContactsAdapter ma = new ContactsAdapter(getApplicationContext());
        lv.setAdapter(ma);
        db.show();


        checkAll = (Button) findViewById(R.id.button_checkall);
        uncheckAll = (Button) findViewById(R.id.button_uncheck_all);
    }

    public void initiateIconDialog() {
        LayoutInflater inflater = LayoutInflater.from(ContactsActivity.this);
        View dialog_layout = inflater.inflate(R.layout.dialog_2, (ViewGroup) findViewById(R.id.dialogLayout_2));
        AlertDialog.Builder db = new AlertDialog.Builder(ContactsActivity.this);
        db.setView(dialog_layout);
        db.setTitle("Add Icon");
        db.show();
    }
}

