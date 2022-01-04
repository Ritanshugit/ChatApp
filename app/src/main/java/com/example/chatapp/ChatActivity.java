package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String userId;

    private CircleImageView chatImg;
    private TextView chatName;
    private RecyclerView chatList;
    private EditText chatEdtView;
    private ImageView chatSendBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private ArrayList<Message> list;
    private ChatListAdapter myAdapter;
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent i = getIntent();
        userId = i.getStringExtra("clickedUserId");

        chatImg = findViewById(R.id.chat_img);
        chatName = findViewById(R.id.chat_name);
        chatList = findViewById(R.id.chat_list);
        chatEdtView = findViewById(R.id.chat_edtview);
        chatSendBtn = findViewById(R.id.chat_send_btn);
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();

        list = new ArrayList<>();
        layoutManager = new LinearLayoutManager(ChatActivity.this);
        chatList.setHasFixedSize(true);
        chatList.setLayoutManager(layoutManager);
        myAdapter = new ChatListAdapter(ChatActivity.this, list);
        chatList.setAdapter(myAdapter);

        selectedChatInfo();
        fetchChats();

        chatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = chatEdtView.getText().toString();
                chatEdtView.setText("");

                Message toMessage = new Message("to", msg);
                String key1 = dbRef.child("Messages").child(mAuth.getCurrentUser().getUid()).child(userId).push().getKey();
                dbRef.child("Messages").child(mAuth.getCurrentUser().getUid()).child(userId).child(key1).setValue(toMessage);

                Message fromMessage = new Message("from", msg);
                String key2 = dbRef.child("Messages").child(userId).child(mAuth.getCurrentUser().getUid()).push().getKey();
                dbRef.child("Messages").child(userId).child(mAuth.getCurrentUser().getUid()).child(key2).setValue(fromMessage);
            }
        });
    }

    void selectedChatInfo(){
        dbRef.child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    String imageUrl = snapshot.child("imageUrl").getValue().toString();
                    if (!imageUrl.equals("")) {
                        Picasso.get().load(imageUrl).into(chatImg);
                    }
                    String name = snapshot.child("name").getValue().toString();
                    chatName.setText(name);
                } catch (NullPointerException e) {
                    Toast.makeText(ChatActivity.this, "User does not exist", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    void fetchChats(){
        dbRef.child("Messages").child(mAuth.getCurrentUser().getUid()).child(userId).
                addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        String sendType = snapshot.child("sendType").getValue().toString();
                        String text = snapshot.child("text").getValue().toString();
                        list.add(new Message(sendType, text));
                        myAdapter.notifyItemInserted(list.size()-1);
                        chatList.smoothScrollToPosition(list.size()-1);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

}

