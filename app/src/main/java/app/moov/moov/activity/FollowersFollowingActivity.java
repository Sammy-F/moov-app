package app.moov.moov.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
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
//import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import app.moov.moov.R;
import app.moov.moov.model.User;
import app.moov.moov.util.UserResultViewHolder;

/**
 * This class is the page that shows all of the user's followers/following users, and is called when
 * the followers/following button is clicked on any user's profile.
 */
public class FollowersFollowingActivity extends ToolbarBaseActivity {

    private RecyclerView rvFollow;
    private SwipeRefreshLayout swipeContainer;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference baseRef;
    private DatabaseReference usersRef;
    private DatabaseReference userRef;
    private DatabaseReference followerRef;
    private DatabaseReference followingRef;

    private String resultsType;
    private String userID;

    private FirebaseStorage firebaseStorage;
    private StorageReference allAvRef;

    /**
     * Initializes all of the object (like toolbar, bottom nav bar), sets up data references
     * and specifies which layout XML to use
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers_following);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolBarSetup(toolbar);

        BottomNavigationView navBar = (BottomNavigationView) findViewById(R.id.navBar);
        BottomNavigationViewHelper.disableShiftMode(navBar);
        setUpNavBar(navBar);

        userID = getIntent().getStringExtra("uid");
        resultsType = getIntent().getStringExtra("type");

        firebaseAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        baseRef = database.getReference();
        usersRef = baseRef.child("Users");
        userRef = usersRef.child(userID);
        followerRef = userRef.child("Followers");
        followingRef = userRef.child("Following");

        setUIViews();

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

    /**
     * First, checks to see whether we want to populate the user's followers or following, then
     * populate the viewholder
     */
    @Override
    protected void onStart() {
        super.onStart();

        firebaseStorage = FirebaseStorage.getInstance();
        allAvRef = firebaseStorage.getReference().child("images").child("avatars");

        Query firebaseSearchQuery;

        if (resultsType.equals("followers")) {
            firebaseSearchQuery = followerRef;
        }
        else {
            firebaseSearchQuery = followingRef;
        }

        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setIndexedQuery(firebaseSearchQuery, usersRef, User.class)
                        .build();

        FirebaseRecyclerAdapter<User, UserResultViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, UserResultViewHolder>(options) {

            /**
             * Creates the view, specify which cardview layout we want to use for the view
             * @param parent
             * @param viewType
             * @return
             */
            @Override
            public UserResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.cv_user_search_result, parent, false);

                return new UserResultViewHolder(view);
            }

            /**
             * For each of the view, specify what data we want it to populate (The Full name, Username of user)
             * @param viewHolder
             * @param position
             * @param model
             */
            @Override
            protected void onBindViewHolder(final UserResultViewHolder viewHolder, int position, User model) {
                viewHolder.setUsername(model.getUsername()); //changed back
                viewHolder.setFullName(model.getFirstName() + " " + model.getLastName());
                String username = model.getlowername();
                database.getReference().child("lusernames").child(username).addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        uid = (String) dataSnapshot.getValue();
                        String uid = dataSnapshot.getValue(String.class);
                        viewHolder.setUID(uid);
                        StorageReference avatarRef = allAvRef.child(uid + ".png");
                        avatarRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Glide.with(FollowersFollowingActivity.this).asBitmap().load(uri.toString()).into(viewHolder.getIvAvatar());
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Clicked
                        if (viewHolder.getUid().equals(firebaseAuth.getCurrentUser().getUid())) {
                            Intent intent = new Intent(FollowersFollowingActivity.this, UserProfileActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Intent intent = new Intent(FollowersFollowingActivity.this, OtherUserProfile.class);
                            intent.putExtra("thisUserID", viewHolder.getUid());
                            startActivity(intent);
                        }
                    }
                });
            }
        };
        firebaseRecyclerAdapter.startListening();
        rvFollow.setAdapter(firebaseRecyclerAdapter);


    }

    /**
     * Initialize the View variables used in
     * the Activity.
     */
    private void setUIViews() {

        rvFollow = (RecyclerView) findViewById(R.id.rvFollow);
        rvFollow.setHasFixedSize(true);
        rvFollow.setLayoutManager(new LinearLayoutManager(this));
    }



}
