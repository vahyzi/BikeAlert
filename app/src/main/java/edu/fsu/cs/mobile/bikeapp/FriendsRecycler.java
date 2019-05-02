package edu.fsu.cs.mobile.bikeapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

public class FriendsRecycler extends RecyclerView.Adapter<FriendsRecycler.ViewHolder> {

    private LayoutInflater mInflater;
    private InviteRecycler.ItemClickListener mClickListener;
    private List<String> mPendingInvites;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    View view;

    // data is passed into the constructor
    FriendsRecycler(Context context, List<String> pendingInvites) {
        this.mInflater = LayoutInflater.from(context);
        this.mPendingInvites = pendingInvites;
    }

    // inflates the row layout from xml when needed
    @Override
    public FriendsRecycler.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = mInflater.inflate(R.layout.users_display_layout, parent, false);
        return new FriendsRecycler.ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(FriendsRecycler.ViewHolder holder, final int position) {
        Log.d("Test",mPendingInvites.get(position));
        holder.myTextView.setText(mPendingInvites.get(position));
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mPendingInvites.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView profilePicture;
        public TextView myTextView;

        ViewHolder(View itemView) {
            super(itemView);

            profilePicture = itemView.findViewById(R.id.profilePicture);
            myTextView = itemView.findViewById(R.id.riderEmail);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }
}
