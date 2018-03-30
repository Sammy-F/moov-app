package app.moov.moov.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import app.moov.moov.archived.FindUserActivity;
import app.moov.moov.util.JSONMovieResultsAdapter;
import app.moov.moov.R;
import app.moov.moov.util.MovieSearchResultAdapter;
import app.moov.moov.util.VolleySingleton;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

public class MovieSearchResultsActivity extends AppCompatActivity {

    private RecyclerView searchRecycler;
    private String searchQuery;

//    private List<MovieDb> searchResults;

    private FirebaseAuth firebaseAuth;

    private Context thisContext;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_search_results);

        thisContext = this;

        firebaseAuth = FirebaseAuth.getInstance();
        searchQuery = getIntent().getStringExtra("searchQuery");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUIViews();
    }

    /**
     * Initialize and prepare UI Views for display
     * Called in onCreate
     */
    private void setUIViews() {

        final ProgressBar progressBar = findViewById(R.id.resultsProgress);
        progressBar.setVisibility(View.VISIBLE);

        //run search handler
//        APISearchHandler myHandler = new APISearchHandler(searchQuery);
//        myHandler.execute();
//
//        try {
//            searchResults = myHandler.get();
//        } catch (InterruptedException e) {
//            searchResults = null;
//        } catch (ExecutionException f) {
//            searchResults = null;
//        }
//
//        myHandler.cancel(true);

        searchQuery = searchQuery.replaceAll(" ", "+");

        String url = "https://api.themoviedb.org/3/search/movie?api_key=3744632a440f06514578b01d1b6e9d27&query=" + searchQuery;

        RequestQueue queue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
//        queue = new RequestQueue(cache, network);
//        queue.start();

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
                    } else if (convertedArray.size() > 0 && convertedArray.size() <= 20) {
                        searchRecycler = (RecyclerView) findViewById(R.id.searchRecycler);
                        searchRecycler.setHasFixedSize(true);

                        LinearLayoutManager mLayoutManager = new LinearLayoutManager(thisContext);
                        searchRecycler.setLayoutManager(mLayoutManager);

                        JSONMovieResultsAdapter searchAdapter = new JSONMovieResultsAdapter(thisContext, convertedArray);
                        searchRecycler.setAdapter(searchAdapter);
                    }
                    else if (convertedArray.size() > 20) { // Cap the result size to 20
                        convertedArray = convertedArray.subList(0, 19);
                        searchRecycler = (RecyclerView) findViewById(R.id.searchRecycler);
                        searchRecycler.setHasFixedSize(true);

                        LinearLayoutManager mLayoutManager = new LinearLayoutManager(thisContext);
                        searchRecycler.setLayoutManager(mLayoutManager);

                        JSONMovieResultsAdapter searchAdapter = new JSONMovieResultsAdapter(thisContext, convertedArray);
                        searchRecycler.setAdapter(searchAdapter);
                    }
                    else {
                        Toast.makeText(MovieSearchResultsActivity.this,"No Results Found", Toast.LENGTH_SHORT).show();
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

//        if (searchResults == null) {
//            Toast.makeText(MovieSearchResultsActivity.this,"No Results Found", Toast.LENGTH_SHORT).show();
//        } else if (searchResults.size() > 0 && searchResults.size() <= 20) {
//            searchRecycler = (RecyclerView) findViewById(R.id.searchRecycler);
//            searchRecycler.setHasFixedSize(true);
//
//            LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
//            searchRecycler.setLayoutManager(mLayoutManager);
//
//            MovieResultsAdapter searchAdapter = new MovieResultsAdapter(this, searchResults);
//            searchRecycler.setAdapter(searchAdapter);
//        }
//        else if (searchResults.size() > 20) { // Cap the result size to 20
//            searchResults = searchResults.subList(0, 19);
//            searchRecycler = (RecyclerView) findViewById(R.id.searchRecycler);
//            searchRecycler.setHasFixedSize(true);
//
//            LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
//            searchRecycler.setLayoutManager(mLayoutManager);
//
//            MovieResultsAdapter searchAdapter = new MovieResultsAdapter(this, searchResults);
//            searchRecycler.setAdapter(searchAdapter);
//        }
//        else {
//            Toast.makeText(MovieSearchResultsActivity.this,"No Results Found", Toast.LENGTH_SHORT).show();
//        }

    }

    /**
     * Internal class handles searches in separate thread.
     */
//    static class APISearchHandler extends AsyncTask<String, Void, List<MovieDb>> {
//        private List<MovieDb> resultList;
//        private String searchQuery;
//        Exception exception;
//
//        public APISearchHandler(String searchQuery) {
//            super();
//            this.searchQuery = searchQuery;
//        }
//
//        /**
//         * Processes run when execute() is called
//         * @param searchQueries
//         * @return
//         */
//        protected List<MovieDb> doInBackground(String[] searchQueries) {
//            try {
//                TmdbSearch movieSearch = new TmdbSearch(new TmdbApi("3744632a440f06514578b01d1b6e9d27")); //TODO: Save API Key elsewhere
//                MovieResultsPage results = movieSearch.searchMovie(searchQuery, null, "en", false, 0);
//                resultList = results.getResults();
//                return resultList;
//            } catch (Exception e) {
//                this.exception = e;
//                return null;
//            }
//        }
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id==R.id.addIcon) {
            Intent intent = new Intent(MovieSearchResultsActivity.this,PostActivity.class);
            startActivity(intent);
        }

        if (id==R.id.logoutIcon) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(MovieSearchResultsActivity.this, MainActivity.class));
        }

        if (id==R.id.profileIcon) {
            Intent intent = new Intent(MovieSearchResultsActivity.this, UserProfileActivity.class);
            startActivity(intent);
        }

        if (id==R.id.searchIcon) {
            Intent intent = new Intent(MovieSearchResultsActivity.this,FindUserActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

}
