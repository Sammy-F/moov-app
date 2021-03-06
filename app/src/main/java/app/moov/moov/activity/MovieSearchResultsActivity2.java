package app.moov.moov.activity;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.moov.moov.R;
//import app.moov.moov.util.JSONMovieResultsAdapter;
import app.moov.moov.util.MovieSearchResultAdapter;
import app.moov.moov.util.VolleySingleton;
/**
 * Created by Lisa on 30/03/18.
 * THIS IS NOT FOR SEARCHING IN THE POST ACTIVITY.
 *
 * Modified by Sammy 3/30/2018
 */

public class MovieSearchResultsActivity2 extends ToolbarBaseActivity {

    private RecyclerView searchRecycler;
    private String searchQuery;
    private FirebaseAuth firebaseAuth;
    private Context thisContext;
    private ProgressBar progressBar;

    /**
     * Initializes all of the object (like toolbar, bottom nav bar), sets up data references
     * and specifies which layout XML to use
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_search_results);

        searchQuery = getIntent().getStringExtra("searchQuery").trim();
        firebaseAuth = FirebaseAuth.getInstance();
        thisContext = this;
        firebaseAuth = FirebaseAuth.getInstance();
        searchQuery = getIntent().getStringExtra("searchQuery");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolBarSetup(toolbar);

        BottomNavigationView navBar = findViewById(R.id.navBar);
        BottomNavigationViewHelper.disableShiftMode(navBar);
        setUpNavBar(navBar);

        setUIViews();
    }

    /**
     * Initialize the View variables used in
     * the Activity.
     */
    public void setUIViews() {
        searchRecycler = (RecyclerView) findViewById(R.id.movieSearchRecycler);
        progressBar = (ProgressBar) findViewById(R.id.resultsProgress);

        progressBar.setVisibility(View.VISIBLE);
        searchQuery = searchQuery.replaceAll(" ", "+");
        String url = "https://api.themoviedb.org/3/search/movie?api_key=3744632a440f06514578b01d1b6e9d27&query=" + searchQuery + "&page=1";

        //Get the VolleySingleton queue that we use to queue requests
        RequestQueue queue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());

        //Create a new request that we will load to the queue
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray resultsArray = response.getJSONArray("results");
                    List<JSONObject> convertedArray = new ArrayList<JSONObject>();

                    for (int i = 0; i < resultsArray.length(); i++) {
                        JSONObject thisMovie = resultsArray.getJSONObject(i);
                        convertedArray.add(thisMovie);
                    }

                    if (convertedArray.size() == 0) {
                        Toast.makeText(MovieSearchResultsActivity2.this,"No Results Found", Toast.LENGTH_SHORT).show();
                    } else if (convertedArray.size() > 0) {
                        searchRecycler = (RecyclerView) findViewById(R.id.searchRecycler);
                        searchRecycler.setHasFixedSize(true);
                        searchRecycler.setDrawingCacheEnabled(true);
                        searchRecycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

                        GridLayoutManager mLayoutManager = new GridLayoutManager(thisContext, 2);
                        searchRecycler.setLayoutManager(mLayoutManager);

                        MovieSearchResultAdapter searchAdapter = new MovieSearchResultAdapter(thisContext, convertedArray);
                        searchRecycler.setAdapter(searchAdapter);
                    }
                    else {
                        Toast.makeText(MovieSearchResultsActivity2.this,"An error occurred", Toast.LENGTH_SHORT).show();
                    }

                    progressBar.setVisibility(View.INVISIBLE);

                } catch (org.json.JSONException e) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(MovieSearchResultsActivity2.this,"An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                    Log.e("JSON Error", "Unable to get JSON Object Array");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(MovieSearchResultsActivity2.this,"An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                Log.e("JSON Error", "Unable to get JSON Object Array");
            }
        });
        queue.add(jsonObjectRequest);

    }

}
