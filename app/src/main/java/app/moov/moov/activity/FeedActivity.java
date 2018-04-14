package app.moov.moov.activity;

/**
 * Created by Sammy on 3/3/18.
 * Loads and displays the feed
 * and manages user interactions
 * with the toolbar.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import app.moov.moov.model.Post;
import app.moov.moov.R;
import app.moov.moov.util.PaginatingPostsActivity;
import app.moov.moov.util.PaginationRecyclerAdapter;

public class FeedActivity extends PaginatingPostsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolBarSetup(toolbar);

        BottomNavigationView navBar = (BottomNavigationView) findViewById(R.id.navBar);
        setUpNavBar(navBar);

        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        baseRef = database.getReference();
        postsRef = baseRef.child("Users").child(uid).child("Feed");
        setupDatabaseRefs();

        setUIViews();
    }

    /**
     * Initialize layout objects, called in onCreate
     */
    private void setUIViews() {
        feedRecycler = (RecyclerView) findViewById(R.id.feedRecycler);
        feedRecycler.setHasFixedSize(true);

        orderedManager = new LinearLayoutManager(this);

        feedRecycler.setLayoutManager(orderedManager);
        feedRecycler.setItemAnimator(new DefaultItemAnimator());

        initPostLoad();

    }
}
