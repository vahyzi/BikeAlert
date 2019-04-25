package edu.fsu.cs.mobile.bikeapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FindFriends extends AppCompatActivity {
    private ListView riderList;
    private List<String> friends = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        riderList = findViewById(R.id.find_friends_recycler_list);
        final ArrayList<Rider> riderArrayList = new ArrayList<Rider>();
        final RidersAdapter ridersAdapter = new RidersAdapter(this, riderArrayList);
        riderList.setAdapter(ridersAdapter);


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

                                friends.add(riderEmailStr);

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


                                    bike = new Bike(bikeMake,bikeModel,bikeType,bikeColor,bikeSize,bikeWidth,bikeValve);

                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                                Rider rider = new Rider(document.getId(),time,location,bike, friends);

                                ridersAdapter.add(rider);
                            }
                        } else {
                            Log.d("RidersRef", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
