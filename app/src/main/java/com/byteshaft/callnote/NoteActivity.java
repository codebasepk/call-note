package com.byteshaft.callnote;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import contactpicker.ContactsPicker;

public class NoteActivity extends ActionBarActivity implements Spinner.OnItemSelectedListener {

    private EditText noteTitle;
    private Helpers mHelpers;
    private DataBaseHelpers mDbHelpers;
    private String imageVariable;
    private AlertDialog alert;
    private String mTitle;
    private String mId = null;
    private String mCheckedContacts;
    private SharedPreferences mPreferences;
    private ImageView iconImageView;
    private int spinnerState;
    private GridView mGridView;
    private boolean mShowTemporaryCheckedContacts;
    private boolean isStartedFresh;
    private Switch vibrationSwitch;
    private boolean vibrationValue;
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
    private int[] imageIdForFree = {
            R.drawable.character_1,
            R.drawable.character_2,
            R.drawable.character_3
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mTitle = noteTitle.getText().toString();
        if (imageVariable == null) {
            imageVariable = "android.resource://com.byteshaft.callnote/" + R.drawable.character_1;
        }
        switch (item.getItemId()) {
            case R.id.action_apply:
                if (mTitle.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "please make sure to add note text",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (isStartedFresh) {
                    DataBaseHelpers dataBaseHelpers = new DataBaseHelpers(getApplicationContext());
                    mCheckedContacts = dataBaseHelpers.getNumbersForNote(mTitle);
                }
                if (mCheckedContacts == null) {
                    Toast.makeText(getApplicationContext(), "please select at least one contact",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (mId != null) {
                    mDbHelpers.clickUpdate(mId, mCheckedContacts, mTitle,
                            imageVariable, mHelpers.getCurrentDateandTime());
                    mHelpers.saveSpinnerState(mTitle, spinnerState);
                    if (vibrationSwitch.isChecked()) {
                        vibrationValue = true;
                    } else {
                        vibrationValue = false;
                    }
                    if (AppGlobals.isPremium()) {
                        mHelpers.saveVibrationState(mTitle, vibrationValue);
                    }
                    mCheckedContacts = null;
                    Log.i(Helpers.LOG_TAG, "Update success");
                    this.finish();
                } else {
                    if (mDbHelpers.checkIfItemAlreadyExistInDatabase(mTitle) != null
                            && mCheckedContacts != null) {
                        NotesAlreadyExistDialog();
                        mCheckedContacts = null;
                    } else if (mDbHelpers.checkIfItemAlreadyExistInDatabase(mTitle) == null
                            && mCheckedContacts != null) {
                        mDbHelpers.createNewEntry(mCheckedContacts, mTitle, imageVariable,
                                mHelpers.getCurrentDateandTime());
                        vibrationValue = vibrationSwitch.isChecked();
                        if (AppGlobals.isPremium()) {
                            mHelpers.saveVibrationState(mTitle, vibrationValue);
                        }
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
            mTitle = noteTitle.getText().toString();
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
        isStartedFresh = true;
        mPreferences = AppGlobals.getSharedPreferences();
        iconImageView = (ImageView) findViewById(R.id.image_icon);
        mHelpers = new Helpers(getApplicationContext());
        mDbHelpers = new DataBaseHelpers(getApplicationContext());
        noteTitle = (EditText) findViewById(R.id.editText_title_note);
        Spinner mSpinner = (Spinner) findViewById(R.id.note_spinner);
        vibrationSwitch = (Switch) findViewById(R.id.vibrationSwitch);
        vibrationSwitch.setChecked(false);
        if (!AppGlobals.isPremium()) {
            vibrationSwitch.setVisibility(View.GONE);
        }
        mSpinner.setOnItemSelectedListener(this);
        String[] freeVersionOptions = {"Incoming Call", "Turn Off"};
        String[] premiumVersionOptions = {"Incoming Call", "Outgoing Call", "Incoming & Outgoing Call",
                "Turn Off"};
        ArrayAdapter<String> arrayAdapter;
        if (AppGlobals.isPremium()) {
            arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, premiumVersionOptions);
        } else {
            arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, freeVersionOptions);
        }
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(arrayAdapter);
        mTitle = noteTitle.getText().toString();
        if (getIntent().getExtras() != null) {
            String title = getIntent().getExtras().getString("note_title", "");
            noteTitle.setText(title);
            String[] detailsForThisNote = mDbHelpers.retrieveNoteDetails(title);
            iconImageView.setImageURI(Uri.parse(detailsForThisNote[4]));
            imageVariable = detailsForThisNote[4];
            mId = detailsForThisNote[0];
            setTitle("Edit Note");
            mSpinner.setSelection(mHelpers.getSpinnerValue(title));
            vibrationSwitch.setChecked(mHelpers.getVibrationState(title));
        }
        Button addIcon = (Button) findViewById(R.id.button_icon);
        addIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppGlobals.isPremium()) {
                    initiateIconDialog(imageId);
                } else {
                    initiateIconDialog(imageIdForFree);
                }
            }
        });
        Button attachContacts = (Button) findViewById(R.id.attach_contacts);
        attachContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ContactsPicker.class);
                intent.putExtra("note", mTitle);
                intent.putExtra("pre_checked", mCheckedContacts);
                intent.putExtra("temporary_select", mShowTemporaryCheckedContacts);
                startActivityForResult(intent, AppGlobals.REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AppGlobals.REQUEST_CODE:
                if (resultCode == AppGlobals.RESULT_OK) {
                    mShowTemporaryCheckedContacts = true;
                    isStartedFresh = false;
                    Bundle extras = data.getExtras();
                    if (extras == null) {
                        mCheckedContacts = null;
                    } else {
                        mCheckedContacts = extras.getString("selected_contacts");
                    }
                }
        }
    }

    public void initiateIconDialog(final int[] items) {
        LayoutInflater inflater = LayoutInflater.from(NoteActivity.this);
        View dialog_layout = inflater.inflate(R.layout.dialog_2, null);
        AlertDialog.Builder db = new AlertDialog.Builder(NoteActivity.this);
        alert = db.create();
        db.setView(dialog_layout);
        db.setTitle("Select character");
        alert = db.show();
        CustomGrid adapter = new CustomGrid(NoteActivity.this, items);
        mGridView = (GridView) dialog_layout.findViewById(R.id.grid);
        mGridView.setAdapter(adapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                setImageVariableAndCloseDialog(items[position]);
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
        if (!mTitle.equals(noteTitle.getText().toString())) {
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
                        mDbHelpers.updateData(mCheckedContacts, mTitle, imageVariable,
                                mHelpers.getCurrentDateandTime());
                        mHelpers.saveSpinnerState(mTitle, spinnerState);
                        NoteActivity.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        spinnerState = position;

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}