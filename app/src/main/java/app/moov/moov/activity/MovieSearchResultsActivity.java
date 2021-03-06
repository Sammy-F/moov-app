package app.moov.moov.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
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
import app.moov.moov.util.GridSpacingItemDecoration;
import app.moov.moov.util.PostableMovieResultAdapter;
import app.moov.moov.util.VolleySingleton;
/**
 * Class handles movie results after
 * searching for one the user wants to post about.
 */

public class MovieSearchResultsActivity extends ToolbarBaseActivity {

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

        thisContext = this;

        firebaseAuth = FirebaseAuth.getInstance();
        searchQuery = getIntent().getStringExtra("searchQuery");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolBarSetup(toolbar);

        BottomNavigationView navBar = (BottomNavigationView) findViewById(R.id.navBar);
        BottomNavigationViewHelper.disableShiftMode(navBar);
        setUpNavBar(navBar);

        setUIViews();
    }

    /**
     * Initialize and prepare UI Views for display
     * Called in onCreate
     */
    private void setUIViews() {


        final ProgressBar progressBar = findViewById(R.id.resultsProgress);
        progressBar.setVisibility(View.VISIBLE);

        //Gets the searchQuery string that is input by the user, replaces the spaces with +
        searchQuery = searchQuery.replaceAll(" ", "+");

        String url = "https://api.themoviedb.org/3/search/movie?api_key=3744632a440f06514578b01d1b6e9d27&language=en-US&query=" + searchQuery + "&page=1";

        //Get the VolleySingleton queue that we are using to queue requests
        RequestQueue queue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
        //Create a new request
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
                        Toast.makeText(MovieSearchResultsActivity.this,"No Results Found", Toast.LENGTH_SHORT).show();
                    } else if (convertedArray.size() > 0) {
                        searchRecycler = (RecyclerView) findViewById(R.id.searchRecycler);
                        searchRecycler.setHasFixedSize(true);

                        GridLayoutManager mLayoutManager = new GridLayoutManager(thisContext, 2);
                        searchRecycler.setLayoutManager(mLayoutManager);

                        Resources r = getResources();
                        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, r.getDisplayMetrics());

                        PostableMovieResultAdapter searchAdapter = new PostableMovieResultAdapter(thisContext, convertedArray);
                        searchRecycler.setAdapter(searchAdapter);
                    }
                    else {
                        Toast.makeText(MovieSearchResultsActivity.this,"An error occurred.", Toast.LENGTH_SHORT).show();
                    }

                    progressBar.setVisibility(View.INVISIBLE);

                } catch (org.json.JSONException e) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(MovieSearchResultsActivity.this,"An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                    Log.e("JSON Error", "Unable to get JSON Object Array");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(MovieSearchResultsActivity.this,"An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                Log.e("JSON Error", "Unable to get JSON Object Array");
            }
        });
        queue.add(jsonObjectRequest);
    }

}
