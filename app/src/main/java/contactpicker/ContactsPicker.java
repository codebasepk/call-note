package contactpicker;

import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.telephony.PhoneNumberUtils;
import android.text.Html;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.byteshaft.callnote.DataBaseHelpers;
import com.byteshaft.callnote.Helpers;
import com.byteshaft.callnote.R;

import java.util.ArrayList;
import java.util.List;

public class ContactsPicker extends ActionBarActivity {

    private ArrayAdapter<String> listAdapter;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_picker_activity);
        List<String> names = Helpers.getAllContactNames();
        List<String> numbers = Helpers.getAllContactNumbers();
        ArrayList<String> output = getFormattedListEntries(names, numbers);
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice,
                                         output);
        mListView = (ListView) findViewById(R.id.list);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mListView.setAdapter(listAdapter);

        DataBaseHelpers dataBaseHelpers = new DataBaseHelpers(getApplicationContext());
        String noteTitle;
        if (getIntent().getExtras() != null) {
            noteTitle = getIntent().getExtras().getString("note");
            ArrayList<String> numbersForNote = dataBaseHelpers.getNumberFromNote(noteTitle);
            for (int i = 0; i < mListView.getCount(); i++) {
                for (String contact : numbersForNote) {
                    if (PhoneNumberUtils.compare(numbers.get(i), contact)) {
                        mListView.setItemChecked(i, true);
                    }
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                listAdapter.getFilter().filter(newText);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.select_all:
                for (int i = 0; i < mListView.getCount(); i++) {
                    mListView.setItemChecked(i, true);
                }
                return true;
            case R.id.unselect_all:
                for (int i = 0; i < mListView.getCount(); i++) {
                    mListView.setItemChecked(i, false);
                }
                return true;
            case R.id.action_done:
                saveSelectedContacts(null);
                SparseBooleanArray array = mListView.getCheckedItemPositions();
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < mListView.getCount(); i++) {
                    if (array.get(i)) {
                        String out = (String) mListView.getAdapter().getItem(i);
                        String lines[] = out.split("\\r?\\n");
                        stringBuilder.append(lines[1]).append(",");
                    }
                }
                saveSelectedContacts(stringBuilder.toString());
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ArrayList<String> getFormattedListEntries(List<String> names, List<String> numbers) {
        ArrayList<String> entries = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            String formattedName = Html.fromHtml(names.get(i)).toString();
            String formattedNumber = Html.fromHtml(numbers.get(i)).toString();
            String result = formattedName + "\n" + formattedNumber;
            entries.add(result);
        }
        return entries;

    }

    private void saveSelectedContacts(String contacts) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().putString("checkedContactsTemp", contacts).apply();
    }
}
