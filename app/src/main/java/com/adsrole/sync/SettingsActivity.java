package com.adsrole.sync;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class SettingsActivity extends AppCompatActivity {
    SharedPreferences sp;
    SharedPreferences.Editor ed;
private ImageView iv_back;
private Button btn_editProfile, btn_changePassword, btn_queries, btn_privacyPolicy, btn_logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //getSupportActionBar().hide();

        /*to store authentication one time in sharedpreferences*/
        sp = getSharedPreferences("loginpref", Context.MODE_PRIVATE);
        ed = sp.edit();

        btn_changePassword = findViewById(R.id.btn_changePassword);
        btn_editProfile  = findViewById(R.id.btn_editProfile);
        btn_logout = findViewById(R.id.btn_logout);
        btn_privacyPolicy = findViewById(R.id.btn_privacyPolicy);
        btn_queries = findViewById(R.id.btn_queries);
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_queries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent queries = new Intent(SettingsActivity.this, QueryActivity.class);
                startActivity(queries);
            }
        });

        btn_privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent privacy = new Intent(Intent.ACTION_VIEW);
                privacy.setData(Uri.parse("https://syncliv.com/app/privacypolicy.html"));
                startActivity(privacy);
            }
        });

        btn_editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editProfile = new Intent(SettingsActivity.this, EditProfileActivity.class);
                startActivity(editProfile);
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent logout = new Intent(SettingsActivity.this, LoginActivity.class);
                /*to clear the login session time*/
                ed.clear();
                ed.apply();
                GoogleSignInOptions gso = new GoogleSignInOptions.
                        Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                        build();

                GoogleSignInClient googleSignInClient= GoogleSignIn.getClient(SettingsActivity.this,gso);
                googleSignInClient.signOut();
                startActivity(logout);
            }
        });

        btn_changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent changePassword = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
                startActivity(changePassword);
            }
        });
    }
}