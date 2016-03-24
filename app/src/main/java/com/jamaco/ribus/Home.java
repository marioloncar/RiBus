package com.jamaco.ribus;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.jamaco.ribus.database.DatabaseAdapter;
import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;


public class Home extends AppCompatActivity {
    DatabaseAdapter helper;
    String busname, workday1, workday2, saturday1, saturday2, sunday1, sunday2, wd1notice, wd2notice, sat1notice, sat2notice, sun1notice, sun2notice;
    ImageButton timetable, stations;
    TextView ttimetable, sstations;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        timetable = (ImageButton) findViewById(R.id.ibTimetable);
        ttimetable = (TextView) findViewById(R.id.tvTimetable);
        stations = (ImageButton) findViewById(R.id.ibStations);
        sstations = (TextView) findViewById(R.id.tvStations);

        timetable.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             Intent openLines = new Intent("com.jamaco.ribus.LINES");
                                             startActivity(openLines);
                                         }
                                     }
        );

        stations.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent openStations = new Intent("com.jamaco.ribus.STATIONS");
                                            startActivity(openStations);
                                        }
                                    }

        );
    }

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


    public void showConnectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("No internet connection");
        builder.setMessage("Please check your internet connection and try again.");
        builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!isNetworkAvailable()) {
                    showConnectionDialog();
                } else {
                    dialog.dismiss();
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            }
        });

        builder.setNeutralButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private boolean isNetworkAvailable() {
        // Using ConnectivityManager to check for Network Connection
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onResume() {
        super.onResume();

        helper = new DatabaseAdapter(this);

        if (helper.isEmpty()) {
            if (!isNetworkAvailable()) {
                showConnectionDialog();
            } else {
                //first download
                progress = new ProgressDialog(Home.this);
                progress.setIndeterminate(true);
                progress.setMessage("Retrieving and storing data...");
                progress.setCancelable(false);
                progress.show();
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

                                helper.insertData(busname, workday1, wd1notice, workday2, wd2notice, saturday1, sat1notice, saturday2, sat2notice, sunday1, sun1notice, sunday2, sun2notice);
                            }
                        }
                        progress.dismiss();
                    }
                });
            }
        } else {
            //update database
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

    }

}
