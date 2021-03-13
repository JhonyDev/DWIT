package com.app.dwit.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dwit.Info.Info;
import com.app.dwit.R;
import com.app.dwit.adapters.TypeRecyclerViewAdapter;
import com.app.dwit.models.Event;
import com.app.dwit.models.Super;
import com.app.dwit.models.User;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EventDetailsActivity extends AppCompatActivity implements Info {

    SimpleDraweeView ivEventImage;
    TextView tvEventTitle;
    TextView tvEventAddress;
    TextView tvEventDescription;
    String eventId;
    Event event;
    RecyclerView recyclerView;
    List<Super> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        initViews();
        checkIntent();
        getEventFromId();
        initParticipants();

    }

    private void initParticipants() {
        recyclerView = findViewById(R.id.rv_participants);
        userList = new ArrayList<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("EventJoined").child(eventId);


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot s : children) {
                    User user = s.getValue(User.class);
                    Log.i(TAG, "onDataChange: " + user.getEmail());
                    userList.add(user);
                }
                TypeRecyclerViewAdapter typeRecyclerViewAdapter = new
                        TypeRecyclerViewAdapter(EventDetailsActivity.this, userList, TYPE_USER);
                typeRecyclerViewAdapter.notifyDataSetChanged();
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(EventDetailsActivity.this);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(typeRecyclerViewAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void updateViewElements() {
        ivEventImage.setImageURI(event.getImageUrl());
        tvEventTitle.setText(event.getTitle());
        tvEventAddress.setText(event.getAddress());
        tvEventDescription.setText(event.getDescription());
    }

    private void checkIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            eventId = bundle.get(KEY_EVENT_ID).toString();
        }

    }

    private void getEventFromId() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Events").child(eventId);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                event = dataSnapshot.getValue(Event.class);
                Log.i(TAG, "onDataChange: " + dataSnapshot);
                updateViewElements();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    private void initViews() {
        ivEventImage = findViewById(R.id.iv_event_image);
        tvEventTitle = findViewById(R.id.tv_event_title);
        tvEventAddress = findViewById(R.id.tv_address_time);
        tvEventDescription = findViewById(R.id.tv_description);
    }

    public void joinEvent(View view) {
        Log.i(TAG, "joinEvent: ");
        String userId = FirebaseAuth.getInstance().getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child(userId);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User value = dataSnapshot.getValue(User.class);
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database
                        .getReference("EventJoined")
                        .child(eventId)
                        .child(userId);
                myRef.setValue(value);
                initParticipants();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
}