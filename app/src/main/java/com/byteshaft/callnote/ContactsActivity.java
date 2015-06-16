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
    Button addAnotherNote;
    Button attachContacts;
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
                    dbHelpers.createNewEntry(SqliteHelpers.NUMBER_COLUMN, checkedContacts , SqliteHelpers.NOTES_COLUMN, note,
                            SqliteHelpers.PICTURE_COLUMN, "sdcard location");
                    System.out.println("working");
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
        editTextNote = (EditText) findViewById(R.id.editText_create_note);
        adapter = new ContactsAdapter(getApplicationContext());
        mHelpers = new Helpers(getApplicationContext());
        dbHelpers = new DataBaseHelpers(getApplicationContext());
        addAnotherNote = (Button) findViewById(R.id.add_another_note);
        attachContacts = (Button) findViewById(R.id.attach_contacts);
        attachContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(ContactsActivity.this);
                View dialog_layout = inflater.inflate(R.layout.dialog, (ViewGroup)
                        findViewById(R.id.dialogLayout));
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ContactsActivity.this);
                dialogBuilder.setView(dialog_layout);
                dialogBuilder.setTitle("Select Contacts");
                dialogBuilder.setPositiveButton("OK", new
                        DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(ContactsActivity.this, "Checked Contacts Selected"
                                        + which, Toast.LENGTH_SHORT).show();
                            }
                        });
                dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                lv = (ListView) dialog_layout.findViewById(R.id.lv);
                ContactsAdapter ma = new ContactsAdapter(getApplicationContext());
                lv.setAdapter(ma);
                dialogBuilder.show();
            }
        });
    }
}