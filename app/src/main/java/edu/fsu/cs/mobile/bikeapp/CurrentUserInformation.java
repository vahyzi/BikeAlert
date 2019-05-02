package edu.fsu.cs.mobile.bikeapp;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CurrentUserInformation extends AppCompatActivity{
    ImageView profilePicture;
    TextView email;
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    InviteRecycler pendingFriendsList;

    private FirebaseFirestore db;
    //private FirestoreRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    LinearLayoutManager linearLayoutManager;
    List<String> temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_user_information);

        profilePicture = findViewById(R.id.currentUserProfilePicture);
        email = findViewById(R.id.currentUserEmail);
        email.setText(user.getEmail());

        getSupportActionBar().setTitle("My Profile");
        // ---- Top and Bottom NavBar colors ---- //
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        ButterKnife.bind(this);
        getUserList();
    }

    private void getUserList() {
        // Create a query against the collection to see pending invites

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String email = user.getEmail();
        // OnLocationListener -
        db.collection("riders")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    //DocumentReference emailRef = db.document(email).child(friends);

                                    String riderEmailStr = document.getId();
                                    if(user.getEmail().equals(document.getId())) {
                                        temp = (List<String>) document.get("pendingInvites");
                                        RecyclerView recyclerView = findViewById(R.id.my_recycler_view);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(CurrentUserInformation.this));
                                        pendingFriendsList = new InviteRecycler(CurrentUserInformation.this, temp);
                                        recyclerView.setAdapter(pendingFriendsList);
                                    }

                                }
                            }
                        }
                    });







//        Query query = db.collection("riders").document(user.getEmail()).collection("pendingInvites");
//                //whereEqualTo(user.getEmail(), "pendingInvites").orderBy("pendingInvites", Query.Direction.ASCENDING);
//
//        FirestoreRecyclerOptions<Rider> response = new FirestoreRecyclerOptions.Builder<Rider>()
//                .setQuery(query, Rider.class)
//                .build();
//
//        adapter = new FirestoreRecyclerAdapter<Rider, CurrentUserInformation.RiderHolder>(response) {
//            @Override
//            public void onBindViewHolder(CurrentUserInformation.RiderHolder holder, int position, final Rider model) {
//                holder.profilePicture.setImageResource(R.drawable.ic_android_black_24dp);
//                holder.userEmail.setText(model.getEmail());
//
//                holder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        // ---- Click Listener for Users Adapter ---- //
//                        Log.d("SelectedRider", "" + model);
//                        Intent userInfoIntent = new Intent (CurrentUserInformation.this, UserInformation.class);
//                        userInfoIntent.putExtra("RiderInfo", model);
//                        startActivity(userInfoIntent);
//                    }
//                });
//            }
//
//            @Override
//            public CurrentUserInformation.RiderHolder onCreateViewHolder(ViewGroup group, int i) {
//                View view = LayoutInflater.from(group.getContext())
//                        .inflate(R.layout.users_display_layout, group, false);
//
//                return new CurrentUserInformation.RiderHolder(view);
//            }
//
//            @Override
//            public void onError(FirebaseFirestoreException e) {
//                Log.e("error", e.getMessage());
//            }
//        };
//
//        adapter.notifyDataSetChanged();
//        pendingFriendsList.setAdapter(adapter);
    }

    public class RiderHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.profilePicture)
        ImageView profilePicture;

        @BindView(R.id.riderEmail)
        TextView userEmail;

        public RiderHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        adapter.startListening();
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        adapter.stopListening();
//    }

}
