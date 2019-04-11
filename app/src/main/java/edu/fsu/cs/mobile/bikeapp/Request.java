package edu.fsu.cs.mobile.bikeapp;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.GeoPoint;

public class Request {
    private String UserId;
    private Timestamp timestamp;
    private GeoPoint location;

    public String getUserId() {
        return this.UserId;
    }

    public void setUserId(String userId) {
        this.UserId = userId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }


    public Request(String userId, Timestamp timestamp, GeoPoint location) {
        this.UserId = userId;
        this.timestamp = timestamp;
        this.location = location;
    }


    public static Request generateRequest(FirebaseUser user, GeoPoint point) {
        return new Request(user.getEmail(), Timestamp.now(), point);
    }

}
