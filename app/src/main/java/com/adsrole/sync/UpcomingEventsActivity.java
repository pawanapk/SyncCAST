package com.adsrole.sync;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class UpcomingEventsActivity extends AppCompatActivity {
ImageView event_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_events);
        //getSupportActionBar().hide();
        Intent i = getIntent();
        event_image = findViewById(R.id.event_image);
        Picasso.get().load(i.getStringExtra("catimage")).placeholder(R.drawable.category_icon).into(event_image);
    }
}