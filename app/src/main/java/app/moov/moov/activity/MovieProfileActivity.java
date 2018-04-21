package app.moov.moov.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.json.JSONObject;

import app.moov.moov.R;
import app.moov.moov.model.Post;
import app.moov.moov.util.FirebaseSwitchingAdapter;
import app.moov.moov.util.PaginatingPostsActivity;
import app.moov.moov.util.VolleySingleton;

/**
 * Created by Lisa on 07/04/18.
 *
 * Modified by Sammy 4/7/2018
 */

public class MovieProfileActivity extends PaginatingPostsActivity{
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private RecyclerView movieRecycler;
    private LinearLayoutManager orderedManager;
    private int movieID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolBarSetup(toolbar);

        BottomNavigationView navBar = (BottomNavigationView) findViewById(R.id.navBar);
        BottomNavigationViewHelper.disableShiftMode(navBar);
        setUpNavBar(navBar);
//        BottomNavigationViewEx navBar = (BottomNavigationViewEx) findViewById(R.id.navBar);
//        setUpNavBar(navBar);

        movieID = getIntent().getIntExtra("movieID", 0);

        database = FirebaseDatabase.getInstance();
        ref = database.getReference();

        setUIViews();
    }

    private void setUIViews() {

        movieRecycler = (RecyclerView) findViewById(R.id.movieRecycler);
        DatabaseReference baseRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference postsRef = baseRef.child("PostsByMovie").child(Integer.toString(movieID));

        movieProfilePaginationSetup(this, FirebaseAuth.getInstance(), database,
                baseRef, postsRef, movieRecycler, movieID);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.putExtra("movieID", movieID);
                intent.putExtra("movieTitle", getMovieProfilePaginatingRecyclerAdapter().getMovieTitle());
                intent.putExtra("posterURL", getMovieProfilePaginatingRecyclerAdapter().getPosterURL());
                startActivity(intent);

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}