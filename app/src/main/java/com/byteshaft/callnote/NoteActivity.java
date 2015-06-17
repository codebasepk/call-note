package com.byteshaft.callnote;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.opengl.Visibility;
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

public class NoteActivity extends ActionBarActivity {

    EditText noteTitle;
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
                    dbHelpers.createNewEntry(SqliteHelpers.NUMBER_COLUMN, checkedContacts,
                            SqliteHelpers.NOTES_COLUMN, note,SqliteHelpers.PICTURE_COLUMN,
                            "sdcard location", SqliteHelpers.DATE_COLUMN,mHelpers.getCurrentDateandTime());
                    this.finish();
                }
                break;
                case R.id.action_share:
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody = "Here is the share content body";
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, "Share via"));
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
        setContentView(R.layout.activity_note);
        mHelpers = new Helpers(getApplicationContext());
        dbHelpers = new DataBaseHelpers(getApplicationContext());
        editTextNote = (EditText) findViewById(R.id.editText_create_note);
        noteTitle = (EditText) findViewById(R.id.editText_title_note);
        MenuItem item = (MenuItem) findViewById(R.id.action_share);
        if (getIntent().getExtras() != null) {
            item.setEnabled(true);
                    noteTitle.setText(getIntent().getExtras().getString("note_title", ""));
            editTextNote.setText(getIntent().getExtras().getString("note_data", ""));
            setTitle("Edit Note");
        }
        addIcon = (Button) findViewById(R.id.button_icon);
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
        LayoutInflater inflater = LayoutInflater.from(NoteActivity.this);
        View dialog_layout = inflater.inflate(R.layout.dialog, (ViewGroup) findViewById(R.id.dialogLayout));
        AlertDialog.Builder db = new AlertDialog.Builder(NoteActivity.this);
        db.setView(dialog_layout);
        db.setTitle("Select Contacts");
        db.setPositiveButton("OK", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(NoteActivity.this, "Checked Contacts Selected", Toast.LENGTH_SHORT).show();
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
        LayoutInflater inflater = LayoutInflater.from(NoteActivity.this);
        View dialog_layout = inflater.inflate(R.layout.dialog_2, (ViewGroup) findViewById(R.id.dialogLayout_2));
        AlertDialog.Builder db = new AlertDialog.Builder(NoteActivity.this);
        db.setView(dialog_layout);
        db.setTitle("Add Icon");
        db.show();
    }
}