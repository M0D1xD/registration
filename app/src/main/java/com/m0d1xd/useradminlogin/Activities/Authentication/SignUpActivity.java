package com.m0d1xd.useradminlogin.Activities.Authentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.m0d1xd.useradminlogin.Activities.AdminPanel.AdminHomeActivity;
import com.m0d1xd.useradminlogin.Activities.UserPanel.UserHomeActivity;
import com.m0d1xd.useradminlogin.Model.Users;
import com.m0d1xd.useradminlogin.R;


import static com.m0d1xd.useradminlogin.MainActivity.user;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    EditText email, pass, name;
    Boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.et_email);
        pass = findViewById(R.id.et_pass);
        name = findViewById(R.id.et_name);

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


        final Users user = new Users();

        findViewById(R.id.btn_signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(email.getText())) {
                    email.requestFocus();
                    email.setError("Email Required");
                    return;
                }
                if (TextUtils.isEmpty(pass.getText())) {
                    pass.requestFocus();
                    pass.setError("Password Required");
                    return;
                } else if (pass.getText().length() < 6) {
                    pass.requestFocus();
                    pass.setError("Password must be more that 6 letters");
                    return;
                }
                if (TextUtils.isEmpty(name.getText())) {
                    name.requestFocus();
                    name.setError("name Required");
                    return;
                }

                user.setAdmin(isAdmin);
                user.setUserName(name.getText().toString());

                mAuth.createUserWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d("SignUpActivity", "onComplete: task.isSuccessful =" + task.isSuccessful());
                                    DatabaseReference userData = FirebaseDatabase.getInstance()
                                            .getReference("Users");
                                    userData.child(mAuth.getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("SignUpActivity", "onComplete: userData setValue Successful");
                                            } else {
                                                Log.d("SignUpActivity", "onComplete: userData setValue Failed");

                                            }
                                        }
                                    });
                                } else {
                                    Log.d("SignUpActivity", "onComplete: task.isSuccessful =" + task.isSuccessful());
                                    Log.d("SignUpActivity", "onComplete: " + task.isSuccessful());
                                    Log.d("SignUpActivity", "onComplete: " + task.getException());

                                }
                            }
                        });
            }
        });


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
                        startActivity(new Intent(SignUpActivity.this, AdminHomeActivity.class));
                        finish();
                    } else {
                        startActivity(new Intent(SignUpActivity.this, UserHomeActivity.class));
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
