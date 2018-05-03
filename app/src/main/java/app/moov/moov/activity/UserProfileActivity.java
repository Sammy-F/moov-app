package app.moov.moov.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import app.moov.moov.model.Post;
import app.moov.moov.R;
import app.moov.moov.util.PaginatingPostsActivity;
import app.moov.moov.util.SelfProfileSwitchingAdapter;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * UserProfileActivity displays the user's profile
 * and handles certain interactions with items within
 * the profile.
 *
 * Modified by Sammy 3/25/18
 */

public class UserProfileActivity extends PaginatingPostsActivity {

    private SwipeRefreshLayout swipeContainer;

    private Context thisContext;

    private TextView tvUsername;
    private TextView tvNumFollowing;
    private TextView tvNumFollowers;
    private TextView tvFullName;

    private String username;
    private String userID;

//    private FirebaseAuth firebaseAuth;
//    private FirebaseDatabase database;
//    private DatabaseReference baseRef;
//    private DatabaseReference postsRef;
    //    private RecyclerView profileFeedRecycler;
    //    private Context thisContext;

    private DatabaseReference allUsers;

    private DatabaseReference userRef;

    private LinearLayout llFollowers;
    private LinearLayout llFollowing;

    private CircleImageView ivAvatar;
    private FirebaseStorage firebaseStorage;
    private StorageReference avatarRef;

    final long ONE_MEGABYTE = 1024 * 1024;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolBarSetup(toolbar);
        BottomNavigationView navBar = (BottomNavigationView) findViewById(R.id.navBar);
        BottomNavigationViewHelper.disableShiftMode(navBar);
        setUpNavBar(navBar);

        thisContext = this;

        //Swipe container is currently bugged
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
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

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        final String uid = user.getUid();
        userID = user.getUid();

        firebaseStorage = FirebaseStorage.getInstance();
        avatarRef = firebaseStorage.getReference().child("images").child("avatars").child(userID + ".png");

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference baseRef = database.getReference();
        DatabaseReference postsRef = database.getReference().child("Users").child(uid).child("Posts");
        RecyclerView feedRecycler = (RecyclerView) findViewById(R.id.profileRecycler);

        paginationSetup(thisContext, firebaseAuth, database, baseRef, postsRef, feedRecycler);

    }

    private void activitySetup() {

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        toolBarSetup(toolbar);

        Context thisContext = this;

//        BottomNavigationViewEx navBar = (BottomNavigationViewEx) findViewById(R.id.navBar);
//        setUpNavBar(navBar);

        // initialize the variables in the super class
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String uid = firebaseAuth.getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference baseRef = database.getReference();
        DatabaseReference postsRef = baseRef.child("Users").child(uid).child("Feed");

        RecyclerView feedRecycler = (RecyclerView) findViewById(R.id.feedRecycler);

        paginationSetup(thisContext, firebaseAuth, database, baseRef, postsRef, feedRecycler);

    }

}
