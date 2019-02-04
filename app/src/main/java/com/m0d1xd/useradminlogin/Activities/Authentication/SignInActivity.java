package com.m0d1xd.useradminlogin.Activities.Authentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.SigningInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.m0d1xd.useradminlogin.Activities.AdminPanel.AdminHomeActivity;
import com.m0d1xd.useradminlogin.Activities.UserPanel.UserHomeActivity;
import com.m0d1xd.useradminlogin.MainActivity;
import com.m0d1xd.useradminlogin.Model.Users;
import com.m0d1xd.useradminlogin.R;

import static com.m0d1xd.useradminlogin.MainActivity.user;

public class SignInActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    EditText email, pass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.et_email);
        pass = findViewById(R.id.et_pass);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Checking ");

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                progressDialog.show();
                if (firebaseAuth.getCurrentUser() != null) {
                    Log.d("SignInActivity", "onAuthStateChanged: User is logged in ");
                    getUserData(firebaseAuth.getCurrentUser().getUid());
                    progressDialog.dismiss();
                    finish();
                } else {
                    Log.d("SignInActivity", "onAuthStateChanged: User signed out ");
                    Log.d("SignInActivity", "onAuthStateChanged: ");
                    progressDialog.dismiss();
                }
            }
        };

        Button bt = findViewById(R.id.login);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                if (!TextUtils.isEmpty(email.getText()) && !TextUtils.isEmpty(pass.getText())) {
                    String Email = email.getText().toString();
                    String password = pass.getText().toString();
                    mAuth.signInWithEmailAndPassword(Email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SignInActivity.this, "Weolcome", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    } else
                                        progressDialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("MainActivity", "onFailure: " + e.getMessage());
                            progressDialog.dismiss();
                            Toast.makeText(SignInActivity.this, "Error ", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseApp.initializeApp(this);
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuth != null)
            mAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuth != null)
            mAuth.removeAuthStateListener(mAuthStateListener);
    }


    private void getUserData(String uid) {
        final DatabaseReference userData = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        userData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    user = ds.getValue(Users.class);
                    assert user != null;
                    if (user.isAdmin()) {
                        startActivity(new Intent(SignInActivity.this, AdminHomeActivity.class));
                        finish();
                    } else {
                        startActivity(new Intent(SignInActivity.this, UserHomeActivity.class));
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
