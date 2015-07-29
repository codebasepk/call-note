package com.byteshaft.callnote;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import contactpicker.ContactsPicker;

public class NoteActivity extends ActionBarActivity implements Spinner.OnItemSelectedListener {

    private EditText noteTitle;
    private EditText editTextNote;
    private Helpers mHelpers;
    private DataBaseHelpers mDbHelpers;
    private String imageVariable;
    private AlertDialog alert;
    private String mTitle;
    private String mNote;
    private String mId = null;
    private String mCheckedContacts = null;
    private SharedPreferences mPreferences;
    private ImageView iconImageView;
    private int spinnerState;
    private GridView mGridView;
    private int[] imageId = {
            R.drawable.character_1,
            R.drawable.character_2,
            R.drawable.character_3,
            R.drawable.character_4,
            R.drawable.character_5,
            R.drawable.character_6,
            R.drawable.character_7,
            R.drawable.character_8,
            R.drawable.character_9
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mTitle = noteTitle.getText().toString();
        mNote = editTextNote.getText().toString();
        mHelpers.putTemporaryPreferenceToPermanent();
        mCheckedContacts = getPermanentPreference();
        if (!mNote.isEmpty()) {
            if (mTitle.isEmpty()) {
                mTitle = mHelpers.getCurrentDateandTime().substring(0, 21);
            }
        }
        if (mNote.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Note is empty", Toast.LENGTH_SHORT).show();
        } else if (mCheckedContacts == null) {
            Toast.makeText(getApplicationContext(), "please select at least one contact",
                    Toast.LENGTH_SHORT).show();
        }
        if (imageVariable == null) {
            imageVariable = "android.resource://com.byteshaft.callnote/" + R.drawable.character_1;
        }
        switch (item.getItemId()) {
            case R.id.action_apply:
                if (mId != null && !mNote.isEmpty() && mCheckedContacts != null) {
                    mDbHelpers.clickUpdate(mId, mCheckedContacts, mTitle, mNote,
                            imageVariable, mHelpers.getCurrentDateandTime());
                    mHelpers.saveSpinnerState(mTitle, spinnerState);
                    mCheckedContacts = null;
                    Log.i(Helpers.LOG_TAG, "Update success");
                    this.finish();
                } else {
                    if (mDbHelpers.checkIfItemAlreadyExistInDatabase(mTitle) != null &&
                            !mNote.isEmpty() && mCheckedContacts != null) {
                        NotesAlreadyExistDialog();
                        mCheckedContacts = null;
                    } else if (mDbHelpers.checkIfItemAlreadyExistInDatabase(mTitle) == null &&
                            !mNote.isEmpty() && mCheckedContacts != null) {
                        mDbHelpers.createNewEntry(mCheckedContacts, mTitle, mNote, imageVariable,
                                mHelpers.getCurrentDateandTime());
                        mHelpers.saveSpinnerState(mTitle, spinnerState);
                        mCheckedContacts = null;
                        this.finish();
                    }
                }
                break;
            case R.id.action_share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Try ‘Call Note’, it’s really fun. \n \n " +
                        "Link: https://play.google.com/store/apps/details?id=com.fungamesmobile.callnote”";
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                break;
            case R.id.action_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        if (!mId.isEmpty()) {
                            mDbHelpers.deleteItemById(mId);
                            finish();
                        }
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
                return true;
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
        AppGlobals.setIsNoteEditModeFirst(true);
        mPreferences = AppGlobals.getSharedPreferences();
        iconImageView = (ImageView) findViewById(R.id.image_icon);
        mHelpers = new Helpers(getApplicationContext());
        mHelpers.putPermanentPreferenceToTemporary();
        mDbHelpers = new DataBaseHelpers(getApplicationContext());
        editTextNote = (EditText) findViewById(R.id.editText_create_note);
        noteTitle = (EditText) findViewById(R.id.editText_title_note);
        Spinner mSpinner = (Spinner) findViewById(R.id.note_spinner);
        mSpinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.spinner, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mTitle = noteTitle.getText().toString();
        mNote = editTextNote.getText().toString();
        if (getIntent().getExtras() != null) {
            String title = getIntent().getExtras().getString("note_title", "");
            noteTitle.setText(title);
            String[] detailsForThisNote = mDbHelpers.retrieveNoteDetails(title);
            iconImageView.setImageURI(Uri.parse(detailsForThisNote[4]));
            imageVariable = detailsForThisNote[4];
            mId = detailsForThisNote[0];
            editTextNote.setText(getIntent().getExtras().getString("note_data", ""));
            setTitle("Edit Note");
            mSpinner.setSelection(mHelpers.getSpinnerValue(title));
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
                Intent intent = new Intent(getApplicationContext(), ContactsPicker.class);
                intent.putExtra("note", mTitle);
                startActivity(intent);
            }
        });
    }

    public void initiateIconDialog() {
        LayoutInflater inflater = LayoutInflater.from(NoteActivity.this);
        View dialog_layout = inflater.inflate(R.layout.dialog_2, null);
        AlertDialog.Builder db = new AlertDialog.Builder(NoteActivity.this);
        alert = db.create();
        db.setView(dialog_layout);
        db.setTitle("Select character");
        alert = db.show();
        CustomGrid adapter = new CustomGrid(NoteActivity.this, imageId);
        mGridView = (GridView) dialog_layout.findViewById(R.id.grid);
        mGridView.setAdapter(adapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                setImageVariableAndCloseDialog(imageId[position]);
            }
        });
    }

    private void setImageVariableAndCloseDialog(int drawable) {
        imageVariable = "android.resource://com.byteshaft.callnote/" + drawable;
        iconImageView.setImageResource(drawable);
        iconImageView.setVisibility(View.VISIBLE);
        alert.dismiss();
    }

    @Override
    public void onBackPressed() {
        if (!mNote.equals(editTextNote.getText().toString()) || !mTitle.equals(noteTitle.getText().toString())) {
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
                        mHelpers.saveSpinnerState(mTitle, spinnerState);
                        NoteActivity.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private String getPermanentPreference() {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return mPreferences.getString("checkedContactsPrefs", null);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        spinnerState = position;

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}