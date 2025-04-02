package com.example.aptitude;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

public class CustomViewCompany  extends BaseAdapter {
    String[] company_name;
    private final Context context;

    public CustomViewCompany(Context applicationContext, String[] company_names) {
        this.context = applicationContext;
        this.company_name = company_names;
    }
    @Override
    public int getCount() {
        return company_name.length;
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
    public View getView ( int i, View view, ViewGroup viewGroup){
        LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;
        if (view == null) {
            gridView = new View(context);
            gridView = inflator.inflate(R.layout.activity_custom_view_company, null);
        } else {
            gridView = (View) view;
        }

        TextView name = (TextView) gridView.findViewById(R.id.company_names);
        name.setText(company_name[i]);
        SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(context);

        return gridView;
    }
}
