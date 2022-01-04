package com.example.chatapp;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class Signup extends AppCompatActivity {

    private TextView sgnUpText;
    private Button sgnUpBtn;
    private EditText sgnName, sgnEmail, sgnPsd;
    private CircleImageView sgnImg;
    private ProgressBar pBar;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseDatabase fDatabase;
    private DatabaseReference dbRef;
    private FirebaseStorage firebaseStorage;


    private int SELECT_PICTURE = 200;
    private Uri selectedImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        sgnUpText = findViewById(R.id.sgn_text);
        sgnUpBtn = findViewById(R.id.sgn_btn);
        sgnName = findViewById(R.id.sgn_name);
        sgnEmail = findViewById(R.id.sgn_email);
        sgnPsd = findViewById(R.id.sgn_psd);
        sgnImg = findViewById(R.id.sgn_img);
        pBar = findViewById(R.id.sgn_pbar);

        mAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        sgnUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Signup.this, Signin.class));
                finish();
            }
        });

        sgnImg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);

                // pass the constant to compare it
                // with the returned requestCode
                startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
            }
        });

        sgnUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userName = sgnName.getText().toString();
                String email = sgnEmail.getText().toString();
                String password = sgnPsd.getText().toString();

                if(TextUtils.isEmpty(userName)){
                    sgnEmail.setError("Enter Name");
                    sgnEmail.findFocus();
                }

                else if(TextUtils.isEmpty(email)){
                    sgnEmail.setError("Enter Email");
                    sgnEmail.findFocus();
                }
                else if(TextUtils.isEmpty(password)){
                    sgnPsd.setError("Enter Password");
                    sgnPsd.findFocus();
                }
                else {
                    pBar.setVisibility(View.VISIBLE);
                    createAccount(selectedImageUri, userName, email, password);
                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                // update the preview image in the layout
                sgnImg.setImageURI(selectedImageUri);
            }
        }
    }

    private void createAccount(Uri imageUri, String userName, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user = mAuth.getCurrentUser();
                            dbRef = fDatabase.getReference("Users");

                            if(imageUri != null) {
                                StorageReference filepath = firebaseStorage.getReference().child("ProfileImg")
                                        .child(user.getUid());
                                filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl()
                                                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                       @Override
                                                       public void onComplete(@NonNull Task<Uri> task) {
                                                           User myUser = new User(user.getUid(), task.getResult().toString(), userName, email);
                                                           dbRef.child(user.getUid()).setValue(myUser);
                                                       }
                                                   }
                                                );
                                    }
                                });
                            }
                            else{
                                User myUser = new User(user.getUid(), "", userName, email);
                                dbRef.child(user.getUid()).setValue(myUser);
                            }

                            Toast.makeText(Signup.this, "Account Created.", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(Signup.this, FriendsActivity.class));
                            finish();
                        }
                        else {
                            Toast.makeText(Signup.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                        pBar.setVisibility(View.INVISIBLE);
                    }
                });
    }
}