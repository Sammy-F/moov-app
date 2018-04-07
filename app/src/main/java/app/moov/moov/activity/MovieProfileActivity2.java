package app.moov.moov.activity;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import org.json.JSONArray;
import org.json.JSONObject;

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
        String movieID = getIntent().getStringExtra("movieID");
        String url = "https://api.themoviedb.org/3/movie/343611?api_key=" + movieID;
        RequestQueue queue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();

        JsonObjectRequest JsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONObject movieDetail = response;
                    String title = (String) response.get("title");
                    tvMovieTitle = (TextView) findViewById(R.id.tvMovieTitle);
                    if (title != null) {
                        tvMovieTitle.setText(title);
                    }

                    String releaseDate = (String) response.get("release_date");
                    tvReleaseYear = (TextView) findViewById(R.id.tvReleaseYear);
                    if (releaseDate!=null) {
                        String subString = releaseDate.substring(0, 4);
                        tvReleaseYear.setText(subString);
                    }

                    String summary = (String) response.get("overview");
                    tvMovieSummary = (TextView) findViewById(R.id.tvMovieSummary);
                    if (summary!=null) {
                        tvMovieSummary.setText(summary);
                    }

                    Integer runtime = (Integer) response.get("runtime");
                    tvRunTime = (TextView) findViewById(R.id.tvRuntime);
                    if(runtime != null) {
                        tvRunTime.setText(runtime.toString());
                    }

                    ivMoviePoster = (ImageView) findViewById(R.id.ivMoviePoster);


                } catch (org.json.JSONException e) {
                    Toast.makeText(MovieProfileActivity2.this, "Sorry", Toast.LENGTH_LONG).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MovieProfileActivity2.this,"An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                Log.e("JSON Error", "Unable to get JSON Object Array");
            }
        });

    }


    }
