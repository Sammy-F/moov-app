package app.moov.moov.activity;

/**
 * Created by EFrost on 3/19/2018.
 * Modified by Sammy on 3/27/2018.
 */

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
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import app.moov.moov.archived.FindUserActivity;
import app.moov.moov.model.Post;
import app.moov.moov.R;

public class MovieProfile extends ToolbarBaseActivity {

    private Context thisContext;

    private int movieID;
    private String movieTitle;


    private String currentUID;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private DatabaseReference postsRef;

    private DatabaseReference movieRef;
    private DatabaseReference thisUserRef;
    private DatabaseReference currentUserRef;
    private DatabaseReference thisMoviePostsRef;

    private TextView tvNumFollowers;
    private TextView tvNumFollowing;
    private TextView tvMoviename;
    private TextView tvMovieDesc;
    private Button btnFollow;

    private RecyclerView userRecycler;
    private LinearLayoutManager orderedManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolBarSetup(toolbar);

        thisContext = this;

        movieID = getIntent().getIntExtra("movieID", 0);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUID = firebaseAuth.getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();
        postsRef = ref.child("Posts");
        thisMoviePostsRef = ref.child("PostsByMovie").child(Integer.toString(movieID));
//        movieRef = postsRef.child("movieTitle");

        setUIViews();
    }

    private void setUIViews() {

        tvMoviename = (TextView) findViewById(R.id.tvMoviename);
        tvMoviename.setText(movieTitle);
        btnFollow = (Button) findViewById(R.id.btnFollow);

//        tvNumFollowers = (TextView) findViewById(R.id.tvNumFollowers);
//        tvNumFollowing = (TextView) findViewById(R.id.tvNumFollowing);

        userRecycler = (RecyclerView) findViewById(R.id.userRecycler);
        userRecycler.setHasFixedSize(true);

        orderedManager = new LinearLayoutManager(this);
        orderedManager.setReverseLayout(true);
        orderedManager.setStackFromEnd(true);

        userRecycler.setLayoutManager(orderedManager);

//        thisUserRef.child("Followers").child(currentUID).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (!dataSnapshot.exists()) {
//                    btnFollow.setText("Follow");
//                }
//                else {
//                    btnFollow.setText("Unfollow");
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//
//        thisUserRef.child("Followers").addValueEventListener(new ValueEventListener() {
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
//        thisUserRef.child("Following").addValueEventListener(new ValueEventListener() {
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

        /**
         * Inside method gets user's posts and displays them
         */


        /**
         * Inside gets current user's username and sets the
         * TextView to display it,
         * currently sets the name of TextView to be whatever search was entered, regardless of legitimacy
         */
//        movieRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String moviename = dataSnapshot.getValue(String.class);
//
//                tvMoviename.setText(moviename);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Toast.makeText(MovieProfile.this,"Getting Movie Failed", Toast.LENGTH_SHORT).show();
//
//            }
//        });

//        btnFollow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                thisUserRef.child("Followers").child(currentUID).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if (!dataSnapshot.exists()) {
//                            thisUserRef.child("Followers").child(currentUID).setValue(true);
//                            currentUserRef.child("Following").child(thisUserID).setValue(true);
//                            btnFollow.setText("Unfollow");
//                        }
//                        else {
//                            thisUserRef.child("Followers").child(currentUID).removeValue();
//                            currentUserRef.child("Following").child(thisUserID).removeValue();
//                            btnFollow.setText("Follow");
////                            Toast.makeText(OtherUserProfile.this,"You already follow them.", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//            }
//        });

    }

    @Override
    protected void onStart() {

        super.onStart();

        Query keysQuery = thisMoviePostsRef.orderByChild("timestamp");

        FirebaseRecyclerOptions<Post> options =
                new FirebaseRecyclerOptions.Builder<Post>()
                        .setIndexedQuery(keysQuery, postsRef, Post.class)
                        .build();

        FirebaseRecyclerAdapter<Post, MovieProfile.MovieProfileHolder> FBRA = new FirebaseRecyclerAdapter<Post, MovieProfile.MovieProfileHolder>(options) {

            @Override
            public MovieProfile.MovieProfileHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.cv_layout, parent, false);

                return new MovieProfile.MovieProfileHolder(view);
            }

            @Override
            protected void onBindViewHolder(MovieProfile.MovieProfileHolder viewHolder, int position, Post model) {

                final MovieProfile.MovieProfileHolder viewHolder1 = viewHolder;

                viewHolder.setTitle(model.getMovieTitle());
                viewHolder.setRating(model.getMovieRating());
                viewHolder.setReview(model.getMovieReview());
                viewHolder.setUsername(model.getUsername());


            }
        };
        FBRA.startListening();
        userRecycler.setAdapter(FBRA);

    }

    public static class MovieProfileHolder extends RecyclerView.ViewHolder {

        public MovieProfileHolder(View itemView) {
            super(itemView);
            View mView = itemView;
        }

        public void setTitle(String title) {
            TextView movieTitle = (TextView) itemView.findViewById(R.id.MovieTitle);
            movieTitle.setText(title);
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

    }


}
