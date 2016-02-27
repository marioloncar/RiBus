package com.jamaco.ribus;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v7.app.AppCompatActivity;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by mario on 1/19/16.
 */
public class Settings extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        FragmentManager mFragmentManager = getFragmentManager();
        FragmentTransaction mFragmentTransaction = mFragmentManager
                .beginTransaction();
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PrefsFragment()).commit();
        mFragmentTransaction.commit();

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public static class PrefsFragment extends PreferenceFragment {
        Preference date;
        SwitchPreference enablePush;
        String lastUpdate;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

            date = (Preference) findPreference("date");
            enablePush = (SwitchPreference) findPreference("pushOnOff");

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Dates");
            query.getInBackground("w27AilFNTN", new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    if (e == null) {
                        lastUpdate = parseObject.getString("lastUpdate");
                    }
                    date.setSummary(lastUpdate);
                }
            });

        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}

