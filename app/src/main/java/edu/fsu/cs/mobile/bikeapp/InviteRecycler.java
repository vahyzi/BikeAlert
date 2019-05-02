package edu.fsu.cs.mobile.bikeapp;

import android.content.Context;
import android.database.Observable;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class InviteRecycler extends RecyclerView.Adapter<InviteRecycler.ViewHolder> {

    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private List<String> mPendingInvites;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    View view;

    // data is passed into the constructor
    InviteRecycler(Context context,  List<String> pendingInvites) {
        this.mInflater = LayoutInflater.from(context);
        this.mPendingInvites = pendingInvites;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = mInflater.inflate(R.layout.recycler_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d("Test",mPendingInvites.get(position));
        holder.myTextView.setText(mPendingInvites.get(position));

        holder.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("riders").document(currentUser.getEmail()).
                        update("friendsList", FieldValue.arrayUnion(mPendingInvites.get(position)));
                db.collection("riders").document(mPendingInvites.get(position)).
                        update("friendsList", FieldValue.arrayUnion(currentUser.getEmail()));
                final String email = mPendingInvites.get(position);

                mPendingInvites.remove(position);

                db.collection("riders").document(currentUser.getEmail()).
                        update("pendingInvites", mPendingInvites);

                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                db.collection("riders")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if(email.equals(document.getId())) {
                                            List<String> temp = (List<String>) document.get("pendingInvites");
                                            temp.remove(currentUser.getEmail());
                                            db.collection("riders").document(document.getId()).
                                                    update("pendingInvites", temp);

                                        }
                                    }
                                }
                            }
                        });


                db.collection("riders").document(email).
                        update("pendingInvites", FieldValue.arrayUnion(currentUser.getEmail()));

            }
        });

        holder.rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mPendingInvites.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView myTextView;
        public Button acceptButton;
        public Button rejectButton;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.inviteName);
            acceptButton = itemView.findViewById(R.id.accept_button);
            rejectButton = itemView.findViewById(R.id.reject_button);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

//    // convenience method for getting data at click position
//    String getItem(int id) {
//        return mData.get(id);
//    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}