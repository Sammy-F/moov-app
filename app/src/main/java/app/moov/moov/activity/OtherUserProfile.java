package app.moov.moov.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import app.moov.moov.model.Post;
import app.moov.moov.R;
import app.moov.moov.model.User;

public class OtherUserProfile extends ToolbarBaseActivity {

    private String thisUserID;
    private String currentUID;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private DatabaseReference usersRef;

    private DatabaseReference currentUserRef;
    private DatabaseReference thisUserRef;

    private TextView tvNumFollowers;
    private TextView tvNumFollowing;
    private TextView tvUsername;
    private Button btnFollow;

    private RecyclerView userRecycler;
    private LinearLayoutManager orderedManager;

    private Context thisContext;

    private LinearLayout llFollowers;
    private LinearLayout llFollowing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolBarSetup(toolbar);

        thisContext = this;

        thisUserID = getIntent().getStringExtra("thisUserID");
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();
        usersRef = ref.child("Users");
        currentUID = firebaseAuth.getCurrentUser().getUid();
        currentUserRef = usersRef.child(currentUID);
        thisUserRef = usersRef.child(thisUserID);

        setUIViews();
    }

    private void setUIViews() {

        tvUsername = (TextView) findViewById(R.id.tvUsername);
        btnFollow = (Button) findViewById(R.id.btnFollow);

        tvNumFollowers = (TextView) findViewById(R.id.tvNumFollowers);
        tvNumFollowing = (TextView) findViewById(R.id.tvNumFollowing);

        llFollowers = (LinearLayout) findViewById(R.id.llFollowers);
        llFollowing = (LinearLayout) findViewById(R.id.llFollowing);

        llFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OtherUserProfile.this, FollowersFollowingActivity.class);
                intent.putExtra("type", "followers");
                intent.putExtra("uid", thisUserID);
                startActivity(intent);
            }
        });

        llFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OtherUserProfile.this, FollowersFollowingActivity.class);
                intent.putExtra("type", "following");
                intent.putExtra("uid", thisUserID);
                startActivity(intent);
            }
        });


        userRecycler = (RecyclerView) findViewById(R.id.userRecycler);
        userRecycler.setHasFixedSize(true);

        orderedManager = new LinearLayoutManager(this);
//        orderedManager.setReverseLayout(true);
//        orderedManager.setStackFromEnd(true);

        userRecycler.setLayoutManager(orderedManager);

        // Set the text for the follow/unfollow button
        thisUserRef.child("Followers").child(currentUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    btnFollow.setText("Follow");
                }
                else {
                    btnFollow.setText("Unfollow");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        // Display number of user's followers
        thisUserRef.child("Followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tvNumFollowers.setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Display how many people the user is following
        thisUserRef.child("Following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tvNumFollowing.setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /**
         * Inside method gets user's posts and displays them
         */


        /**
         * Inside gets current user's username and sets the
         * TextView to display it
         */
        thisUserRef.child("Username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = dataSnapshot.getValue(String.class);

                tvUsername.setText(username);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(OtherUserProfile.this,"Getting username failed.", Toast.LENGTH_SHORT).show();

            }
        });

        // Handle following/unfollowing when button is clicked
        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                thisUserRef.child("Followers").child(currentUID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            thisUserRef.child("Followers").child(currentUID).setValue(true);
                            currentUserRef.child("Following").child(thisUserID).setValue(true);
                            btnFollow.setText("Unfollow");
                        }
                        else {
                            thisUserRef.child("Followers").child(currentUID).removeValue();
                            currentUserRef.child("Following").child(thisUserID).removeValue();
                            btnFollow.setText("Follow");
//                            Toast.makeText(OtherUserProfile.this,"You already follow them.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    @Override
    protected void onStart() {

        super.onStart();

        Query query = thisUserRef.child("Posts").orderByChild("timestamp");

        FirebaseRecyclerOptions<Post> options =
                new FirebaseRecyclerOptions.Builder<Post>()
                        .setIndexedQuery(query, ref.child("Posts"), Post.class)
                        .build();

        /**
         * Internal Adapter for use with the RecyclerView
         */
        FirebaseRecyclerAdapter <Post, ProfileFeedHolder> FBRA = new FirebaseRecyclerAdapter<Post, ProfileFeedHolder>(options) {
            @Override
            public ProfileFeedHolder onCreateViewHolder(ViewGroup parent, int ViewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.new_cv_layout, parent, false);

                return new ProfileFeedHolder(view);
            }

            @Override
            protected void onBindViewHolder(ProfileFeedHolder viewHolder, int position, Post model) {

                final ProfileFeedHolder viewHolder1 = viewHolder;

                viewHolder.setTitle(model.getMovieTitle());
                viewHolder.setRating(Float.parseFloat(model.getMovieRating()));
                viewHolder.setReview(model.getMovieReview());
                if (model.getMovieReview().equals("")) {
                    viewHolder.getReviewView().setTextSize(0);
                    viewHolder.getReviewView().setPadding(0, 0, 0, 0);
                    viewHolder.getReviewView().setVisibility(View.GONE);
                    viewHolder.getReviewView().setHeight(0);
                }
                viewHolder.setUsername(model.getUsername());

                String posterUrl = model.getPosterURL();
                Glide.with(thisContext).asBitmap().load(posterUrl).into(viewHolder.ivPoster);

//                try {
//                    Glide.with(thisContext).asBitmap().load(posterUrl).into(viewHolder.getIvPoster());
//                }  catch (NullPointerException e) {
//                    Log.e("Image skipped", "Unable to get image for " + posterUrl);
//                }

                final int movieID = model.getMovieID();

                viewHolder.getIvPoster().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) { //temporary button response
                        Intent intent = new Intent(thisContext, MovieProfile.class);
                        intent.putExtra("movieID", movieID);
                        startActivity(intent); //go to movie's profile
//                        MovieGetterByID movieGetter = new MovieGetterByID(thisContext, movieID);
//                        MovieDb thisMovie = movieGetter.getMovie();
//                        String movieTitle = thisMovie.getTitle();
//                        Toast.makeText(FeedActivity.this,movieTitle, Toast.LENGTH_SHORT).show();
                    }
                });


            }
        };
        FBRA.startListening();
        userRecycler.setAdapter(FBRA);
    }

    /**
     * ViewHolder class for use on the profile
     */
    public static class ProfileFeedHolder extends RecyclerView.ViewHolder {

        private TextView btnMovieTitle;
        private TextView movieReview;
        private ImageView ivPoster;

        public ProfileFeedHolder(View itemView) {
            super(itemView);
            View mView = itemView;
            btnMovieTitle = itemView.findViewById(R.id.MovieTitle);
            ivPoster = itemView.findViewById(R.id.ivPoster);
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

    }

}
