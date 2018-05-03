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
import app.moov.moov.util.FirebaseSwitchingAdapter;
import app.moov.moov.util.PaginatingPostsActivity;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * This class is the page for user profiles (other than the user who is using the app)
 */
public class OtherUserProfile extends PaginatingPostsActivity {

    private SwipeRefreshLayout swipeContainer;

    private Context thisContext;
    private String thisUserID;
    private DatabaseReference usersRef;
    private DatabaseReference thisUserRef;
    private FirebaseStorage firebaseStorage;
    private StorageReference avatarRef;


    /**
     * Initializes all of the object (like toolbar, bottom nav bar), sets up data references
     * and specifies which layout XML to use
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolBarSetup(toolbar);

        BottomNavigationView navBar = (BottomNavigationView) findViewById(R.id.navBar);
        BottomNavigationViewHelper.disableShiftMode(navBar);
        setUpNavBar(navBar);

        thisContext = this;

        thisUserID = getIntent().getStringExtra("thisUserID");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference baseRef = database.getReference();
        usersRef = baseRef.child("Users");
        thisUserRef = usersRef.child(thisUserID);
        DatabaseReference postsRef = thisUserRef.child("Posts");

        firebaseStorage = FirebaseStorage.getInstance();
        avatarRef = firebaseStorage.getReference().child("images").child("avatars").child(thisUserID + ".png");

        RecyclerView feedRecycler = (RecyclerView) findViewById(R.id.userRecycler);


        otherUserProfilePaginationSetup(thisContext, firebaseAuth, database, baseRef, postsRef, feedRecycler, thisUserID);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
//                activitySetup();
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

}
