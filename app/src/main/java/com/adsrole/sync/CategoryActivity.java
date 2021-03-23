package com.adsrole.sync;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class CategoryActivity extends AppCompatActivity {
TextView tv_category,tv_categoryValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        //getSupportActionBar().hide();
        tv_category = findViewById(R.id.tv_category);
        tv_categoryValue = findViewById(R.id.tv_categoryValue);
        /*receive Intent*/
        Intent i = getIntent();
        tv_category.setText(i.getStringExtra("catname"));
        tv_categoryValue.setText("There is no "+i.getStringExtra("catname"));
    }
}