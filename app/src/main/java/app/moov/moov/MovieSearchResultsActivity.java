package app.moov.moov;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import java.util.List;
import java.util.concurrent.ExecutionException;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

public class MovieSearchResultsActivity extends AppCompatActivity {

    private RecyclerView searchRecycler;
    private String searchQuery;

    private List<MovieDb> searchResults;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_search_results);

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

        APISearchHandler myHandler = new APISearchHandler(searchQuery);
        myHandler.execute();

        try {
            searchResults = myHandler.get();
        } catch (InterruptedException e) {
            searchResults = null;
        } catch (ExecutionException f) {
            searchResults = null;
        }

        myHandler.cancel(true);

        if (searchResults == null) {
            Toast.makeText(MovieSearchResultsActivity.this,"No Results Found", Toast.LENGTH_SHORT).show();
        } else if (searchResults.size() > 0 && searchResults.size() <= 20) {
            searchRecycler = (RecyclerView) findViewById(R.id.searchRecycler);
            searchRecycler.setHasFixedSize(true);

            LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
            searchRecycler.setLayoutManager(mLayoutManager);

            MovieResultsAdapter searchAdapter = new MovieResultsAdapter(this, searchResults);
            searchRecycler.setAdapter(searchAdapter);
        }
        else if (searchResults.size() > 20) { // Cap the result size to 20
            searchResults = searchResults.subList(0, 19);
            searchRecycler = (RecyclerView) findViewById(R.id.searchRecycler);
            searchRecycler.setHasFixedSize(true);

            LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
            searchRecycler.setLayoutManager(mLayoutManager);

            MovieResultsAdapter searchAdapter = new MovieResultsAdapter(this, searchResults);
            searchRecycler.setAdapter(searchAdapter);
        }
        else {
            Toast.makeText(MovieSearchResultsActivity.this,"No Results Found", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Internal class handles searches in separate thread.
     */
    static class APISearchHandler extends AsyncTask<String, Void, List<MovieDb>> {
        private List<MovieDb> resultList;
        private String searchQuery;
        Exception exception;

        public APISearchHandler(String searchQuery) {
            super();
            this.searchQuery = searchQuery;
        }

        /**
         * Processes run when execute() is called
         * @param searchQueries
         * @return
         */
        protected List<MovieDb> doInBackground(String[] searchQueries) {
            try {
                TmdbSearch movieSearch = new TmdbSearch(new TmdbApi("3744632a440f06514578b01d1b6e9d27")); //TODO: Save API Key elsewhere
                MovieResultsPage results = movieSearch.searchMovie(searchQuery, null, "en", false, 0);
                resultList = results.getResults();
                return resultList;
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
        }
    }

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
