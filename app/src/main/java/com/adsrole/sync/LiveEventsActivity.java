package com.adsrole.sync;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.badge.BadgeUtils;
import com.squareup.picasso.Picasso;

public class LiveEventsActivity extends AppCompatActivity {
ImageView event_image;
Button btn_ticketBuy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_events);
       // getSupportActionBar().hide();
        Intent i = getIntent();
        event_image = findViewById(R.id.event_image);
        Picasso.get().load(i.getStringExtra("catimage")).placeholder(R.drawable.category_icon).into(event_image);
        btn_ticketBuy = findViewById(R.id.btn_ticketBuy);

        btn_ticketBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent buyticket = new Intent(LiveEventsActivity.this, TicketActivity.class);
                startActivity(buyticket);
            }
        });
    }
}