package com.jamaco.ribus;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.jamaco.ribus.database.DatabaseAdapter;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import io.fabric.sdk.android.Fabric;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

/**
 * Created by mario on 24.10.15..
 */
public class AppDelegate extends android.app.Application {

    String busname, workday1, workday2, saturday1, saturday2, sunday1, sunday2, wd1notice, wd2notice, sat1notice, sat2notice, sun1notice, sun2notice;
    DatabaseAdapter helper;


    @Override
    public void onCreate() {
        super.onCreate();
        // Fabric.with(this, new Crashlytics(), new Answers());
        Parse.initialize(this, getString(R.string.parse_app_id), getString(R.string.parse_client_key));
/*
        ParseAnonymousUtils.logIn(new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.d("Find My ATM", "Anonymous login failed.");
                } else {
                    Log.d("Find My ATM", "Anonymous user logged in.");
                }
            }
        });
*/
        helper = new DatabaseAdapter(this);
        Foreground.get(this).addListener(getMyListener);
    }

    Foreground.Listener getMyListener = new Foreground.Listener() {
        @Override
        public void onBecameForeground() {

            ParseQuery<ParseObject> query = ParseQuery.getQuery("RiBusTimetable");
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> table, ParseException e) {
                    if (e == null) {
                        for (ParseObject post : table) {
                            busname = post.getString("busname");
                            saturday1 = jsonToArray(post.getJSONArray("saturday1"));
                            saturday2 = jsonToArray(post.getJSONArray("saturday2"));
                            sunday1 = jsonToArray(post.getJSONArray("sunday1"));
                            sunday2 = jsonToArray(post.getJSONArray("sunday2"));
                            workday1 = jsonToArray(post.getJSONArray("workday1"));
                            workday2 = jsonToArray(post.getJSONArray("workday2"));
                            wd1notice = post.getString("wd1notice");
                            wd2notice = post.getString("wd2notice");
                            sat1notice = post.getString("sat1notice");
                            sat2notice = post.getString("sat2notice");
                            sun1notice = post.getString("sun1notice");
                            sun2notice = post.getString("sun2notice");

                            helper.updateData(busname, workday1, wd1notice, workday2, wd2notice, saturday1, sat1notice, saturday2, sat2notice, sunday1, sun1notice, sunday2, sun2notice);
                        }
                    }
                }
            });
        }

        @Override
        public void onBecameBackground() {
        }
    };

    private String jsonToArray(JSONArray a) {
        String tmp;
        String str = " ";
        String strSeparator = ",";
        if (a != null) {
            for (int i = 0; i < a.length(); i++) {
                try {
                    tmp = a.get(i).toString();
                    str += tmp;
                    if (i < a.length() - 1) {
                        str += strSeparator;
                    }
                } catch (JSONException err) {
                    err.printStackTrace();
                }
            }
        } else {
            return null;
        }
        return str;
    }
}
