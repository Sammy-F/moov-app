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

//        BottomNavigationViewEx navBar = (BottomNavigationViewEx) findViewById(R.id.navBar);
//        setUpNavBar(navBar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolBarSetup(toolbar);
        BottomNavigationView navBar = (BottomNavigationView) findViewById(R.id.navBar);
        BottomNavigationViewHelper.disableShiftMode(navBar);
        setUpNavBar(navBar);

        thisContext = this;

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        final String uid = user.getUid();
        userID = user.getUid();

        firebaseStorage = FirebaseStorage.getInstance();
        avatarRef = firebaseStorage.getReference().child("images").child("avatars").child(userID + ".png");

//        setUIViews();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference baseRef = database.getReference();
//        userRef = database.getReference().child("Users").child(uid);
        DatabaseReference postsRef = database.getReference().child("Users").child(uid).child("Posts");
//        allUsers = baseRef.child("Users");
        RecyclerView feedRecycler = (RecyclerView) findViewById(R.id.profileRecycler);

        // Set text for number of Followers user has
//        userRef.child("Followers").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                tvNumFollowers.setText(String.valueOf(dataSnapshot.getChildrenCount()));
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//        // Set next for number of users the user Follows
//        userRef.child("Following").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                tvNumFollowing.setText(String.valueOf(dataSnapshot.getChildrenCount()));
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//        /**
//         * Inside gets current user's username
//         */
//        userRef.child("Username").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                username = dataSnapshot.getValue(String.class);
//
//                tvUsername.setText(username);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Toast.makeText(UserProfileActivity.this,"Getting username failed.", Toast.LENGTH_SHORT).show();
//
//            }
//        });
//
//        userRef.child("FirstName").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String firstName = dataSnapshot.getValue() + " ";
//                tvFullName.setText(firstName);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//        userRef.child("LastName").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String fullName = tvFullName.getText().toString() + dataSnapshot.getValue(String.class);
//                tvFullName.setText(fullName);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        paginationSetup(thisContext, firebaseAuth, database, baseRef, postsRef, feedRecycler);



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

//    @Override
//    protected void onStart() {
//
//        super.onStart();
//
//        Query query = postsRef.orderByChild("timestamp");
//
//        FirebaseRecyclerOptions<Post> options = new FirebaseRecyclerOptions.Builder<Post>()
//                .setIndexedQuery(query, baseRef.child("Posts"), Post.class)
//                .build();
//
//        SelfProfileSwitchingAdapter FBRA = new SelfProfileSwitchingAdapter(options, thisContext);
//
//        FBRA.startListening();
//        profileFeedRecycler.setAdapter(FBRA);
//
//    }

    /**
     * Internal ViewHolder class used with
     * the custom Adapter for our profile
     * RecyclerView
     */
//    public static class ProfileFeedHolder extends RecyclerView.ViewHolder {
//
//        private TextView btnMovieTitle;
//        private TextView movieReview;
//        private ImageView ivPoster;
//        private Button editButton;
//        private Button delButton;
//
//        public ProfileFeedHolder(View itemView) {
//            super(itemView);
//            View mView = itemView;
//            btnMovieTitle = itemView.findViewById(R.id.MovieTitle);
//            ivPoster = itemView.findViewById(R.id.ivPoster);
//        }
//
//        public void setTitle(String title) {
//            btnMovieTitle.setText(title);
//        }
//
//        public void setRating(float rating) {
//            RatingBar movieRating = (RatingBar) itemView.findViewById(R.id.ratingBar);
//            movieRating.setIsIndicator(true);
//            movieRating.setRating(rating);
//        }
//
//        public void setReview(String review) {
//            movieReview = (TextView) itemView.findViewById(R.id.MovieReview);
//            movieReview.setText(review);
//        }
//
//        public TextView getReviewView() { return movieReview; }
//
//        public void setUsername(String username) {
//            TextView userName = (TextView) itemView.findViewById(R.id.Username);
//            userName.setText(username);
//        }
//
//        public TextView getBtnMovieTitle() { return btnMovieTitle; }
//
//        public ImageView getIvPoster() {
//            return ivPoster;
//        }
//
//        public Button getDelButton() { return delButton; }
//
//        public Button getEditButton() { return editButton; }
//
//    }

    private void activitySetup() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolBarSetup(toolbar);

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
