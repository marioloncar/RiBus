package com.jamaco.ribus;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.jamaco.ribus.tabs_departures.ViewPagerAdapterDepartures;

/**
 * Created by mario on 18.07.15..
 */
public class Departures extends AppCompatActivity {
    String dir;
    String[] dir2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ViewPager pager;
        ViewPagerAdapterDepartures adapter;
        SlidingTabLayout tabs;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            dir = extras.getString("direction");
        }
        assert dir != null;
        dir2 = dir.split("-");
        CharSequence Titles[] = {"From: " + dir2[0], "From: " + dir2[1]};
        int Numboftabs = 2;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.departures);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter = new ViewPagerAdapterDepartures(getSupportFragmentManager(), Titles, Numboftabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int value = prefs.getInt("index", 0);
        pager.setCurrentItem(value);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        //tabs.setDistributeEvenly(false); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return ContextCompat.getColor(getApplicationContext(), R.color.custom_blue);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
