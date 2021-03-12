package com.app.dwit.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import com.app.dwit.Info.Info;
import com.app.dwit.R;
import com.app.dwit.models.Event;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, Info {

    public static Activity main;
    List<Event> events;
    boolean firstClick = true;
    Runnable runnable = () -> {
        firstClick = true;
    };
    Handler handler;
    EditText etAddress;
    ProgressBar progressBar;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        main = this;
        handler = new Handler();
        etAddress = findViewById(R.id.address);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        getEvents();

    }

    private void getEvents() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Events");
        myRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                events = new ArrayList<>();

                Log.i(TAG, "onDataChange: " + dataSnapshot);
                try {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Event event = postSnapshot.getValue(Event.class);

                        long end = Long.parseLong(event.getEndTimeInMillis());
                        long curr = System.currentTimeMillis();

                        if (end < curr) {
                            postSnapshot.getRef().removeValue();
                            Log.i(TAG, "onDataChange: Event Removed");
                            return;
                        }

                        progressBar.setVisibility(View.GONE);


                        LatLng sydney = new LatLng(Double.parseDouble(event.getLat()), Double.parseDouble(event.getLng()));
                        mMap.addMarker(new MarkerOptions().position(sydney).title(event.getTitle()));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                        events.add(event);
                    }
                    mMap.setOnMarkerClickListener(MapsActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "onDataChange: " + e);
                    Toast.makeText(MapsActivity.this, "no Event", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void addEvent(View view) {
        startActivity(new Intent(this, AddEventActivity.class));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        int markerId = Integer.parseInt(marker.getId().replace('m', '0'));
        etAddress.setText(events.get(markerId).getAddress());
        Log.i(TAG, "onMarkerClick: " + markerId);
        try {
            Log.i(TAG, "onMarkerClick: " + events.get(markerId).getTitle());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Please try reloading app", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!firstClick) {
            Intent intent = new Intent(this, EventDetailsActivity.class);
            intent.putExtra(KEY_EVENT_ID, events.get(markerId).getEventId());
            startActivity(intent);

        } else {

            Toast.makeText(this, "Click the marker again for details", Toast.LENGTH_SHORT).show();
        }
        handler.removeCallbacks(runnable);
        firstClick = false;
        handler.postDelayed(runnable, 2000);
        return false;
    }

    public void messages(View view) {
        startActivity(new Intent(this, ChatListActivity.class));
    }

    public void showSettings(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
    }
}