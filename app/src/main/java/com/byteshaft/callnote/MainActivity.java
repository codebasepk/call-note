package com.byteshaft.callnote;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

public class MainActivity extends ActionBarActivity implements Switch.OnCheckedChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Switch toggleSwitch = (Switch) findViewById(R.id.aSwitch);
        toggleSwitch.setOnCheckedChangeListener(this);
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
}