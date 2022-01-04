package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Signin extends AppCompatActivity {

    private TextView lgnText;
    private Button lgnBtn;
    private EditText lgnEmail, lgnPsd;
    private ProgressBar pBar;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if(user != null){
            startActivity(new Intent(Signin.this, FriendsActivity.class));
            finish();
        }

        lgnText = findViewById(R.id.lgn_text);

        lgnBtn = findViewById(R.id.lgn_btn);
        lgnEmail = findViewById(R.id.lgn_email);
        lgnPsd = findViewById(R.id.lgn_psd);
        pBar = findViewById(R.id.lgn_pbar);

        lgnText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Signin.this, Signup.class));
                finish();
            }
        });

        lgnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = lgnEmail.getText().toString();
                String password = lgnPsd.getText().toString();

                if(TextUtils.isEmpty(email)){
                    lgnEmail.setError("Enter Email");
                    lgnEmail.findFocus();
                }
                else if(TextUtils.isEmpty(password)){
                    lgnPsd.setError("Enter Password");
                    lgnPsd.findFocus();
                }
                else {
                    pBar.setVisibility(View.VISIBLE);
                    signIn(email, password);
                }
            }
        });
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(Signin.this, "Welcome", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Signin.this, FriendsActivity.class));
                            finish();
                        }
                        else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Signin.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                        pBar.setVisibility(View.INVISIBLE);
                    }
                });
    }
}