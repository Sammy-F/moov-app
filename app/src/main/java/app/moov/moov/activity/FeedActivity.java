package app.moov.moov.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import app.moov.moov.R;
import app.moov.moov.util.ConnectionTester;
import app.moov.moov.util.PaginatingPostsActivity;

/**
 * Created by Sammy on 3/3/18.
 * Loads and displays the feed
 * and manages user interactions
 * with the toolbar. Also included a pull to refresh feature
 */
public class FeedActivity extends PaginatingPostsActivity {

    private SwipeRefreshLayout swipeContainer;
    private ProgressBar progressBar;

    /**
     * Initializes all of the object (like toolbar, bottom nav bar), sets up data references
     * and specifies which layout XML to use
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        BottomNavigationView navBar = (BottomNavigationView) findViewById(R.id.navBar);
        BottomNavigationViewHelper.disableShiftMode(navBar);
        setUpNavBar(navBar);

        ConnectionTester connectionTester = new ConnectionTester();

    }


    @Override
    protected void onStart() {
        super.onStart();

        activitySetup();

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                activitySetup();
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

    private void activitySetup() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolBarSetup(toolbar);

        Context thisContext = this;

        // initialize the variables in the super class
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String uid = firebaseAuth.getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference baseRef = database.getReference();
        DatabaseReference postsRef = baseRef.child("Users").child(uid).child("Feed");

        final RecyclerView feedRecycler = (RecyclerView) findViewById(R.id.feedRecycler);

        feedRecycler.setVisibility(View.INVISIBLE);

        paginationSetup(thisContext, firebaseAuth, database, baseRef, postsRef, feedRecycler);

        feedRecycler.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                feedRecycler.setVisibility(View.VISIBLE);
                feedRecycler.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });


    }
}
