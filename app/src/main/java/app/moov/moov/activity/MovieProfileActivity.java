package app.moov.moov.activity;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.json.JSONObject;

import app.moov.moov.R;
import app.moov.moov.model.Post;
import app.moov.moov.util.FirebaseSwitchingAdapter;
import app.moov.moov.util.VolleySingleton;

/**
 * Created by Lisa on 07/04/18.
 *
 * Modified by Sammy 4/7/2018
 */

public class MovieProfileActivity extends ToolbarBaseActivity{
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private RecyclerView movieRecycler;
    ImageView ivMoviePoster;
    private TextView tvMovieTitle;
    private TextView tvReleaseYear;
    private TextView tvRunTime;
    private TextView tvMovieSummary;
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

        movieID = getIntent().getIntExtra("movieID", 0);

        database = FirebaseDatabase.getInstance();
        ref = database.getReference();

        setUIViews();
    }

    private void setUIViews() {

        movieRecycler = (RecyclerView) findViewById(R.id.movieRecycler);
        movieRecycler.setFocusable(false);
        movieRecycler.setHasFixedSize(true);
        orderedManager = new LinearLayoutManager(this);
//        orderedManager.setAutoMeasureEnabled(true);
        movieRecycler.setLayoutManager(orderedManager);
//        movieRecycler.setNestedScrollingEnabled(true);



        DatabaseReference baseRef = FirebaseDatabase.getInstance().getReference();
        Query keysQuery = FirebaseDatabase.getInstance().getReference().child("PostsByMovie").child(Integer.toString(movieID)).orderByChild("timestamp");

        FirebaseRecyclerOptions<Post> options = new FirebaseRecyclerOptions.Builder<Post>()
                .setIndexedQuery(keysQuery, baseRef.child("Posts"), Post.class)
                .build();

        FirebaseSwitchingAdapter FBRA = new FirebaseSwitchingAdapter(options, this);
        FBRA.startListening();
        movieRecycler.setAdapter(FBRA);

        String url = "https://api.themoviedb.org/3/movie/"+ Integer.toString(movieID) + "?api_key=3744632a440f06514578b01d1b6e9d27";
        RequestQueue queue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONObject movieDetail = response;
                    String title = (String) response.get("title");
                    tvMovieTitle = (TextView) findViewById(R.id.tvMovieTitle);
                    if (title != null) {
                        tvMovieTitle.setText("Movie Title: " + title);
                    }

                    String releaseDate = (String) response.get("release_date");
                    tvReleaseYear = (TextView) findViewById(R.id.tvReleaseYear);
                    if (releaseDate!=null) {
                        String subString = releaseDate.substring(0, 4);
                        tvReleaseYear.setText("Release Year: " + subString);
                    }

                    String summary = (String) response.get("overview");
                    tvMovieSummary = (TextView) findViewById(R.id.tvMovieSummary);
                    if (summary!=null) {
                        tvMovieSummary.setText("Summary: " + summary);
                    }

                    tvRunTime = (TextView) findViewById(R.id.tvRuntime);

                    try {
                        Integer runtime = (Integer) response.get("runtime");
                        tvRunTime.setText("Runtime: " + Integer.toString(runtime) + " minutes");
                    } catch (ClassCastException e) { //catch null runtimes
                        String unknown = "Run time unknown";
                        tvRunTime.setText("Runtime: " +unknown);
                    }

                    ivMoviePoster = (ImageView) findViewById(R.id.ivMoviePoster);

                    String posterUrl = "https://image.tmdb.org/t/p/w185/" + ((String) movieDetail.get("poster_path"));

                    Glide.with(MovieProfileActivity.this).asBitmap().load(posterUrl).into(ivMoviePoster);

                } catch (org.json.JSONException e) {
                    Toast.makeText(MovieProfileActivity.this, "Sorry", Toast.LENGTH_LONG).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MovieProfileActivity.this,"An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                Log.e("JSON Error", "Unable to get JSON Object Array");
            }
        });

        queue.add(jsonObjectRequest);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}