package edu.fsu.cs.mobile.bikeapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.NavigationView;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class MapsActivity extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, OnMapReadyCallback,
        NavigationView.OnNavigationItemSelectedListener {

    private EditText mSearchText;
    private DrawerLayout drawer;
    private HashMap<String, Marker> mMarkers = new HashMap<>();
    View mapView;


    private static final float DEFAULT_ZOOM = 15f;
    private String TAG = "MapsActivity";

    private GoogleMap mMap;
    private static final String[] LOCATION_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private static final int LOCATION_PERMISSIONS_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        /*docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        document.getData()
                        Map<String, Object> data = document.getData();
                        string data.get("email");
                        data.get("bike");
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });*/

        // ---- Search Bar EditText input ---- //
        mSearchText = findViewById(R.id.input_search);
        mSearchText.setSingleLine(); // to press return key as enter key when searching

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView email = (TextView) headerView.findViewById(R.id.email);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference riderRef = db.collection("riders");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userEmail = user.getEmail();
        DocumentReference docRef = riderRef.document(userEmail);
        email.setText(userEmail);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapView = mapFragment.getView(); // set mapView for moving the currentLocation button
        if (savedInstanceState == null) {
            mapFragment.getMapAsync(this);
            navigationView.setCheckedItem(R.id.map);
        }



        final Button button = findViewById(R.id.alert_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d( "MADE IT TO onClick", "ONCLICK");
                if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Log.d( "MADE IT TO PERMISSIONS", "PERM");
                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(MapsActivity.this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    // Got last known location. In some rare situations this can be null.
                                    if (location != null) {
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        CollectionReference reqRef = db.collection("requests");
                                        Log.d( "MADE IT TO LOCATION", location.toString());

                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        String email = user.getEmail();
                                        DocumentReference docRef = reqRef.document(email);
                                        GeoPoint point = new GeoPoint(location.getLatitude(), location.getLongitude());
                                        Request req = Request.generateRequest(user, point);
                                        docRef.set(req);
                                    }
                                }
                            });
                    return;
                }
            }
        });

        db = FirebaseFirestore.getInstance();
        db.collection("requests")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        ArrayList<GeoPoint> markers = new ArrayList<GeoPoint>();
                        for (DocumentSnapshot snapshot : value.getDocuments()) {

                            GeoPoint loc = ( (GeoPoint) snapshot.get("location"));

                            markers.add(loc);
                        }
                        refreshMap(markers, false);
                    }
                });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.map:
                //switch to mapfrag
                break;
            case R.id.bikes:
                Intent intent2 = new Intent(MapsActivity.this, AddBikeInfo.class);
                startActivity(intent2);
                break;
            case R.id.settings:
                //switch to bikesfrag
                break;
            case R.id.logout:
                Intent intent4 = new Intent(MapsActivity.this, LogoutActivity.class);
                startActivity(intent4);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // ---- Where the map is prepared after getMapAsync is called ---- //
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng murpheyRepair = new LatLng(30.442342, -84.292942);
        LatLng sallyRepair = new LatLng(30.445997, -84.292942);
        LatLng tullyRepair = new LatLng(30.442235, -84.302351);
        mMap.addMarker(new MarkerOptions()
                .position(murpheyRepair)
                .title("Campus Repair Stand"));
        mMap.addMarker(new MarkerOptions()
                .position(sallyRepair)
                .title("Campus Repair Stand"));
        mMap.addMarker(new MarkerOptions()
                .position(tullyRepair)
                .title("Campus Repair Stand"));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
            /* Remove the getLocationButton to add search bar */
            // mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.setOnMyLocationClickListener(this);

            // ---- Move currentLocationButton to bottom left ---- //
            if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null){
                // Get the button view
                View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                // and next place it, on bottom right (as Google Maps app)
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                        locationButton.getLayoutParams();
                // position on right bottom
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                layoutParams.setMargins(0, 0, 30, 200);
            }

            // ---- Initialize search bar if onMapReady ---- //
            init();
        } else {
            //Toast.makeText(this, "No Permissions", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, LOCATION_PERMISSIONS, LOCATION_PERMISSIONS_REQUEST_CODE);

        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("riders")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        ArrayList<GeoPoint> markers = new ArrayList<GeoPoint>();
                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                            GeoPoint loc = ( (GeoPoint) snapshot.get("location"));
                            markers.add(loc);
                        }
                        refreshMap(markers, true);
                    }
                });

        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d( "MADE IT TO PERMISSIONS", "PERM");
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(MapsActivity.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                LatLng coordinate = new LatLng(location.getLatitude(), location.getLongitude()); //Store these lat lng values somewhere. These should be constant.
                                CameraUpdate place = CameraUpdateFactory.newLatLngZoom(
                                        coordinate, 15);
                                mMap.animateCamera(place);
                            }
                        }
                    });
            return;
        }
    }


    private void refreshMap(ArrayList<GeoPoint> markers, boolean user) {
        for (GeoPoint loc : markers) {
            double lat = loc.getLatitude();
            double lng = loc.getLongitude();
            if (user) {
                LatLng latLng = new LatLng(lat, lng);
                mMap.addMarker(new MarkerOptions()
                        .position(latLng).icon(BitmapDescriptorFactory.fromBitmap(getBitmap(R.drawable.ic_motorcycle_black_24dp))));
            } else {
                LatLng latLng = new LatLng(lat, lng);
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("SEND HELP!")
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

            }

        }
    }
    private void addStatic(ArrayList<GeoPoint> markers) {
        for (GeoPoint loc : markers) {
            double lat = loc.getLatitude();
            double lng = loc.getLongitude();

            LatLng latLng = new LatLng(lat, lng);
            mMap.addMarker(new MarkerOptions()
                    .position(latLng));
        }
    }

    /*
    * Sweet angel of stack overflow
    * https://stackoverflow.com/questions/10111073/how-to-get-a-bitmap-from-a-drawable-defined-in-a-xml
     */
    private Bitmap getBitmap(int drawableRes) {
        Drawable drawable = getResources().getDrawable(drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;
    }


    @Override
    public void onMyLocationClick(@NonNull Location location) {
        //Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    // ---- Hidden and override with in init function with mGPS image view ---- //
    @Override
    public boolean onMyLocationButtonClick() {
        //Toast.makeText(this, "Moving to current location", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    private void geoLocate() {
        Log.d(TAG, "Inside geoLocate function");
        String searchString = mSearchText.getText().toString();
        Log.d(TAG, "mSearchText.getText().toString()");

        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.e(TAG,"geoLocate: IOException " + e.getMessage());
        }

        if (list.size() > 0) {
            Address address = list.get(0);
            //Toast.makeText(this, address.toString(), Toast.LENGTH_LONG).show();

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0
            ));
        }
    }

    // ---- OnClick for map search ---- //
    private void init() {
        Log.d("Init", "Inside init function");
        // Override return key instead of next line, search
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.d("InitClicked", "Inside init clicked");
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER) {
                    // ---- Execute method for search ---- //
                    geoLocate();
                }

                return false;
            }
        });
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        // ---- Code for dropping a pin from search ---- //
        /* and Set Marker to the Map */
        MarkerOptions options = new MarkerOptions().position(latLng).title(title);
        mMap.addMarker(options);
    }
}