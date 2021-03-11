package com.app.dwit.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dwit.Info.Info;
import com.app.dwit.R;
import com.app.dwit.activities.ChatActivity;
import com.app.dwit.models.User;

import java.util.List;


public class TypeRecyclerViewAdapter extends RecyclerView.Adapter<TypeRecyclerViewHolder> implements Info {

    private static final String TAG = "TAG";
    Context context;

    List<User> listInstances;

    public TypeRecyclerViewAdapter(Context context, List<User> listInstances) {
        this.context = context;
        this.listInstances = listInstances;
    }

    @NonNull
    @Override
    public TypeRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder: TYPE: " + viewType);
        LayoutInflater li = LayoutInflater.from(context);
        View view = li.inflate(R.layout.rv_user_detail, parent, false);
        return new TypeRecyclerViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final TypeRecyclerViewHolder holder, int position) {
        // TODO: Views PlayGround
        User user = listInstances.get(position);
        String userName = user.getFirstName() + " " + user.getLastName();
        Log.i(TAG, "onBindViewHolder: " + userName);
        Log.i(TAG, "onBindViewHolder: " + user.getUrlToImage());
        holder.tvUserName.setText(userName);
        holder.ivUserProfile.setImageURI(user.getUrlToImage());
        holder.tvUserName.setOnClickListener(v -> startChatActivity(user));
        holder.ivUserProfile.setOnClickListener(v -> startChatActivity(user));


    }

    private void startChatActivity(User user) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(KEY_TARGET_USER_ID, user.getId());
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return listInstances.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

}
