package edu.fsu.cs.mobile.bikeapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);
        final Activity activity = this;

        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Toast.makeText(getApplicationContext(), "Logging out " + currentFirebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
        FirebaseAuth.getInstance().signOut();
        Intent backIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(backIntent);
        activity.finish();
    }
}
