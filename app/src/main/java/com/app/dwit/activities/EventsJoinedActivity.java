package com.app.dwit.activities;

import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EventsJoinedActivity extends AppCompatActivity implements Info {

    RecyclerView rvEventsJoined;
    TypeRecyclerViewAdapter typeRecyclerViewAdapter;

    String userId;
    List<Super> eventList;
    List<String> eventIdList;
    TextView tvUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_joined);

        tvUserName = findViewById(R.id.user_name);
        tvUserName.setText("");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userId = bundle.get(KEY_TARGET_USER_ID).toString();
            String username = bundle.get("USERNAME").toString();
            tvUserName.setText(username);
        }
        initRecyclerView();

        initOrderList();

    }

    private void initOrderList() {
        eventIdList = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("EventJoined");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        User user = childSnapshot.getValue(User.class);
                        assert user != null;
                        String userName = user.getFirstName() + " " + user.getLastName();
                        tvUserName.setText(userName);
                        Log.i(TAG, "onDataChange: " + user.getId());
                        Log.i(TAG, "onDataChange: " + userId);
                        Log.i(TAG, "onDataChange: .......................");
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
                    typeRecyclerViewAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
        }
    }

    private void initRecyclerView() {
        eventList = new ArrayList<>();
        rvEventsJoined = findViewById(R.id.rv_events_joined);
        typeRecyclerViewAdapter = new
                TypeRecyclerViewAdapter(EventsJoinedActivity.this, eventList, TYPE_EVENTS);
        typeRecyclerViewAdapter.notifyDataSetChanged();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvEventsJoined.setLayoutManager(linearLayoutManager);
        rvEventsJoined.setAdapter(typeRecyclerViewAdapter);
    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(KEY_TARGET_USER_ID, userId);
        startActivity(intent);

    }

}