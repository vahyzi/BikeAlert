package edu.fsu.cs.mobile.bikeapp;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AllUsers extends AppCompatActivity {

    private ListView riderList;
    private List<String> users = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        riderList = findViewById(R.id.all_users_recycler_list);
        final ArrayList<Rider> riderArrayList = new ArrayList<Rider>();
        final RidersAdapter ridersAdapter = new RidersAdapter(this, riderArrayList);
        riderList.setAdapter(ridersAdapter);

                getSupportActionBar().setTitle("All Riders");
        // ---- Top and Bottom NavBar colors ---- //
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("riders")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("RidersRef", document.getId() + " => " + document.getData());
                                String riderEmailStr = document.getId();

                                users.add(riderEmailStr);

                                JSONObject reader = new JSONObject(document.getData());
                                Bike bike;
                                GeoPoint location;
                                Timestamp time;
                                try {
                                    JSONObject sys  = reader.getJSONObject("bike");
                                    String bikeMake = sys.getString("make");
                                    String bikeModel = sys.getString("model");
                                    String bikeType = sys.getString("type");
                                    String bikeColor = sys.getString("color");
                                    String bikeSize = sys.getString("wheel_size");
                                    String bikeValve = sys.getString("valve");
                                    String bikeWidth = sys.getString("tire_width");
                                    location = document.getGeoPoint("location");
                                    time = document.getTimestamp("timestamp");

                                    bike = new Bike(bikeMake, bikeModel, bikeType, bikeColor, bikeSize, bikeWidth, bikeValve);

                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }

                                Rider rider = new Rider(document.getId(), time, location, bike, users);
                                ridersAdapter.add(rider);
                            }

                        } else {
                            Log.d("RidersRef", "Error getting documents: ", task.getException());
                        }
                    }
                });

        // ---- Click Listener for Users Adapter ---- //
        /* UserInformation Activity displays selected users details */
        riderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Rider selectedRider = new Rider();
                selectedRider = ridersAdapter.getItem(position);
                Log.d("SelectedRider", "" + selectedRider);
                Intent userInfoIntent = new Intent (AllUsers.this, UserInformation.class);
                userInfoIntent.putExtra("RiderInfo", selectedRider);
                startActivity(userInfoIntent);
            }
        });
    }
}
