package app.moov.moov.activity;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import app.moov.moov.R;
import app.moov.moov.util.VolleySingleton;

/**
 * Created by Lisa on 07/04/18.
 */

public class MovieProfileActivity2 extends ToolbarBaseActivity{
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private RecyclerView movieRecycler;
    ImageView ivMoviePoster;
    TextView tvMovieTitle;
    TextView tvReleaseYear;
    TextView tvRunTime;
    TextView tvMovieSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_profile2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolBarSetup(toolbar);
        BottomNavigationView navBar = (BottomNavigationView) findViewById(R.id.navBar);
        setUpNavBar(navBar);

        database = FirebaseDatabase.getInstance();
        ref = database.getReference();

        setUIViews();
    }

    private void setUIViews() {
        getIntent().getStringExtra("movieID");
        RequestQueue queue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();



    }



    }
