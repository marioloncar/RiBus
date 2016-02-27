package com.jamaco.ribus;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.jamaco.ribus.tabs_timetable.ViewPagerAdapter;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


public class Timetable extends AppCompatActivity {
    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    String num, dir;
    CharSequence Titles[] = {"Workday","Saturday", "Sunday & Holidays"};
    int Numboftabs = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timetable);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        num = extras.getString("lineNum");
        dir = extras.getString("direction");


        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter =  new ViewPagerAdapter(getSupportFragmentManager(),Titles,Numboftabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        //check day of the week
        List<String> holidays = Arrays.asList("01.01.", "06.01.", "06.04.", "01.05.", "04.06.", "22.06.", "25.06.", "05.08.", "15.08.", "08.10.", "25.12.", "26.12.");

        //Take today date
        Date todayDateAndTime = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.");
        String date = dateFormat.format(todayDateAndTime);

        int dayNumber = 0;

        if (holidays.contains(date)){
            pager.setCurrentItem(2);
        } else {
            Calendar calendar = new GregorianCalendar();
            dayNumber = calendar.get(Calendar.DAY_OF_WEEK);
        }

        switch (dayNumber){
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                pager.setCurrentItem(0);
                break;
            case 7:
                pager.setCurrentItem(1);
                break;
            case 1:
               pager.setCurrentItem(2);
                break;
            default:
                System.out.println("Error with fetching day number.");
        }

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
             case R.id.time:
                Intent depart = new Intent(this, Departures.class);
                 depart.putExtra("lineNum", num);
                 depart.putExtra("direction", dir);
                 startActivity(depart);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.departures, menu);
        return super.onCreateOptionsMenu(menu);
    }
}