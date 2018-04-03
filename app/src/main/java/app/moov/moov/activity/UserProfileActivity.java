package app.moov.moov.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import app.moov.moov.model.Post;
import app.moov.moov.R;
import app.moov.moov.util.FirebaseSwitchingAdapter;
import app.moov.moov.util.SelfProfileSwitchingAdapter;

/**
 * UserProfileActivity displays the user's profile
 * and handles certain interactions with items within
 * the profile.
 *
 * Modified by Sammy 3/25/18
 */

public class UserProfileActivity extends ToolbarBaseActivity {

    private TextView tvUsername;
    private TextView tvNumFollowing;
    private TextView tvNumFollowers;

    private String username;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private DatabaseReference userRef;
    private DatabaseReference userPostsRef;
    private DatabaseReference allUsers;

    private RecyclerView profileFeedRecycler;
    private LinearLayoutManager orderedManager;

    private Context thisContext;

    private String userID;

    private LinearLayout llFollowers;
    private LinearLayout llFollowing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        BottomNavigationView navBar = (BottomNavigationView) findViewById(R.id.navBar);
        setUpNavBar(navBar);

        thisContext = this;

        setUIViews();

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        final String uid = user.getUid();
        userID = user.getUid();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();
        userRef = database.getReference().child("Users").child(uid);
        userPostsRef = database.getReference().child("Users").child(uid).child("Posts");
        allUsers = ref.child("Users");

        // Set text for number of Followers user has
        userRef.child("Followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tvNumFollowers.setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Set next for number of users the user Follows
        userRef.child("Following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tvNumFollowing.setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        /**
         * Inside gets current user's username
         */
        userRef.child("Username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                username = dataSnapshot.getValue(String.class);

                tvUsername.setText(username);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UserProfileActivity.this,"Getting username failed.", Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    protected void onStart() {

        super.onStart();

        Query query = userPostsRef.orderByChild("timestamp");

        FirebaseRecyclerOptions<Post> options = new FirebaseRecyclerOptions.Builder<Post>()
                .setIndexedQuery(query, ref.child("Posts"), Post.class)
                .build();

        SelfProfileSwitchingAdapter FBRA = new SelfProfileSwitchingAdapter(options, thisContext);

        FBRA.startListening();
        profileFeedRecycler.setAdapter(FBRA);

    }

    /**
     * Internal ViewHolder class used with
     * the custom Adapter for our profile
     * RecyclerView
     */
    public static class ProfileFeedHolder extends RecyclerView.ViewHolder {

        private TextView btnMovieTitle;
        private TextView movieReview;
        private ImageView ivPoster;
        private Button editButton;
        private Button delButton;

        public ProfileFeedHolder(View itemView) {
            super(itemView);
            View mView = itemView;
            btnMovieTitle = itemView.findViewById(R.id.MovieTitle);
            ivPoster = itemView.findViewById(R.id.ivPoster);
            delButton = itemView.findViewById(R.id.btnDelete);
            editButton = itemView.findViewById(R.id.btnEdit);
        }

        public void setTitle(String title) {
            btnMovieTitle.setText(title);
        }

        public void setRating(float rating) {
            RatingBar movieRating = (RatingBar) itemView.findViewById(R.id.ratingBar);
            movieRating.setIsIndicator(true);
            movieRating.setRating(rating);
        }

        public void setReview(String review) {
            movieReview = (TextView) itemView.findViewById(R.id.MovieReview);
            movieReview.setText(review);
        }

        public TextView getReviewView() { return movieReview; }

        public void setUsername(String username) {
            TextView userName = (TextView) itemView.findViewById(R.id.Username);
            userName.setText(username);
        }

        public TextView getBtnMovieTitle() { return btnMovieTitle; }

        public ImageView getIvPoster() {
            return ivPoster;
        }

        public Button getDelButton() { return delButton; }

        public Button getEditButton() { return editButton; }

    }

    /**
     * Initialize layout variables
     * Called in onStart
     */
    private void setUIViews() {
        tvUsername = (TextView) findViewById(R.id.tvUsername);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        toolBarSetup(toolbar);

        tvNumFollowers = (TextView) findViewById(R.id.tvNumFollowers);
        tvNumFollowing = (TextView) findViewById(R.id.tvNumFollowing);

        llFollowers = (LinearLayout) findViewById(R.id.llFollowers);
        llFollowing = (LinearLayout) findViewById(R.id.llFollowing);

        llFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfileActivity.this, FollowersFollowingActivity.class);
                intent.putExtra("type", "followers");
                intent.putExtra("uid", userID);
                startActivity(intent);
            }
        });

        llFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfileActivity.this, FollowersFollowingActivity.class);
                intent.putExtra("type", "following");
                intent.putExtra("uid", userID);
                startActivity(intent);
            }
        });


        profileFeedRecycler = (RecyclerView) findViewById(R.id.profileRecycler);
        profileFeedRecycler.setHasFixedSize(true);

        orderedManager = new LinearLayoutManager(this);
//        orderedManager.setReverseLayout(true);
//        orderedManager.setStackFromEnd(true);

        profileFeedRecycler.setLayoutManager(orderedManager);
    }

}
