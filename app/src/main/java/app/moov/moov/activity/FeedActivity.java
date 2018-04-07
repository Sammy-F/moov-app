package app.moov.moov.activity;

/**
 * Created by Sammy on 3/3/18.
 * Loads and displays the feed
 * and manages user interactions
 * with the toolbar.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenuItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import app.moov.moov.model.Post;
import app.moov.moov.R;
import app.moov.moov.util.FirebaseSwitchingAdapter;

public class FeedActivity extends ToolbarBaseActivity {

    private RecyclerView feedRecycler;
    private FirebaseDatabase database;
    private DatabaseReference baseRef;
    private DatabaseReference postsRef;
    private FirebaseAuth firebaseAuth;
    private LinearLayoutManager orderedManager;
    private String uid;
    private Context thisContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolBarSetup(toolbar);

        BottomNavigationView navBar = (BottomNavigationView) findViewById(R.id.navBar);
        setUpNavBar(navBar);

        thisContext = this;

        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        baseRef = database.getReference();
        postsRef = baseRef.child("Users").child(uid).child("Feed");

        setUIViews();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Query keysQuery = postsRef.orderByChild("timestamp").limit(25);

        FirebaseRecyclerOptions<Post> options = new FirebaseRecyclerOptions.Builder<Post>()
                .setIndexedQuery(keysQuery, baseRef.child("Posts"), Post.class)
                .build();

        FirebaseSwitchingAdapter FBRA = new FirebaseSwitchingAdapter(options, thisContext);
        FBRA.startListening();
        feedRecycler.setAdapter(FBRA);
    }

    /**
     * Initialize layout objects, called in onCreate
     */
    private void setUIViews() {
        feedRecycler = (RecyclerView) findViewById(R.id.feedRecycler);
        feedRecycler.setHasFixedSize(true);

        orderedManager = new LinearLayoutManager(this);
//        orderedManager.setReverseLayout(true);
//        orderedManager.setStackFromEnd(true);

        feedRecycler.setLayoutManager(orderedManager);

    }
}
