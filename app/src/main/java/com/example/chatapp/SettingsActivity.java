package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private CircleImageView settingsImg;
    private EditText settingsName;
    private Button updateBtn;
    private ProgressBar pBar;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private FirebaseStorage firebaseStorage;

    int SELECT_PICTURE = 200;
    private Uri selectedImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settingsImg = findViewById(R.id.settings_img);
        settingsName = findViewById(R.id.settings_name);
        updateBtn = findViewById(R.id.settings_btn);
        pBar = findViewById(R.id.settings_pbar);

        mAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        loadData();

        settingsImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pBar.setVisibility(View.VISIBLE);

                if(selectedImageUri != null) {
                    StorageReference filepath = firebaseStorage.getReference().child("ProfileImg")
                            .child(mAuth.getCurrentUser().getUid());
                    filepath.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl()
                                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Uri> task) {
                                               String editedName = settingsName.getText().toString();
                                               dbRef.child("name").setValue(editedName);
                                               dbRef.child("imageUrl").setValue(task.getResult().toString());
                                           }
                                       }
                                    );
                        }
                    });
                }
                else{
                    String editedName = settingsName.getText().toString();
                    dbRef.child("name").setValue(editedName);
                }
                Toast.makeText(SettingsActivity.this, "Information Updated", Toast.LENGTH_SHORT).show();
                pBar.setVisibility(View.INVISIBLE);

            }
        });
    }

    void loadData(){
        pBar.setVisibility(View.VISIBLE);

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String imageUrl = snapshot.child("imageUrl").getValue().toString();
                if(!imageUrl.equals("")) {
                    Picasso.get().load(imageUrl).into(settingsImg);
                }
                settingsName.setText(snapshot.child("name").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        pBar.setVisibility(View.INVISIBLE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    settingsImg.setImageURI(selectedImageUri);
                }
            }
        }
    }
}