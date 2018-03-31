package app.moov.moov.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        thisContext = this;

        setUIViews();

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        final String uid = user.getUid();
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

        /**
         * Create custom adapter using ProfileFeedHolder,
         * populate using data pulled from Firebase.
         */
        FirebaseRecyclerAdapter<Post, UserProfileActivity.ProfileFeedHolder> FBRA = new FirebaseRecyclerAdapter<Post, UserProfileActivity.ProfileFeedHolder>(

                Post.class,
                R.layout.cv_own_post,
                UserProfileActivity.ProfileFeedHolder.class,
                userPostsRef.orderByChild("time")

        ) {
            @Override
            protected void populateViewHolder(UserProfileActivity.ProfileFeedHolder viewHolder, Post model, int position) {
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

        tvNumFollowers = (TextView) findViewById(R.id.tvNumFollowers);
        tvNumFollowing = (TextView) findViewById(R.id.tvNumFollowing);

        profileFeedRecycler = (RecyclerView) findViewById(R.id.profileRecycler);
        profileFeedRecycler.setHasFixedSize(true);

        orderedManager = new LinearLayoutManager(this);
        orderedManager.setReverseLayout(true);
        orderedManager.setStackFromEnd(true);

        profileFeedRecycler.setLayoutManager(orderedManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_feed_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id==R.id.addIcon) {
            Intent intent = new Intent(UserProfileActivity.this,ChooseMovieActivity.class);
            startActivity(intent);
        }

        if (id==R.id.logoutIcon) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(UserProfileActivity.this, MainActivity.class));
        }

        if (id==R.id.searchIcon) {
            Intent intent = new Intent(UserProfileActivity.this,SearchActivity.class);
            startActivity(intent);
        }

        if (id==R.id.profileIcon) {
            //save time by not creating a new intent just to return to the same page
//            Intent intent = new Intent(UserProfileActivity.this,UserProfileActivity.class);
//            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

}
