package com.byteshaft.callnote;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class NoteActivity extends ActionBarActivity  {

    private EditText noteTitle;
    private EditText editTextNote;
    private Helpers mHelpers;
    private DataBaseHelpers mDbHelpers;
    private String imageVariable;
    private AlertDialog alert;
    private String mTitle;
    private String mNote;
    private String mId = null;
    private String[] mCheckedContacts;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mTitle = noteTitle.getText().toString();
        mNote = editTextNote.getText().toString();
        mCheckedContacts = mHelpers.getCheckedContacts();
        if (mTitle.isEmpty()) {
            mTitle = mHelpers.getCurrentDateandTime();
        }
        if (mNote.isEmpty()) {
            mNote = " ";
        }
        if (imageVariable == null) {
            imageVariable = "android.resource://com.byteshaft.callnote/" + R.drawable.character_1;
        }
        switch (item.getItemId()) {
            case R.id.action_apply:
                if (mId != null) {
                    mDbHelpers.clickUpdate(mId, mCheckedContacts, mTitle, mNote,
                                    imageVariable, mHelpers.getCurrentDateandTime());
                    Log.i(Helpers.LOG_TAG,"Update success");
                    this.finish();
                    } else {
                    if (mDbHelpers.checkIfItemAlreadyExistInDatabase(mTitle) != null) {
                        NotesAlreadyExistDialog();
                    } else if (mDbHelpers.checkIfItemAlreadyExistInDatabase(mTitle) == null) {
                        mDbHelpers.createNewEntry(mCheckedContacts, mTitle, mNote, imageVariable,
                                mHelpers.getCurrentDateandTime());
                        this.finish();
                    }
                }
                break;
            case R.id.action_share:
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody = getIntent().getExtras().getString("note_summary", "");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TITLE, getIntent().getExtras().getString("note_title", ""));
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, "Share via"));
                break;
            case R.id.action_delete:
                if (!mId.isEmpty()) {
                    mDbHelpers.deleteItemById(mId);
                    finish();
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_contacts, menu);
        if (getIntent().getExtras() != null) {
            menu.findItem(R.id.action_share).setVisible(true);
            menu.findItem(R.id.action_delete).setVisible(true);
            noteTitle.setText(getIntent().getExtras().getString("note_title", ""));
            editTextNote.setText(getIntent().getExtras().getString("note_summary", ""));
            mTitle = noteTitle.getText().toString();
            mNote = editTextNote.getText().toString();
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
        Switch noteTrigger = (Switch) findViewById(R.id.note_switch);
        mHelpers = new Helpers(getApplicationContext());
        mDbHelpers = new DataBaseHelpers(getApplicationContext());
        editTextNote = (EditText) findViewById(R.id.editText_create_note);
        noteTitle = (EditText) findViewById(R.id.editText_title_note);
        mTitle = noteTitle.getText().toString();
        mNote = editTextNote.getText().toString();
        if (getIntent().getExtras() != null) {
            String title = getIntent().getExtras().getString("note_title", "");
                    noteTitle.setText(title);
            String[] detailsForThisNote = mDbHelpers.retrieveNoteDetails(title);
            mId = detailsForThisNote[0];
            System.out.println("ID "+mId);
            editTextNote.setText(getIntent().getExtras().getString("note_data", ""));
            noteTrigger.setVisibility(View.VISIBLE);
            setTitle("Edit Note");
        }
        Button addIcon = (Button) findViewById(R.id.button_icon);
        addIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initiateIconDialog();
            }
        });
        Button attachContacts = (Button) findViewById(R.id.attach_contacts);
        attachContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showContactsDialog();
            }
        });
    }

    public void showContactsDialog() {
        final LayoutInflater inflater = LayoutInflater.from(NoteActivity.this);
        View dialog_layout = inflater.inflate(R.layout.dialog, (ViewGroup) findViewById(R.id.dialogLayout));
        AlertDialog.Builder db = new AlertDialog.Builder(NoteActivity.this);
        db.setView(dialog_layout);
        db.setTitle("Select Contacts");
        db.setPositiveButton("OK", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        db.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        ListView listView = (ListView) dialog_layout.findViewById(R.id.lv);
        final ContactsAdapter ma = new ContactsAdapter(getApplicationContext(), mTitle);
        listView.setAdapter(ma);
        Button checkAll = (Button) dialog_layout.findViewById(R.id.button_checkall);
        checkAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                preferences.edit().putString("check", "checked_all").commit();
                ma.notifyDataSetChanged();
            }
        });
        Button uncheckAll = (Button) dialog_layout.findViewById(R.id.button_uncheck_all);
        uncheckAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                preferences.edit().putString("check", "unchecked_all").commit();
                ma.notifyDataSetChanged();
            }
        });
        db.show();
    }

    public void initiateIconDialog() {
        LayoutInflater inflater = LayoutInflater.from(NoteActivity.this);
        View dialog_layout = inflater.inflate(R.layout.dialog_2, (ViewGroup) findViewById(R.id.dialogLayout_2));
        final AlertDialog.Builder db = new AlertDialog.Builder(NoteActivity.this);
        alert = db.create();
        db.setView(dialog_layout);
        db.setTitle("Add Icon");
        alert = db.show();
        ImageView imageView1 = (ImageView) dialog_layout.findViewById(R.id.character_1);
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageVariable = "android.resource://com.byteshaft.callnote/" + R.drawable.character_1;
                alert.dismiss();
            }
        });
        ImageView imageView2 = (ImageView) dialog_layout.findViewById(R.id.character_2);
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageVariable = "android.resource://com.byteshaft.callnote/" + R.drawable.character_2;
                alert.dismiss();
            }
        });
        ImageView imageView3 = (ImageView) dialog_layout.findViewById(R.id.character_3);
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageVariable = "android.resource://com.byteshaft.callnote/" + R.drawable.character_3;
                alert.dismiss();
            }
        });
    }

        @Override
        public void onBackPressed() {
            if (!mNote.equals(editTextNote.getText().toString()) || !mTitle.equals(noteTitle.getText().toString())){
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
                        mDbHelpers.updateData(mCheckedContacts, mTitle, mNote, imageVariable,
                                mHelpers.getCurrentDateandTime());
                        NoteActivity.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}