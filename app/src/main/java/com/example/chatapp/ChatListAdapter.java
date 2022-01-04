package com.example.chatapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    Context context;
    ArrayList<Message> chatList;

    public ChatListAdapter(Context context, ArrayList<Message> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.chat, parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message msg = chatList.get(position);

        if(msg.sendType.equals("to")){
            holder.toText.setVisibility(View.VISIBLE);
            holder.fromText.setVisibility(View.GONE);
            holder.toText.setText(msg.text);
        }
        else{
            holder.fromText.setVisibility(View.VISIBLE);
            holder.toText.setVisibility(View.GONE);
            holder.fromText.setText(msg.text);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView fromText, toText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            fromText = (TextView) itemView.findViewById(R.id.from_text);
            toText = (TextView) itemView.findViewById(R.id.to_text);

        }
    }
}