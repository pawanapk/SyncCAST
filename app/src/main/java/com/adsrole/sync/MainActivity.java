package com.adsrole.sync;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

BottomNavigationView bottomNavigationView;
    private int mMenuId;
    RecyclerView recyclerView_category, recyclerView_liveevents, recyclerView_upcomingevents, recyclerView_prerecordevents;
    List<Category> catlist = new ArrayList<>();
    CategoryAdapter categoryAdapter;
    UpcomingEventsAdapter upcomingEventsAdapter;
    LiveEventsAdapter liveEventsAdapter;
    PrerecordEventsAdapter prerecordEventsAdapter;
    CircleImageView iv_userProfile;
    ImageView ivcast;
    GoogleApiClient googleApiClient;
    LinearLayout upcomingContainer, liveContainer;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getSupportActionBar().hide();
        progressBar = findViewById(R.id.progressbar);
        upcomingContainer = findViewById(R.id.upcomingContainer);
        liveContainer = findViewById(R.id.liveContainer);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        googleApiClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        ivcast = findViewById(R.id.ivcast);
        /*To check the internet connection is active or not*/
        ConnectivityManager ConnectionManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = ConnectionManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected() == true) {

            loadcategory();
       //     loadliveevents();
       //     loadupcomingevents();
        } else {
            Toast.makeText(getApplicationContext(), "Network Not Available", Toast.LENGTH_LONG).show();
        }
        iv_userProfile = findViewById(R.id.iv_userProfile);
        recyclerView_liveevents = findViewById(R.id.recyclerview_liveevents);
        recyclerView_prerecordevents = findViewById(R.id.recyclerview_prerecordevents);
        recyclerView_upcomingevents = findViewById(R.id.recyclerview_upcomingevents);
        recyclerView_category = findViewById(R.id.recyclerview_category);
        recyclerView_category.setHasFixedSize(true);
        recyclerView_liveevents.setHasFixedSize(true);
        recyclerView_upcomingevents.setHasFixedSize(true);
        recyclerView_prerecordevents.setHasFixedSize(true);
        recyclerView_category.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView_liveevents.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView_upcomingevents.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        recyclerView_prerecordevents.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setBackground(null);
        /*to click on the bottom navigation Item selected*/
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        /*click on the profile*/
        iv_userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an alert builder
                AlertDialog.Builder builder
                        = new AlertDialog.Builder(MainActivity.this, R.style.DialogTheme);
                //builder.setTitle("Name");
                // set the custom layout
                final View customLayout
                        = getLayoutInflater()
                        .inflate(
                                R.layout.item_profile,
                                null);
                builder.setView(customLayout);
                CircleImageView iv_userProfile = customLayout.findViewById(R.id.iv_userProfile);
                iv_userProfile.setImageResource(R.drawable.ic_profile);
                ImageView iv_profileSettings = customLayout.findViewById(R.id.iv_profileSettings);
                iv_profileSettings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent settings = new Intent(customLayout.getContext(), SettingsActivity.class);
                        startActivity(settings);
                    }
                });

                // add a button
                /*builder
                        .setPositiveButton(
                                "OK",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which) {

                                        // send data from the
                                        // AlertDialog to the Activity
                                        //EditText editText = customLayout.findViewById(R.id.editText);

                                    }
                                });
*/
                // create and show
                // the alert dialog
                AlertDialog dialog
                        = builder.create();
                dialog.show();
            }
        });

        ivcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent("android.settings.CAST_SETTINGS"));
            }
        });
    }

    /*private void loadupcomingevents() {
        StringRequest sr = new StringRequest(0,
                "https://syncliv.com/app/viewcategory.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jo = new JSONObject(response);
                    JSONArray ja = jo.getJSONArray("category");
                    for (int i = 0 ; i < ja.length(); i++)
                    {
                        JSONObject job = ja.getJSONObject(i);
                        catlist.add(new Category(job.getString("catname"), job.getString("catimage")));
                    }
                    upcomingEventsAdapter = new UpcomingEventsAdapter(MainActivity.this, catlist);
                    recyclerView_upcomingevents.setAdapter(upcomingEventsAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue rq = Volley.newRequestQueue(this);
        rq.add(sr);
    }*/

    /*private void loadliveevents() {
        StringRequest sr = new StringRequest(0,
                "https://syncliv.com/app/viewcategory.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jo = new JSONObject(response);
                    JSONArray ja = jo.getJSONArray("category");
                    for (int i = 0 ; i < ja.length(); i++)
                    {
                        JSONObject job = ja.getJSONObject(i);
                        catlist.add(new Category(job.getString("catname"), job.getString("catimage")));
                    }
                    liveEventsAdapter = new LiveEventsAdapter(MainActivity.this, catlist);
                    recyclerView_liveevents.setAdapter(liveEventsAdapter);
                    recyclerView_prerecordevents.setAdapter(liveEventsAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue rq = Volley.newRequestQueue(this);
        rq.add(sr);
    }*/

    private void loadcategory() {
        StringRequest sr = new StringRequest(0,
                "https://syncliv.com/app/viewcategory.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jo = new JSONObject(response);
                    JSONArray ja = jo.getJSONArray("category");
                    for (int i = 0 ; i < ja.length(); i++)
                    {
                        JSONObject job = ja.getJSONObject(i);
                        catlist.add(new Category(job.getString("catname"), job.getString("catimage")));
                    }
                    categoryAdapter = new CategoryAdapter(MainActivity.this, catlist);
                    recyclerView_category.setAdapter(categoryAdapter);

                    /*for demo from upcoming events*/
                    upcomingEventsAdapter = new UpcomingEventsAdapter(MainActivity.this, catlist);
                    recyclerView_upcomingevents.setAdapter(upcomingEventsAdapter);

                    /*for demo from live events*/
                    liveEventsAdapter = new LiveEventsAdapter(MainActivity.this, catlist);
                    recyclerView_liveevents.setAdapter(liveEventsAdapter);

                    prerecordEventsAdapter = new PrerecordEventsAdapter(MainActivity.this, catlist);
                    recyclerView_prerecordevents.setAdapter(prerecordEventsAdapter);
                    progressBar.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue rq = Volley.newRequestQueue(this);
        rq.add(sr);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mMenuId = item.getItemId();
        for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
            MenuItem menuItem = bottomNavigationView.getMenu().getItem(i);
            boolean isChecked = menuItem.getItemId() == item.getItemId();
            menuItem.setChecked(isChecked);
        }
        switch (item.getItemId()) {
            case R.id.miHome: {

            }
            break;
            case R.id.miSearch: {
                Intent search = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(search);
            }
            break;
            case R.id.placeholder: {

            }
            break;
            case R.id.miShare: {
                //Toast.makeText(this, "fgjgfdhfgjd", Toast.LENGTH_SHORT).show();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Hey check out my app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Share App Via:"));
            }
            break;
            case R.id.miNotification: {
                Intent notification = new Intent(MainActivity.this, NotificationActivity.class);
                startActivity(notification);
            }
            break;
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        googleApiClient.connect();
        AppIndex.AppIndexApi.start(googleApiClient, getIndexApiAction());
    }


    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(googleApiClient, getIndexApiAction());
        googleApiClient.disconnect();
    }

    private Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

}