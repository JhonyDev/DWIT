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
import com.app.dwit.models.FriendlyMessage;
import com.app.dwit.models.Super;
import com.app.dwit.models.User;

import java.util.List;


public class TypeRecyclerViewAdapter extends RecyclerView.Adapter<TypeRecyclerViewHolder> implements Info {

    private static final String TAG = "TAG";
    Context context;
    List<Super> listInstances;
    int type;

    public TypeRecyclerViewAdapter(Context context, List<Super> listInstances, int type) {
        this.context = context;
        this.listInstances = listInstances;
        this.type = type;
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

        if (type == TYPE_USER) {
            User user = (User) listInstances.get(position);
            String userName = user.getFirstName() + " " + user.getLastName();
            holder.tvUserName.setText(userName);
            holder.ivUserProfile.setImageURI(user.getUrlToImage());
            holder.ibTouchField.setOnClickListener(v -> startChatActivity(user.getId()));
            holder.tvLastText.setVisibility(View.GONE);
            return;
        }

        if (type == TYPE_MESSAGE) {
            FriendlyMessage friendlyMessage = (FriendlyMessage) listInstances.get(position);
            Log.i(TAG, "onBindViewHolder: " + friendlyMessage.getFromUserName());
            holder.tvUserName.setText(friendlyMessage.getFromUserName());
            holder.tvUserName.setVisibility(View.VISIBLE);
            holder.tvUserName.setTextColor(context.getResources().getColor(R.color.black));
            holder.ivUserProfile.setImageURI(friendlyMessage.getFromUserProfilePic());
            holder.tvLastText.setText(friendlyMessage.getText());
            holder.ibTouchField.setOnClickListener(v -> startChatActivity(friendlyMessage.getToUser()));
        }
    }

    private void startChatActivity(String userId) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(KEY_TARGET_USER_ID, userId);
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
