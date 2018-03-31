package app.moov.moov.activity;

/**
 * Created by Sammy on 3/3/18.
 * Loads and displays the feed
 * and manages user interactions
 * with the toolbar.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.w3c.dom.Text;

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

        Query keysQuery = postsRef;

        FirebaseRecyclerOptions<Post> options = new FirebaseRecyclerOptions.Builder<Post>()
                .setIndexedQuery(keysQuery, baseRef.child("Posts"), Post.class)
                .build();

        // Creates new Adapter to user with the RecyclerView using
        // our internal FeedViewHolder.
        FirebaseRecyclerAdapter <Post, FeedViewHolder> FBRA = new FirebaseRecyclerAdapter<Post, FeedViewHolder>(options) {
            @Override
            public FeedViewHolder onCreateViewHolder(ViewGroup parent, int ViewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.new_cv_layout, parent, false);

                return new FeedViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(FeedViewHolder viewHolder, int position, Post model) {

                final FeedViewHolder viewHolder1 = viewHolder;

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
        feedRecycler.setAdapter(FBRA);
    }

    /**
     * Internal ViewHolder class used
     * for the Feed's RecyclerView
     */
    public static class FeedViewHolder extends RecyclerView.ViewHolder{

        private TextView btnMovieTitle;
        private TextView movieReview;
        private ImageView ivPoster;

        public FeedViewHolder(View itemView) {
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
