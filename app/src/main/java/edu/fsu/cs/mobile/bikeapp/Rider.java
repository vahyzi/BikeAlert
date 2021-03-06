package edu.fsu.cs.mobile.bikeapp;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Rider implements Parcelable {
    public String email;
    public Timestamp timestamp;
    public GeoPoint location;


    private List<String> friendsList = new ArrayList<String>();
    private List<String> pendingList = new ArrayList<String>();

    private Bike bike;

    public Rider() {

    }

    public Rider(String email, Timestamp timestamp, GeoPoint location, Bike bike, List<String> friendsList, List<String> pendingList) {
        this.email = email;
        this.timestamp = timestamp;
        this.location = location;
        this.bike = bike;
        this.friendsList = new ArrayList<String>(friendsList);
        this.pendingList = new ArrayList<String>(pendingList);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Bike getBike() {
        return bike;
    }

    public void setBike(Bike bike) {
        this.bike = bike;
    }

    public List<String> getPendingList(){ return pendingList; }



    // Testing Rider
    public static Rider generateRider(FirebaseUser user, GeoPoint point, Bike bike, List<String> friendsList, List<String> pendingList) {
        return new Rider(user.getEmail(), Timestamp.now(), point, bike, friendsList, pendingList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Double latitude, longitude;
        latitude = getLocation().getLatitude();
        longitude = getLocation().getLongitude();

        dest.writeString(this.email);
        dest.writeParcelable(this.timestamp, flags);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeParcelable(this.bike, flags);

    }

    protected Rider(Parcel in) {
        this.email = in.readString();
        this.timestamp = in.readParcelable(Timestamp.class.getClassLoader());
        Double latitude, longitude;
        latitude = in.readDouble();
        longitude = in.readDouble();
        GeoPoint geoPoint = new GeoPoint(latitude, longitude);
        this.location = geoPoint;

        this.bike = in.readParcelable(Bike.class.getClassLoader());
    }

    public static final Creator<Rider> CREATOR = new Creator<Rider>() {
        @Override
        public Rider createFromParcel(Parcel source) {
            return new Rider(source);
        }

        @Override
        public Rider[] newArray(int size) {
            return new Rider[size];
        }
    };
}
