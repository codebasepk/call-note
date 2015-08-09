package com.byteshaft.callnote;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.byteshaft.callnote.IncomingCallListener.Note;

public class MainActivity extends ActionBarActivity implements Switch.OnCheckedChangeListener,
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private Helpers mHelpers;
    private DataBaseHelpers mDbHelpers;
    private ArrayList<String> arrayList;
    private ListView listView;
    private TextView textViewTitle;
    private OverlayHelpers mOverlayHelpers;
    private Switch mToggleSwitch;
    private ArrayAdapter<String> mModeAdapter;
    private DataBaseHelpers dataBaseHelpers;
    private final String mSku = "premiumupgrade";
    private boolean isServiceBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#689F39")));
        textViewTitle = (TextView) findViewById(R.id.title);
        mHelpers = new Helpers(getApplicationContext());
        dataBaseHelpers = new DataBaseHelpers(getApplicationContext());
        mToggleSwitch = (Switch) findViewById(R.id.aSwitch);
        mDbHelpers = new DataBaseHelpers(getApplicationContext());
        mToggleSwitch.setOnCheckedChangeListener(this);
        mOverlayHelpers = new OverlayHelpers(getApplicationContext());
        if (dataBaseHelpers.isEmpty()) {
            showNoNoteFoundDialog();
        }
        if (!AppGlobals.isPremium()) {
            AdView adView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder()
                    .setRequestAgent("android_studio:ad_template").build();
            adView.loadAd(adRequest);
        }
    }

    private void showNoNoteFoundDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Welcome!");
        builder.setMessage("Would you like to add your first note?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), NoteActivity.class);
                startActivity(intent);
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        mToggleSwitch.setChecked(mHelpers.isServiceSettingEnabled());
        if (mHelpers.isServiceSettingEnabled()) {
            mToggleSwitch.setText("Notes Active");
            mToggleSwitch.setTextColor(Color.BLACK);
        } else {
            mToggleSwitch.setText("All Notes OFF");
            mToggleSwitch.setTextColor(Color.RED);
        }
        arrayList = mDbHelpers.getAllPresentNotes();
        mModeAdapter = new NotesArrayList(this, R.layout.row, arrayList);
        listView = (ListView) findViewById(R.id.listView_main);
        listView.setAdapter(mModeAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        listView.setDivider(null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        dismissUpgradeDialog();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_addNote:
                if (dataBaseHelpers.getNotesCount() >= 3 && !AppGlobals.isPremium()) {
                    String message = "You cannot add more than 3 Notes in free version " +
                            "Upgrade to premium";
                    String title = "Notes limit";
                    showFreeLimitExceededDialog(title, message);
//                    mHelpers.showUpgradeDialog(MainActivity.this, title, message);
                } else {
                    startActivity(new Intent(this, NoteActivity.class));
                }
                break;
            case R.id.upgrade_button:
                showUpgradeDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#689F39")));
        MenuItem item = menu.findItem(R.id.upgrade_button);
        if (AppGlobals.isPremium()) {
            item.setVisible(false);
        } else {
            item.setVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (isChecked) {
            startService(new Intent(this, OverlayService.class));
            mToggleSwitch.setText("Notes Active");
            mToggleSwitch.setTextColor(Color.BLACK);
        } else {
            stopService(new Intent(this, OverlayService.class));
            mToggleSwitch.setText("All Notes OFF");
            mToggleSwitch.setTextColor(Color.RED);
        }
        mHelpers.saveServiceStateEnabled(isChecked);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra("note_title", arrayList.get(position));
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
        System.out.println(parent.getItemAtPosition(position));
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Delete");
        builder.setMessage("Are you sure?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                DataBaseHelpers dataBaseHelpers = new DataBaseHelpers(getApplicationContext());
                dataBaseHelpers.deleteItem(SqliteHelpers.NOTES_COLUMN, (String)
                        parent.getItemAtPosition(position));
                mModeAdapter.remove(mModeAdapter.getItem(position));
                mModeAdapter.notifyDataSetChanged();
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

    private String getDirectionThumbnail(String title) {
        String uriBase = "android.resource://com.byteshaft.callnote/";
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int noteShowPreference = preferences.getInt(title, Note.TURN_OFF);
        if (AppGlobals.isPremium()) {
            return getDirectionIconForPremium(uriBase, noteShowPreference);
        } else {
            return getDirectionIconForTrial(uriBase, noteShowPreference);
        }
    }

    private String getDirectionIconForTrial(String uriBase, int notePreference) {
        switch (notePreference) {
            case Note.SHOW_INCOMING_CALL:
                return uriBase + R.drawable.incoming_call;
            case Note.TURN_OFF:
                return uriBase + R.drawable.off;
            default:
                return uriBase + R.drawable.off;
        }
    }

    @NonNull
    private String getDirectionIconForPremium(String uriBase, int noteShowPreference) {
        switch (noteShowPreference) {
            case Note.SHOW_INCOMING_CALL:
                return uriBase + R.drawable.incoming_call;
            case Note.SHOW_OUTGOING_CALL:
                return uriBase + R.drawable.outgoing_call;
            case Note.SHOW_INCOMING_OUTGOING:
                return uriBase + R.drawable.incoming_outgoing_call;
            default:
                return uriBase + R.drawable.off;
        }
    }

    static class ViewHolder {
        public TextView title;
        public ImageView character;
        public ImageView direction;
    }

    class NotesArrayList extends ArrayAdapter<String> {

        public NotesArrayList(Context context, int resource, ArrayList<String> videos) {
            super(context, resource, videos);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(R.layout.row, parent, false);
                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.FilePath);
                holder.character = (ImageView) convertView.findViewById(R.id.Thumbnail);
                holder.direction = (ImageView) convertView.findViewById(R.id.note_direction);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            String title = arrayList.get(position);
            holder.title.setText(title);
            holder.character.setImageURI(Uri.parse(mDbHelpers.getIconLinkForNote(title)));
            holder.direction.setImageURI(Uri.parse(getDirectionThumbnail(title)));
            return convertView;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 4002) {
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");

            if (resultCode == RESULT_OK) {
                try {
                    JSONObject jo = new JSONObject(purchaseData);
                    String sku = jo.getString("productId");
                    if (sku.equals(mSku)) {
                        AppGlobals.enablePremium(true);
                        Toast.makeText(
                                getApplicationContext(),
                                "You have bought the premium version",
                                Toast.LENGTH_LONG).show();
                        disconnectionPayService();
                        // Unlock and restart the activity
                        recreate();
                    }

                }
                catch (JSONException e) {
                    disconnectionPayService();
                    Toast.makeText(
                            getApplicationContext(),
                            "Failed to parse purchase data.",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            } else {
                disconnectionPayService();
            }
        }
    }

    private void disconnectionPayService() {
        if (mService != null && isServiceBound) {
            unbindService(mServiceConn);
        }
        isServiceBound = false;
    }

    private IInAppBillingService mService;

    private ServiceConnection mServiceConn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
            try {
                Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(),
                        mSku, "inapp", "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
                PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                if (buyIntentBundle.getInt("RESPONSE_CODE") == 0) {
                    startIntentSenderForResult(
                            pendingIntent.getIntentSender(), 4002, new Intent(), 0, 0, 0
                    );
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            "Error Code: " + buyIntentBundle.getInt("RESPONSE_CODE"),
                            Toast.LENGTH_SHORT).show();
                }
            } catch (RemoteException | IntentSender.SendIntentException e) {
                disconnectionPayService();
                e.printStackTrace();
            }
        }
    };

    private WindowManager sWindowManager;
    private View sUpgradeDialog;
    private boolean isShown;

    private void showUpgradeDialog() {
        LayoutInflater inflater = getLayoutInflater();
        sUpgradeDialog = inflater.inflate(R.layout.upgrade_dialog, null);
        ImageButton yesButton = (ImageButton) sUpgradeDialog.findViewById(R.id.upgrade_button_yes);
        ImageButton noButton = (ImageButton) sUpgradeDialog.findViewById(R.id.upgrade_button_no);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
                serviceIntent.setPackage("com.android.vending");
                bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
                isServiceBound = true;
                dismissUpgradeDialog();
            }
        });
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissUpgradeDialog();
            }
        });
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.format = PixelFormat.TRANSPARENT;
        sWindowManager = (WindowManager)
                AppGlobals.getContext().getSystemService(Context.WINDOW_SERVICE);
        sWindowManager.addView(sUpgradeDialog, params);
        isShown = true;
    }

    private void dismissUpgradeDialog() {
        if (isShown) {
            sWindowManager.removeView(sUpgradeDialog);
        }
        isShown = false;
    }

    private void showFreeLimitExceededDialog(String title, String DialogMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(DialogMessage);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
                serviceIntent.setPackage("com.android.vending");
                bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
                isServiceBound = true;
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
    }
}