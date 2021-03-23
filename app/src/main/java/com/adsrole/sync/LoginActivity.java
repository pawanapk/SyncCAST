package com.adsrole.sync;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    private SignInButton gsignin;
    private FirebaseAuth mAuth;
    private static final String EMAIL = "email";
    private static final String USER_POSTS = "user_posts";
    private static final String AUTH_TYPE = "rerequest";
    private CallbackManager mCallbackManager;
    //LoginButton mLoginButton;
    LinearLayout linear_glogin, linear_fblogin;
    private LoginManager loginManager;
    String registerurl = "https://syncliv.com/app/registeruser.php";
    Button btnregister, btnlogin;
    EditText etname, etemail, etpassword, etphone;
    CheckBox ctvnotification;
    String name, email, mobile, password, type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnlogin = findViewById(R.id.btnlogin);
        btnregister = findViewById(R.id.btnregister);
        etemail = findViewById(R.id.etemail);
        etname = findViewById(R.id.etname);
        etpassword = findViewById(R.id.etpassword);
        etphone = findViewById(R.id.etphone);
        ctvnotification = findViewById(R.id.ctvnotification);
        //getSupportActionBar().hide();
        linear_fblogin = findViewById(R.id.linear_fblogin);
        linear_glogin = findViewById(R.id.linear_glogin);

        /*to store authentication one time in sharedpreferences*/
        sp = getSharedPreferences("loginpref", Context.MODE_PRIVATE);
        ed = sp.edit();
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login = new Intent(LoginActivity.this, SignInActivity.class);
                startActivity(login);
            }
        });


        FacebookSdk.sdkInitialize(LoginActivity.this);
        mCallbackManager = CallbackManager.Factory.create();
        facebookLogin();
        //mLoginButton = findViewById(R.id.fblogin);
        gsignin = findViewById(R.id.gsignin);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1074572891884-o784dpbujs85fbm7an6ep86s32hnk1ek.apps.googleusercontent.com")
                .requestEmail()
                .build();
        final GoogleSignInClient signInClient = GoogleSignIn.getClient(this, gso);
        linear_glogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = signInClient.getSignInIntent();
                startActivityForResult(signInIntent, 1);
            }
        });
        linear_fblogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginManager.logInWithReadPermissions(
                        LoginActivity.this,
                        Arrays.asList(
                                "email",
                                "public_profile",
                                "user_birthday"));
            }
        });

        //mLoginButton.setBackgroundResource(R.drawable.fblogin_icon);
        // Set the initial permissions to request from the user while logging in
        //mLoginButton.setReadPermissions("email", "public_profile");
        /*mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //            Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }
            @Override
            public void onCancel() {
                //Log.d(TAG, "facebook:onCancel");
                // ...
            }
            @Override
            public void onError(FacebookException error) {
                //Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });*/

        /*login usign form*/
        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = etname.getText().toString().trim();
                email = etemail.getText().toString().trim();
                mobile = etphone.getText().toString().trim();
                password = etpassword.getText().toString().trim();
                type = "form";
                if (name.isEmpty() || email.isEmpty() || mobile.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please fill the form", Toast.LENGTH_SHORT).show();
                } else {
                    userformlogin(name, email, mobile, password, type);
                }

            }
        });
    }

    private void userformlogin(String name, String email, String mobile, String password, String type) {
        StringRequest sr = new StringRequest(1, registerurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(LoginActivity.this, response, Toast.LENGTH_SHORT).show();
                /*clear the form*/
                etemail.setText("");
                etname.setText("");
                etpassword.setText("");
                etphone.setText("");
                /*switch to the login screen*/
                Intent login = new Intent(LoginActivity.this, MainActivity.class);
                /*to store authentication in sharedpreferences for session*/
                ed.putBoolean("login", true);
                ed.commit();
                startActivity(login);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("namekey", name);
                map.put("emailkey", email);
                map.put("passwordkey", password);
                map.put("phonekey", mobile);
                map.put("typekey", type);
                return map;
            }
        };
        RequestQueue rq = Volley.newRequestQueue(this);
        rq.add(sr);
    }

    private void facebookLogin() {
        loginManager
                = LoginManager.getInstance();
        mCallbackManager
                = CallbackManager.Factory.create();

        loginManager
                .registerCallback(
                        mCallbackManager,
                        new FacebookCallback<LoginResult>() {

                            @Override
                            public void onSuccess(LoginResult loginResult) {
                                handleFacebookAccessToken(loginResult.getAccessToken());
                            }

                            @Override
                            public void onCancel() {
                                Log.v("LoginScreen", "---onCancel");
                            }

                            @Override
                            public void onError(FacebookException error) {
                                // here write code when get error
                                Log.v("LoginScreen", "----onError: "
                                        + error.getMessage());
                            }
                        });
    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            userformlogin(user.getDisplayName(), user.getEmail(), "", "", "facebook");
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 1) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                // Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        } else {
            // Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        /*updateUI(currentUser);*/
        if (sp.getBoolean("login", false) == true) {
            Intent intent = new Intent(LoginActivity.this,
                    MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            Intent intent = new Intent(LoginActivity.this,
                    MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        //Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            // Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            userformlogin(user.getDisplayName(), user.getEmail(), "", "", "Google");
                            //updateUI(user);
                        } else {
                        }
                    }
                });
    }
}