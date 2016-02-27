package com.jamaco.ribus.tabs_departures;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by mario on 03.10.15..
 */

public class ViewPagerAdapterDepartures  extends FragmentStatePagerAdapter {
    CharSequence TitlesDepart[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabsDepart; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapterDepartures(FragmentManager fm,CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);

        this.TitlesDepart = mTitles;
        this.NumbOfTabsDepart = mNumbOfTabsumb;
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position)
    {

        if (position == 0) // if the position is 0 we are returning the First tab
        {
            FragmentFrom1 from1 = new FragmentFrom1();
            return from1;
        }
        if (position == 1)
        {
            FragmentFrom2 from2 = new FragmentFrom2();
            return from2;
        }
        return null;
    }

    // This method return the titles for the Tabs in the Tab Strip
    @Override
    public CharSequence getPageTitle(int position) {
        return TitlesDepart[position];
    }

    // This method return the Number of tabs for the tabs Strip
    @Override
    public int getCount() {
        return NumbOfTabsDepart;
    }
}
