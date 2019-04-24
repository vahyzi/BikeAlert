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
        TextView make = (TextView) convertView.findViewById(R.id.bikeMake);
        TextView model = (TextView) convertView.findViewById(R.id.bikeModel);
        TextView type = (TextView) convertView.findViewById(R.id.bikeType);
        TextView color = (TextView) convertView.findViewById(R.id.bikeColor);
        TextView size = (TextView) convertView.findViewById(R.id.bikeSize);
        TextView valve = (TextView) convertView.findViewById(R.id.bikeValve);
        TextView width = (TextView) convertView.findViewById(R.id.bikeWidth);

        email.setText(rider.email);
        make.setText(rider.getBike().getMake());
        model.setText(rider.getBike().getModel());
        type.setText(rider.getBike().getType());
        color.setText(rider.getBike().getColor());
        size.setText(rider.getBike().getWheel_size());
        valve.setText(rider.getBike().getValve());
        width.setText(rider.getBike().getTire_width());

        return convertView;
    }
}
