package edu.fsu.cs.mobile.bikeapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class RidersAdapter extends ArrayAdapter<Rider> {
    public RidersAdapter(Context context, ArrayList<Rider> riderArrayList) {
        super(context, 0, riderArrayList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Rider rider = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.users_display_layout, parent, false);
        }
        TextView email = (TextView) convertView.findViewById(R.id.riderEmail);
        email.setText(rider.email);
        return convertView;
    }
}
