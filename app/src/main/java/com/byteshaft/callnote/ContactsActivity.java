package com.byteshaft.callnote;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        addAnotherNote = (Button) findViewById(R.id.add_another_note);
        attachContacts = (Button) findViewById(R.id.attach_contacts);
        attachContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });
    }
}