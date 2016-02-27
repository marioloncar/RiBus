package com.jamaco.ribus.tabs_timetable;

import android.content.SharedPreferences;
import android.database.SQLException;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.jamaco.ribus.R;
import com.jamaco.ribus.database.DatabaseAdapter;
import com.jamaco.ribus.dynamic_listview_adapter.Section;
import com.jamaco.ribus.dynamic_listview_adapter.SectionAdapter;

import info.hoang8f.android.segmented.SegmentedGroup;

/**
 * Created by mario on 03.07.15..
 */
public class FragmentSaturday extends Fragment {
    DatabaseAdapter helper;
    String num, dir;
    String[] dir2, data;

    ListView timetable;
    TextView noBus, message;

    public FragmentSaturday(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LayoutInflater lf = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_saturday, container, false);

        TextView text = (TextView) view.findViewById(R.id.tvLine);
        message = (TextView) view.findViewById(R.id.tvMessage);
        noBus = (TextView) view.findViewById(R.id.tvNoBus);
        timetable = (ListView) view.findViewById(R.id.lvTimetable);
        SegmentedGroup segmented = (SegmentedGroup) view.findViewById(R.id.segmented);
        segmented.setTintColor(Color.parseColor("#272561"));
        final RadioButton radio1 = (RadioButton) view.findViewById(R.id.button1);
        final RadioButton radio2 = (RadioButton) view.findViewById(R.id.button2);

        helper = new DatabaseAdapter(getActivity().getApplicationContext());

        Bundle bundle = getActivity().getIntent().getExtras();
        num = bundle.getString("lineNum");
        text.setText(num);
        dir = bundle.getString("direction");
        assert dir != null;
        dir2 = dir.split("-");
        radio1.setText(dir2[0]);
        radio2.setText(dir2[1]);
        radio1.setChecked(true);
        showContent1();

        radio1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showContent1();
            }
        });
        radio2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showContent2();
            }
        });

        return view;
    }

    private void showContent1() {
        //save button index to SharedPreferences
        SharedPreferences index = PreferenceManager.getDefaultSharedPreferences(getActivity());
        index.edit().putInt("index", 0).apply();
        data = helper.getSaturday1(num);

        message.setText(helper.getSaturday1Notice(num));

        if (data != null) {

        try {
                //remove whitespace from array
                for (int i = 0; i < data.length; i++)
                    data[i] = data[i].trim();

                // 3. Create your adapter
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                        R.layout.timetable_listview_item, data);

                Section<String> sectionizer = new Section<String>() {

                    @Override
                    public String getSectionTitleForItem(String time) {
                        return time.substring(0, 2) + " h";
                    }
                };
                SectionAdapter<String> sectionAdapter = new SectionAdapter<String>(getActivity().getApplicationContext(),
                        adapter, R.layout.listview_section, R.id.list_item_section_text, sectionizer);

                timetable.setAdapter(sectionAdapter);
            }catch(SQLException e){
                Log.e("Error", "Cannot display data");
            }
        }
        else{
            noBus.setText("This bus does not drive on selected day");
        }
    }

    private void showContent2() {
        SharedPreferences index = PreferenceManager.getDefaultSharedPreferences(getActivity());
        index.edit().putInt("index", 1).apply();
        data = helper.getSaturday2(num);

        message.setText(helper.getSaturday2Notice(num));

        if(data != null) {

            try {
                //remove whitespace from array
                for (int i = 0; i < data.length; i++)
                    data[i] = data[i].trim();

                // 3. Create your adapter
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                        R.layout.timetable_listview_item, data);

                Section<String> sectionizer = new Section<String>() {

                    @Override
                    public String getSectionTitleForItem(String time) {
                        return time.substring(0, 2) + " h";
                    }
                };
                SectionAdapter<String> sectionAdapter = new SectionAdapter<String>(getActivity().getApplicationContext(),
                        adapter, R.layout.listview_section, R.id.list_item_section_text, sectionizer);

                timetable.setAdapter(sectionAdapter);
            } catch (SQLException e) {
                Log.e("Error", "Cannot display data");
            }
        }
        else{
            noBus.setText("This bus does not drive on selected day");
        }
    }
    }


