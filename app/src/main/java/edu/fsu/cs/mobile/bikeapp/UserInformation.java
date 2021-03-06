package edu.fsu.cs.mobile.bikeapp;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserInformation extends AppCompatActivity {
    ImageView profilePicture;
    TextView userEmail;
    Button sendFriendRequest;
    Boolean flag;
    List<String> friends = new ArrayList<String>();;
    List<String> invites = new ArrayList<String>();;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);

        getSupportActionBar().setTitle("User Information");
        // ---- Top and Bottom NavBar colors ---- //
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }


        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String email = user.getEmail();
        // OnLocationListener -
        CollectionReference riderRef = db.collection("riders");

        Intent riderInformationIntent = getIntent();
        final Rider rider = riderInformationIntent.getParcelableExtra("RiderInfo");
        userEmail = findViewById(R.id.userEmail);
        userEmail.setText(rider.getEmail());

        sendFriendRequest = findViewById(R.id.sendFriendRequest);
        sendFriendRequest.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        sendFriendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SendFriendRequest", "UserInformation(): Sending Request");

                // take my email and add to their pending list
                db.collection("riders").document(rider.getEmail()).
                        update("pendingInvites", FieldValue.arrayUnion(email));

                Toast toast = Toast.makeText(getApplicationContext(), "Friend request sent!", Toast.LENGTH_LONG);
                toast.show();

                // if declined delete it from pending
                // if accepted delete from pending and move it to friends list

                // if they are already friends do not show send friend request button
                // in AllUsers, get everyone that isnt a friend already (and ignore own email)


            }
        });

        db.collection("riders")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String riderEmailStr = document.getId();
                                if(rider.getEmail().equals(document.getId())) {
                                    friends = (List<String>) document.get("friendsList");
                                    invites = (List<String>) document.get("pendingInvites");
                                    if(friends != null)
                                    {
                                        if(friends.contains(email)) {
                                            sendFriendRequest.setVisibility(View.INVISIBLE);
                                        }
                                    }
                                    if(invites != null)
                                    {
                                        if(invites.contains(email)) {
                                            sendFriendRequest.setVisibility(View.INVISIBLE);
                                        }
                                    }
                                }
                            }
                        }
                    }
                });

    }
}
