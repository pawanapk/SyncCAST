package com.adsrole.sync;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordActivity extends AppCompatActivity {
    EditText etemail, etpassword;
    Button btnsubmit, btnback;
    String email, password;
    String forgotpasswordurl = "https://syncliv.com/app/forgotpassword.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        //getSupportActionBar().hide();

        btnsubmit = findViewById(R.id.btnsubmit);
        btnback = findViewById(R.id.btnback);
        etemail = findViewById(R.id.etemail);
        etpassword = findViewById(R.id.etpassword);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = etemail.getText().toString().trim();
                password = etpassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Please fill the credentials", Toast.LENGTH_SHORT).show();
                } else {
                    resetpassword(email, password);
                }
            }
        });

    }

    private void resetpassword(String email, String password) {
        StringRequest sr = new StringRequest(1, forgotpasswordurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(ForgotPasswordActivity.this, response, Toast.LENGTH_SHORT).show();
                etpassword.setText("");
                etemail.setText("");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("emailkey", email);
                map.put("passwordkey", password);
                return map;
            }
        };

        RequestQueue rq = Volley.newRequestQueue(this);
        rq.add(sr);
    }
}