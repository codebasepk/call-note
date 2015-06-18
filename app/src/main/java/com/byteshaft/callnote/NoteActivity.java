package com.byteshaft.callnote;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.opengl.Visibility;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
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

public class NoteActivity extends ActionBarActivity  {

    private EditText noteTitle;
    private EditText editTextNote;
    private Button addIcon;
    private Button attachContacts;
    private Button checkAll;
    private Button uncheckAll;
    private ListView lv;
    private ContactsAdapter adapter;
    private Helpers mHelpers;
    private DataBaseHelpers dbHelpers;
    private String title;
    private String description;
    private String[] checkedContacts;
    private String mId = null;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        title = noteTitle.getText().toString();
        description = editTextNote.getText().toString();
        checkedContacts = mHelpers.getCheckedContacts();
        if (title.isEmpty()) {
            title = mHelpers.getCurrentDateandTime();
        }
        switch (item.getItemId()) {
            case R.id.action_apply:
                System.out.println("ID "+mId);
                if (mId != null) {
                    dbHelpers.deleteItem(SqliteHelpers.ID_COLUMN,mId,false);
                    dbHelpers.clickUpdate(checkedContacts ,title, description,
                                    "Sdcard link", mHelpers.getCurrentDateandTime());
                    Log.i(Helpers.LOG_TAG,"Update success");
                    this.finish();
                    }

                else {
                    if (dbHelpers.checkIfItemAlreadyExistInDatabase(title) != null) {
                        NotesAlreadyExistDialog();
                    } else if (dbHelpers.checkIfItemAlreadyExistInDatabase(title) == null) {
                        dbHelpers.createNewEntry(checkedContacts, title, description, "sdcard location",
                                mHelpers.getCurrentDateandTime());
                        this.finish();
                    }
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
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_contacts, menu);
        if (getIntent().getExtras() != null) {
            menu.findItem(R.id.action_share).setVisible(true);
            noteTitle.setText(getIntent().getExtras().getString("note_title", ""));
            editTextNote.setText(getIntent().getExtras().getString("note_summary", ""));
            setTitle("Edit Note");
        }
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#689F39")));
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
        if (getIntent().getExtras() != null) {
            String title = getIntent().getExtras().getString("note_title", "");
                    noteTitle.setText(title);
            String[] detailsForThisNote = dbHelpers.retrieveNoteDetails(title);
            mId = detailsForThisNote[0];
            System.out.println("ID "+mId);
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

        @Override
        public void onBackPressed() {
            if (editTextNote.length() > 0 || noteTitle.length() > 0){
                discardDialog();
            } else {
                finish();
            }
        }

    void discardDialog() {
        new AlertDialog.Builder(this)
                .setMessage("Discard Note?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        NoteActivity.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    void NotesAlreadyExistDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Note already exist")
                .setMessage("Do you want to replace previous Note ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dbHelpers.updateData(checkedContacts, title, description, "Sd card link",
                                mHelpers.getCurrentDateandTime());
                        NoteActivity.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}