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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import app.moov.moov.archived.FindUserActivity;
import app.moov.moov.util.MovieGetterByID;
import app.moov.moov.model.Post;
import app.moov.moov.R;
import info.movito.themoviedbapi.model.MovieDb;

public class MovieProfile extends AppCompatActivity {

    private Context thisContext;

    private int movieID;
    private String movieTitle;

    private MovieDb movieDb;
    private MovieGetterByID movieGetter;

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

        movieGetter = new MovieGetterByID(this, movieID);
        movieDb = movieGetter.getMovie();

        movieTitle = movieDb.getTitle();

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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id==R.id.addIcon) {
            Intent intent = new Intent(MovieProfile.this,PostActivity.class);
            startActivity(intent);
        }

        if (id==R.id.logoutIcon) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(MovieProfile.this, MainActivity.class));
        }

        if (id==R.id.profileIcon) {
            Intent intent = new Intent(MovieProfile.this, UserProfileActivity.class);
            startActivity(intent);
        }

        if (id==R.id.searchIcon) {
            Intent intent = new Intent(MovieProfile.this,FindUserActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {

        super.onStart();

        FirebaseRecyclerAdapter<Post, MovieProfile.MovieProfileHolder> FBRA = new FirebaseRecyclerAdapter<Post, MovieProfile.MovieProfileHolder>(

                Post.class,
                R.layout.cv_layout,
                MovieProfile.MovieProfileHolder.class,
                thisMoviePostsRef.orderByChild("time")

        ) {
            @Override
            protected void populateViewHolder(MovieProfile.MovieProfileHolder viewHolder, Post model, int position) {

                final MovieProfile.MovieProfileHolder viewHolder1 = viewHolder;

                viewHolder.setTitle(model.getMovieTitle());
                viewHolder.setRating(model.getMovieRating());
                viewHolder.setReview(model.getMovieReview());
                viewHolder.setUsername(model.getUsername());


            }
        };
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
