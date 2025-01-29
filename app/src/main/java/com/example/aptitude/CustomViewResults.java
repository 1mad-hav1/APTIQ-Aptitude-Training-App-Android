package com.example.aptitude;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

public class CustomViewResults extends BaseAdapter {
    String[] testname, testdate, marks_scored, pass_fail, id;
    private final Context context;
    public CustomViewResults(Context applicationContext, String[] id, String[] testname, String[] testdate, String[] marksScored, String[] passFail) {
        this.context = applicationContext;
        this.testname = testname;
        this.testdate = testdate;
        this.marks_scored = marksScored;
        this.pass_fail = passFail;
        this.id = id;
    }

    @Override
    public int getCount() {
        return testname.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;
        if (view == null) {
            gridView = new View(context);
            gridView = inflator.inflate(R.layout.activity_custom_view_results, null);
        } else {
            gridView = (View) view;
        }

        TextView test_name = (TextView) gridView.findViewById(R.id.testname);
        TextView test_date = (TextView) gridView.findViewById(R.id.testdate);
        TextView marks = (TextView) gridView.findViewById(R.id.mark);
        TextView passfail = (TextView) gridView.findViewById(R.id.pass_fail);
        test_name.setTextColor(Color.BLACK);
        test_date.setTextColor(Color.BLACK);
        marks.setTextColor(Color.BLACK);
        passfail.setTextColor(Color.BLACK);
        test_name.setText(testname[i]);
        test_date.setText(testdate[i]);
        marks.setText(marks_scored[i]);
        passfail.setText(pass_fail[i]);
        SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(context);

        return gridView;
    }
}