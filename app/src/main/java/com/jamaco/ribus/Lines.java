package com.jamaco.ribus;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jamaco.ribus.database.DatabaseAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mario on 5.4.2015..
 */
public class Lines extends AppCompatActivity {
    ListView lines;
    DatabaseAdapter helper;
    String busname, workday1, workday2, saturday1, saturday2, sunday1, sunday2, wd1notice, wd2notice, sat1notice, sat2notice, sun1notice, sun2notice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lines);

        helper = new DatabaseAdapter(this);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lines = (ListView) findViewById(R.id.lvLines);
        lines.setAdapter(new MyAdapter(this));

        if (helper.isEmpty()) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setCancelable(false);
            dialog.setTitle("Database error");
            dialog.setMessage("Your database is empty. Please check your internet connection and try again.");
            dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getApplicationContext(), Home.class);
                    startActivity(intent);
                }
            });
            dialog.show();
        }


        lines.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent(view.getContext(), Timetable.class);
                myIntent.putExtra("lineNum", getResources().getStringArray(R.array.titles)[position]);
                myIntent.putExtra("direction", getResources().getStringArray(R.array.descriptions)[position]);
                startActivity(myIntent);
            }
        });
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

    @Override
    protected void onResume() {
        super.onResume();

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
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}

class ViewHolder {
    TextView busTitle, busDesc;
    ImageView arrow;
}


class SingleRow {
    String title;
    String description;
    int image;

    SingleRow(String title, String description, int image) {
        this.title = title;
        this.description = description;
        this.image = image;
    }
}

class MyAdapter extends BaseAdapter {
    ArrayList<SingleRow> list;
    Context context;

    MyAdapter(Context c) {
        context = c;
        list = new ArrayList<SingleRow>();
        Resources res = c.getResources();
        String[] titles = res.getStringArray(R.array.titles);
        String[] descriptions = res.getStringArray(R.array.descriptions);
        int[] images = {R.mipmap.list_arrow};
        for (int i = 0; i < titles.length; i++) {
            list.add(new SingleRow(titles[i], descriptions[i], images[0]));
        }
    }

    @Override
    public int getCount() { //vraca broj elemenata u polju
        return list.size();
    }

    @Override
    public Object getItem(int position) { //vraca objekt na poziciji arg0
        return list.get(position);
    }

    @Override
    public long getItemId(int position) { //vraca ID retka - indeks polja u ovom slucaju
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) //popunjava listview
    {
        ViewHolder holder = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.single_row, parent, false); //sadrzi referencu za single_row
            holder = new ViewHolder();
            holder.busTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            holder.busDesc = (TextView) convertView.findViewById(R.id.tvDescription);
            holder.arrow = (ImageView) convertView.findViewById(R.id.ivArrow);

            convertView.setTag(holder);
        } else {
            //view is already recycled
            holder = (ViewHolder) convertView.getTag();
        }

        SingleRow temp = list.get(position);
        holder.busTitle.setText(temp.title);
        holder.busDesc.setText(temp.description);
        holder.arrow.setImageResource(temp.image);

        return convertView;
    }
}
