package edu.fsu.cs.mobile.bikeapp;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class UserInformation extends AppCompatActivity {
    TextView userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);

        getSupportActionBar().setTitle("User Information");
        // ---- Top and Bottom NavBar colors ---- //
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        Intent riderInformationIntent = getIntent();
        Rider rider = riderInformationIntent.getParcelableExtra("RiderInfo");
        userEmail = findViewById(R.id.userEmail);
        userEmail.setText(rider.getEmail());
    }
}
