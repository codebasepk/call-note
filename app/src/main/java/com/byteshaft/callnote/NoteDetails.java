package com.byteshaft.callnote;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


public class
        NoteDetails extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_note_details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
}
