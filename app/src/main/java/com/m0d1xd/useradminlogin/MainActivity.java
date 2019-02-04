package com.m0d1xd.useradminlogin;

import android.content.Intent;
import android.content.pm.SigningInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.m0d1xd.useradminlogin.Activities.Authentication.SignInActivity;
import com.m0d1xd.useradminlogin.Activities.Authentication.SignUpActivity;
import com.m0d1xd.useradminlogin.Model.Users;

public class MainActivity extends AppCompatActivity {
    public static Users user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_signin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignUpActivity.class));

            }
        });

        findViewById(R.id.btn_signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignInActivity.class));

            }
        });


    }
}
