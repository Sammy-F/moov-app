package app.moov.moov.activity;

/**
 * Created by Sammy on 3/3/18.
 * Loads and displays the feed
 * and manages user interactions
 * with the toolbar.
 */

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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import app.moov.moov.model.Post;
import app.moov.moov.R;

public class FeedActivity extends ToolbarBaseActivity {

    private RecyclerView feedRecycler;
    private FirebaseDatabase database;
    private DatabaseReference baseRef;
    private DatabaseReference postsRef;
    private FirebaseAuth firebaseAuth;

    private LinearLayoutManager orderedManager;

    private String uid;

    private Context thisContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        thisContext = this;

        setUIViews();

        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        baseRef = database.getReference();
//        postsRef = baseRef.child("Posts");
        postsRef = baseRef.child("Users").child(uid).child("Feed");

    }

    /**
     * Initialize layout objects, called in onCreate
     */
    private void setUIViews() {
        feedRecycler = (RecyclerView) findViewById(R.id.feedRecycler);
        feedRecycler.setHasFixedSize(true);

        orderedManager = new LinearLayoutManager(this);
        orderedManager.setReverseLayout(true);
        orderedManager.setStackFromEnd(true);

        feedRecycler.setLayoutManager(orderedManager);

    }

    @Override
    protected void onStart() {
        super.onStart();

        // Creates new Adapter to user with the RecyclerView using
        // our internal FeedViewHolder.
        FirebaseRecyclerAdapter <Post, FeedViewHolder> FBRA = new FirebaseRecyclerAdapter<Post, FeedViewHolder>(

                Post.class,
                R.layout.cv_layout,
                FeedViewHolder.class,
                postsRef.orderByChild("time")

        ) {
            @Override
            protected void populateViewHolder(FeedViewHolder viewHolder, Post model, int position) {

                final FeedViewHolder viewHolder1 = viewHolder;

                viewHolder.setTitle(model.getMovieTitle());
                viewHolder.setRating(model.getMovieRating());
                viewHolder.setReview(model.getMovieReview());
                viewHolder.setUsername(model.getUsername());

                final int movieID = model.getMovieID();

                viewHolder.getBtnMovieTitle().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) { //temporary button response
                        Intent intent = new Intent(FeedActivity.this, MovieProfile.class);
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
        feedRecycler.setAdapter(FBRA);
    }

    /**
     * Internal ViewHolder class used
     * for the Feed's RecyclerView
     */
    public static class FeedViewHolder extends RecyclerView.ViewHolder{

        private Button btnMovieTitle;

        public FeedViewHolder(View itemView) {
            super(itemView);
            View mView = itemView;
            btnMovieTitle = itemView.findViewById(R.id.MovieTitle);
        }

        public void setTitle(String title) {
            btnMovieTitle.setText(title);
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

        public Button getBtnMovieTitle() { return btnMovieTitle; }

    }


//    /**
//     * Internal ViewHolder class used
//     * for the Feed's RecyclerView
//     */
//    public class rvHolder extends RecyclerView.ViewHolder {
//
//        public rvHolder(View itemView) {
//            super(itemView);
//            View mView = itemView;
//        }
//
//        public void setTitle(String movieTitle) {
//            TextView thisMovie = (TextView) itemView.findViewById(R.id.MovieTitle);
//        }
//
//    }
}
