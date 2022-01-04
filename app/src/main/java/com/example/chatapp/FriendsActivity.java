package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FriendsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FriendListAdapter myAdapter;
    private ArrayList<User> list;
    private DatabaseReference dbRef;
    private RecyclerView friendList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        friendList = findViewById(R.id.frnd_list);

        list = new ArrayList<>();
        friendList.setHasFixedSize(true);
        friendList.setLayoutManager(new LinearLayoutManager(FriendsActivity.this));

        dbRef = FirebaseDatabase.getInstance().getReference("Users");
        fetchFriends();
    }

    void fetchFriends(){
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot db : snapshot.getChildren()){
                    String uid = db.child("uid").getValue().toString();
                    if(!uid.equals(mAuth.getCurrentUser().getUid())) {
                        String email = db.child("email").getValue().toString();
                        String name = db.child("name").getValue().toString();
                        String imageUrl = db.child("imageUrl").getValue().toString();

                        list.add(new User(uid, imageUrl, name, email));
                    }
                }
                myAdapter = new FriendListAdapter(FriendsActivity.this, list);
                friendList.setAdapter(myAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.settings:
                startActivity(new Intent(FriendsActivity.this, SettingsActivity.class));
                return true;
            case R.id.sign_out:
                mAuth.signOut();
                Toast.makeText(getApplicationContext(),"Logged Out",Toast.LENGTH_LONG).show();
                startActivity(new Intent(FriendsActivity.this, Signin.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}