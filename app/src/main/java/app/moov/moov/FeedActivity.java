package app.moov.moov;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FeedActivity extends AppCompatActivity {

    private RecyclerView feedRecycler;
    private FirebaseDatabase database;
    private DatabaseReference baseRef;
    private DatabaseReference postsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUIViews();

        database = FirebaseDatabase.getInstance();
        baseRef = database.getReference();
        postsRef = baseRef.child("Posts");

    }

    private void setUIViews() {

        feedRecycler = (RecyclerView) findViewById(R.id.feedRecycler);
        feedRecycler.setHasFixedSize(true);
        feedRecycler.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter <Post, FeedViewHolder> FBRA = new FirebaseRecyclerAdapter<Post, FeedViewHolder>(

                Post.class,
                R.layout.rv_row,
                FeedViewHolder.class,
                postsRef

        ) {
            @Override
            protected void populateViewHolder(FeedViewHolder viewHolder, Post model, int position) {

                viewHolder.setTitle(model.getMovieTitle());
                viewHolder.setRating(model.getMovieRating());
                viewHolder.setReview(model.getMovieReview());

            }
        };
        feedRecycler.setAdapter(FBRA);
    }

    public static class FeedViewHolder extends RecyclerView.ViewHolder{

        public FeedViewHolder(View itemView) {
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

    }

}
