package edu.fsu.cs.mobile.bikeapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddBikeInfo extends AppCompatActivity {

    private EditText make;
    private EditText model;
    private Spinner type;
    private EditText color;
    private Spinner wheel_size;
    private EditText tire_width;
    private Spinner valve_spinner;

    private Button Submit;
    private Button Reset;

    boolean formerror = false;

    private List<String> friendsList = new ArrayList<String>();
    private List<String> pendingList = new ArrayList<String>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bike_info);

        make = findViewById(R.id.make);
        model = findViewById(R.id.model);
        type = findViewById(R.id.type_spinner);
        color = findViewById(R.id.color);
        wheel_size = findViewById(R.id.size_spinner);
        tire_width = findViewById(R.id.tire_width);
        valve_spinner = findViewById(R.id.valve_spinner);
        Submit = findViewById(R.id.submit);
        Reset = findViewById(R.id.reset);

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkData();
            }
        });


    }

    void checkData() {

        if (make.getText().toString().equals("")) {
            make.setError("Missing make");
            formerror = true;
        }

        if (model.getText().toString().equals("")) {
            model.setError("Missing model");
            formerror = true;
        }

        if (color.getText().toString().equals("")) {
            color.setError("Missing color");
            formerror = true;
        }

        if (tire_width.getText().toString().equals("")) {
            tire_width.setError("Missing tire width");
            formerror = true;
        }
        if (formerror) {

        } else {
            Intent myIntent = new Intent(AddBikeInfo.this, MapsActivity.class);

            String Make = make.getText().toString();
            String Model = model.getText().toString();
            String Type = type.getSelectedItem().toString();
            String Color = color.getText().toString();
            String Wheel = wheel_size.getSelectedItem().toString();
            String Tire = tire_width.getText().toString();
            String Valve = valve_spinner.getSelectedItem().toString();

            final FirebaseFirestore db = FirebaseFirestore.getInstance();

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String email = user.getEmail();
            // OnLocationListener -
            CollectionReference riderRef = db.collection("riders");
            DocumentReference docRef = riderRef.document(email);
            Bike bike = new Bike(Make, Model, Type, Color, Wheel, Tire, Valve);


            final Rider rider = Rider.generateRider(user, new GeoPoint(1, 1), bike, friendsList, pendingList);


            docRef.set(rider);

//            db.collection("riders")
//                    .get()
//                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            if (task.isSuccessful()) {
//                                for (QueryDocumentSnapshot document : task.getResult()) {
//                                    //DocumentReference emailRef = db.document(email).child(friends);
//                                    Log.d("RidersRef", document.getId() + " => " + document.getData());
//                                    String riderEmailStr = document.getId();
//                                    HashMap<String,Boolean> temp = (HashMap<String,Boolean>) document.get("invites");
//                                    temp.put(FirebaseAuth.getInstance().getCurrentUser().getEmail(),false);
//                                    db.collection("riders").document(document.getId())
//                                            .update("invites", temp);
//                                    invites.put(riderEmailStr, false);
//                                }
//
//                                db.collection("riders").document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
//                                        .update("invites", invites);
//                            }
//                        }
//                    });
            startActivity(myIntent);
        }
    }
}