package com.example.chatapp;


import android.content.Context;
import android.content.Intent;
import android.net.MailTo;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {

    Context context;
    ArrayList<User> userList;

    public FriendListAdapter(Context context, ArrayList<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.friend, parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);

        holder.frndName.setText(user.getName());
        holder.frndEmail.setText(user.getEmail());
        if(!user.getImageUrl().equals("")) {
            Picasso.get().load(user.getImageUrl()).into(holder.frndImg);
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private CircleImageView frndImg;
        private TextView frndName, frndEmail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            frndImg = (CircleImageView) itemView.findViewById(R.id.frnd_img);
            frndName = (TextView) itemView.findViewById(R.id.frnd_name);
            frndEmail = (TextView) itemView.findViewById(R.id.frnd_email);
        }

        @Override
        public void onClick(View view) {
            Intent i = new Intent(view.getContext(), ChatActivity.class);
            i.putExtra("clickedUserId", userList.get(getLayoutPosition()).getUid());
            context.startActivity(i);
        }
    }
}

