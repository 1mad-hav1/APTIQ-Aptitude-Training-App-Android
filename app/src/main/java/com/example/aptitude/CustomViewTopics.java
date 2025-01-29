package com.example.aptitude;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CustomViewTopics extends BaseAdapter {
    String[] title,id,difficulty,completed;
    private final Context context;

    public CustomViewTopics(Context applicationContext, String[] title, String[] id, String[] difficulty, String[] completed) {
        this.context = applicationContext;
        this.title = title;
        this.difficulty = difficulty;
        this.completed = completed;
        this.id = id;
    }

    @Override
    public int getCount() {
        return title.length;
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
            gridView = inflator.inflate(R.layout.activity_custom_view_topics, null);
        } else {
            gridView = (View) view;
        }

        TextView completed1 = (TextView) gridView.findViewById(R.id.completed);
        TextView title1 = (TextView) gridView.findViewById(R.id.title);
        TextView difficulty1 = (TextView) gridView.findViewById(R.id.difficulty);
        if (completed[i].equals("Yes"))
            // completed textview should be dispayed
            completed1.setVisibility(View.VISIBLE);
        else
            // competed should be hidden
            completed1.setVisibility(View.GONE);
        title1.setTextColor(Color.BLACK);
        difficulty1.setTextColor(Color.BLACK);
        title1.setText(title[i]);
        difficulty1.setText(difficulty[i]);
        SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(context);

        return gridView;
    }
}

