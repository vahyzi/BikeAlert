package edu.fsu.cs.mobile.bikeapp;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import butterknife.ButterKnife;

public class FindFriends extends AllUsers {

    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FriendsRecycler friendsList;

    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    LinearLayoutManager linearLayoutManager;
    List<String> temp;

    @Override
    public Context getUsersContext() {
        return super.getUsersContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        getSupportActionBar().setTitle("My Friends");
        // ---- Top and Bottom NavBar colors ---- //
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        ButterKnife.bind(this);
        getFriends();

    }

    private void getFriends() {
        // Create a query against the collection to see pending invites
        recyclerView = (RecyclerView) findViewById(R.id.all_users_recycler_list);
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
                                String riderEmailStr = document.getId();
                                if(user.getEmail().equals(document.getId())) {
                                    temp = (List<String>) document.get("friendsList");
                                    RecyclerView recyclerView = findViewById(R.id.all_users_recycler_list);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(getUsersContext()));
                                    friendsList = new FriendsRecycler(getUsersContext(), temp);
                                    recyclerView.setAdapter(friendsList);
                                }
                            }
                        }
                    }
                });
    }


}
