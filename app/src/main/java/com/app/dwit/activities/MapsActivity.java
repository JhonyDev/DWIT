package com.app.dwit.activities;

import android.app.Activity;
import android.app.Notification;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentActivity;

import com.app.dwit.Info.Info;
import com.app.dwit.R;
import com.app.dwit.models.Event;
import com.app.dwit.models.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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
    List<String> eventIdList;
    String userId;
    List<Event> eventList;
    NotificationManagerCompat notificationManagerCompat;
    boolean isFirstNotification = true;
    ProgressBar pbSearch;
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
        pbSearch = findViewById(R.id.pb_search);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        initNotification();


    }

    private void performSearch() {
        Log.i(TAG, "performSearch: ");
        String address = etAddress.getText().toString();
        if (address.equals("")) {
            Toast.makeText(main, "Please type a location", Toast.LENGTH_SHORT).show();
            return;
        }
        double lat = getLat(address);
        double lng = getLng(address);
        LatLng latLng = new LatLng(lat, lng);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        new Handler().postDelayed(() -> {
            pbSearch.setVisibility(View.GONE);
            mMap.animateCamera(CameraUpdateFactory.zoomTo(9), 1500, null);
        }, 500);
    }

    private double getLat(String addressMain) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(addressMain, 1);
            Log.i(TAG, "onCreate: " + addresses.get(0).getLatitude() + " " + addresses.get(0).getLongitude());
            for (Address address : addresses) {
                return address.getLatitude();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private double getLng(String addressMain) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(addressMain, 1);
            Log.i(TAG, "onCreate: " + addresses.get(0).getLatitude() + " " + addresses.get(0).getLongitude());
            for (Address address : addresses) {
                return address.getLongitude();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }


    private void initNotification() {
        initOrderList();
    }

    private void initOrderList() {
        eventIdList = new ArrayList<>();
        eventList = new ArrayList<>();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("EventJoined");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        User user = childSnapshot.getValue(User.class);
                        if (user.getId().equals(userId)) {
                            Log.i(TAG, "onDataChange: true ");
                            eventIdList.add(snapshot.getRef().getKey());
                            break;
                        }
                    }
                }
                initEventList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    private void initEventList() {
        Log.i(TAG, "initEventList: " + eventIdList);
        for (String eventId : eventIdList) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference().child("Events").child(eventId);
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Event event = dataSnapshot.getValue(Event.class);
                    eventList.add(event);
                    initNotification(event);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
        }
    }

    private void initNotification(Event event) {
        String timeStamp = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
        Log.i(TAG, "initNotification: " + timeStamp);
        Log.i(TAG, "initNotification: " + event.getDate());
        int toDayDay = Integer.parseInt(splitString(timeStamp, 0));
        int toDayMonth = Integer.parseInt(splitString(timeStamp, 1));
        int toDayYear = Integer.parseInt(splitString(timeStamp, 2));
        String eventDate = event.getDate();
        int eventDay = Integer.parseInt(splitString(eventDate, 0));
        int eventMonth = Integer.parseInt(splitString(eventDate, 1));
        int eventYear = Integer.parseInt(splitString(eventDate, 2));
        if (toDayDay == eventDay && toDayMonth == eventMonth && toDayYear == eventYear) {
            if (isFirstNotification) {
                isFirstNotification = false;
                new Handler().postDelayed(this::sendNotification, 10 * 1000);
            }
        }
    }

    private void sendNotification() {
        notificationManagerCompat = NotificationManagerCompat.from(this);

        Log.i(TAG, "generateNotification: ");

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("DWIT")
                .setContentText("You Have an upcoming Event today")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        notificationManagerCompat.notify(1, notification);
    }

    private String splitString(String string, int t) {
        String[] parts = string.split("/");
        return parts[t];
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera

        etAddress.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                pbSearch.setVisibility(View.VISIBLE);
                return true;
            }
            return false;
        });
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
                progressBar.setVisibility(View.GONE);
                Log.i(TAG, "onDataChange: " + dataSnapshot);
                try {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Event event = postSnapshot.getValue(Event.class);
                        long end = Long.parseLong(event.getEndTimeInMillis());
                        long curr = System.currentTimeMillis();

                        if (end < curr) {
                            postSnapshot.getRef().removeValue();
                            database.getReference("EventJoined").child(event.getEventId()).removeValue();
                            return;
                        }

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
//        etAddress.setText(events.get(markerId).getAddress());
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