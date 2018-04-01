package app.moov.moov.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

        /**
         * Create custom adapter using ProfileFeedHolder,
         * populate using data pulled from Firebase.
         */
        FirebaseRecyclerAdapter<Post, UserProfileActivity.ProfileFeedHolder> FBRA = new FirebaseRecyclerAdapter<Post, UserProfileActivity.ProfileFeedHolder>(options) {
            @Override
            public UserProfileActivity.ProfileFeedHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.cv_own_post, parent, false);

                return new UserProfileActivity.ProfileFeedHolder(view);
            }

            @Override
            protected void onBindViewHolder(UserProfileActivity.ProfileFeedHolder viewHolder, int position, Post model) {
                final Post model1 = model;

                viewHolder.setTitle(model.getMovieTitle());
                viewHolder.setRating(model.getMovieRating());
                viewHolder.setReview(model.getMovieReview());
                viewHolder.setUsername(model.getUsername());

                // Handles going to EditPostActivity when Edit Button is clicked

                viewHolder.getEditBtn().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(UserProfileActivity.this, EditPostActivity.class);
                        intent.putExtra("rating", model1.getMovieRating());
                        intent.putExtra("review", model1.getMovieReview());
                        intent.putExtra("movieTitle", model1.getMovieTitle());
                        intent.putExtra("postID", model1.getPID());
                        startActivity(intent);
                    }
                });

                // Handles going to MoviePage when title is clicked

                viewHolder.getMovieBtn().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final int movieID = model1.getMovieID();
                        // Temporary actions until movie page is finished.
                    }
                });

                // Handles deletion when Delete Post button is clicked
                viewHolder.getDelButton().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String pid = model1.getPID();
                        userRef.child("Feed").child(pid).removeValue();
                        userRef.child("Posts").child(pid).removeValue();
                        ref.child("Posts").child(pid).removeValue();

                        // Remove post from follower feeds
                        userRef.child("Followers").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot follower : dataSnapshot.getChildren()) {
                                    String UID = follower.getKey();

                                    allUsers.child(UID).child("Feed").child(pid).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                dataSnapshot.getRef().removeValue();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }
        };
        FBRA.startListening();
        profileFeedRecycler.setAdapter(FBRA);

    }

    /**
     * Internal ViewHolder class used with
     * the custom Adapter for our profile
     * RecyclerView
     */
    public static class ProfileFeedHolder extends RecyclerView.ViewHolder {

        Button deleteBtn;
        Button movieBtn;
        Button editBtn;

        public ProfileFeedHolder(View itemView) {
            super(itemView);
            View mView = itemView;
            this.deleteBtn = (Button) mView.findViewById(R.id.delBtn);
            this.movieBtn = (Button) mView.findViewById(R.id.MovieTitle);
            this.editBtn = (Button) mView.findViewById(R.id.editBtn);
        }

        public void setTitle(String title) {
            movieBtn.setText(title);
        }

        public void setRating(String rating) {
            TextView movieRating = (TextView) itemView.findViewById(R.id.MovieRating);
            movieRating.setText(rating);
        }

        public void setReview(String review) {
            TextView movieReview = (TextView) itemView.findViewById(R.id.MovieReview);
            movieReview.setText(review);
        }

        public void setUsername(String username) {
            TextView userName = (TextView) itemView.findViewById(R.id.Username);
            userName.setText(username);
        }

        public Button getDelButton() { return deleteBtn; }

        public Button getMovieBtn() { return movieBtn; }

        public Button getEditBtn() { return editBtn; }

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
        orderedManager.setReverseLayout(true);
        orderedManager.setStackFromEnd(true);

        profileFeedRecycler.setLayoutManager(orderedManager);
    }

}
