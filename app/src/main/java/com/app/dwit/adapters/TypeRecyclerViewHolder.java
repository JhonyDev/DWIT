package com.app.dwit.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dwit.R;
import com.facebook.drawee.view.SimpleDraweeView;

public class TypeRecyclerViewHolder extends RecyclerView.ViewHolder {

    SimpleDraweeView ivUserProfile;
    TextView tvUserName;

    public TypeRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);

        ivUserProfile = itemView.findViewById(R.id.iv_user_profile);
        tvUserName = itemView.findViewById(R.id.tv_username);
    }

}


