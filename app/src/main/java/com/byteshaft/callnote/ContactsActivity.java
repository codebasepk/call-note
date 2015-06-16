package com.byteshaft.callnote;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;

import com.android.ex.chips.BaseRecipientAdapter;
import com.android.ex.chips.RecipientEditTextView;
import com.android.ex.chips.recipientchip.DrawableRecipientChip;


public class ContactsActivity extends ActionBarActivity {

    RecipientEditTextView phoneRetv;
    DrawableRecipientChip[] chips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        EditText editText = (EditText) findViewById(R.id.phone_retv);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneRetv = (RecipientEditTextView) findViewById(R.id.phone_retv);
                phoneRetv.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
                phoneRetv.setAdapter(new BaseRecipientAdapter(BaseRecipientAdapter.QUERY_TYPE_PHONE,
                        getApplicationContext()));
                chips = phoneRetv.getSortedRecipients();
            }
        });
    }
}
