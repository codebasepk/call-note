package com.byteshaft.callnote;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

public class MainActivity extends ActionBarActivity implements Switch.OnCheckedChangeListener
        , Button.OnClickListener {

    Button button;
    Helpers mHelpers;
    private boolean mViewCreated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHelpers = new Helpers(getApplicationContext());
        button = (Button) findViewById(R.id.buttonGet);
        Switch toggleSwitch = (Switch) findViewById(R.id.aSwitch);
        toggleSwitch.setOnCheckedChangeListener(this);
        button.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Intent intent = new Intent(getApplicationContext(), OverlayService.class);
        if (isChecked) {
            startService(intent);
        } else {
            stopService(intent);
        }
    }

    public void openActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), ContactsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(new Intent(this, ContactsActivity.class));
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_overlay:
                if (mViewCreated) {
                    OverlayHelpers.removePopupNote();
                    mViewCreated = false;
                } else {
                    OverlayHelpers.showPopupNoteForContact("+923422347000");
                    mViewCreated = true;
                }
        }
    }
}