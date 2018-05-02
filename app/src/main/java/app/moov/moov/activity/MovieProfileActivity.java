package app.moov.moov.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
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

import org.json.JSONException;
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

    private SwipeRefreshLayout swipeContainer;


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

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
//                activitySetup();
                swipeContainer.setRefreshing(false);

            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(
                R.color.google_green,
                R.color.google_yellow,
                R.color.google_red,
                R.color.google_blue);
    }

    private void setUIViews() {

        movieRecycler = (RecyclerView) findViewById(R.id.movieRecycler);
        DatabaseReference baseRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference postsRef = baseRef.child("PostsByMovie").child(Integer.toString(movieID));

        movieProfilePaginationSetup(this, FirebaseAuth.getInstance(), database,
                baseRef, postsRef, movieRecycler, movieID);

        String url = "https://api.themoviedb.org/3/movie/" + movieID + "?api_key=3744632a440f06514578b01d1b6e9d27&language=en-US";

        RequestQueue queue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();

        JsonObjectRequest mRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject movieDetail = response;
                    final String tMovieTitle = (String) movieDetail.get("title");

                    try {

                        final String posterURL = "https://image.tmdb.org/t/p/w185" + (String) movieDetail.get("poster_path");

                        FloatingActionButton fab = findViewById(R.id.tFab);
                        fab.bringToFront();
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.e("clicked", "cliecked");
                                Intent intent = new Intent(MovieProfileActivity.this, PostActivity.class);
                                intent.putExtra("movieID", movieID);
                                intent.putExtra("movieTitle", tMovieTitle);
                                intent.putExtra("posterURL", posterURL);

                                startActivity(intent);

                            }
                        });

                    } catch (ClassCastException e) {

                        FloatingActionButton fab = findViewById(R.id.tFab);
                        fab.bringToFront();
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.e("clicked", "cliecked");
                                Intent intent = new Intent(MovieProfileActivity.this, PostActivity.class);
                                intent.putExtra("movieID", movieID);
                                intent.putExtra("movieTitle", tMovieTitle);
                                intent.putExtra("posterURL", "");

                                startActivity(intent);

                            }
                        });

                    }

                } catch (JSONException e) {
                    Toast.makeText(MovieProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("error", e.getMessage());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MovieProfileActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("error", error.getMessage());
            }
        });

        queue.add(mRequest);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }


}