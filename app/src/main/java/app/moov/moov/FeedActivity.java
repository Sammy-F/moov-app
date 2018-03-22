package app.moov.moov;

/**
 * Created by Sammy on 3/3/18.
 * Loads and displays the feed
 * and manages user interactions
 * with the toolbar.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FeedActivity extends AppCompatActivity {

    private RecyclerView feedRecycler;
    private FirebaseDatabase database;
    private DatabaseReference baseRef;
    private DatabaseReference postsRef;
    private FirebaseAuth firebaseAuth;

    private LinearLayoutManager orderedManager;

    private String uid;

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUIViews();

        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        baseRef = database.getReference();
//        postsRef = baseRef.child("Posts");
        postsRef = baseRef.child("Users").child(uid).child("Feed");

    }

    /**
     * Initialized layout objects
     */
    private void setUIViews() {

        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FeedActivity.this, FeedActivity.class);
                startActivity(intent);
            }
        });

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

        public void setUsername(String username) {
            TextView userName = (TextView) itemView.findViewById(R.id.Username);
            userName.setText(username);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_feed_page, menu);
        return true;
    }

    public class rvHolder extends RecyclerView.ViewHolder {

        public rvHolder(View itemView) {
            super(itemView);
            View mView = itemView;
        }

        public void setTitle(String movieTitle) {
            TextView thisMovie = (TextView) itemView.findViewById(R.id.MovieTitle);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id==R.id.addIcon) {
            Intent intent = new Intent(FeedActivity.this,PostActivity.class);
            startActivity(intent);
        }

        if (id==R.id.logoutIcon) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(FeedActivity.this, MainActivity.class));
        }

        if (id==R.id.profileIcon) {
            Intent intent = new Intent(FeedActivity.this, UserProfileActivity.class);
            startActivity(intent);
        }

        if (id==R.id.searchIcon) {
            Intent intent = new Intent(FeedActivity.this,FindUserActivity.class);
            startActivity(intent);
        }

        if (id==R.id.movieSearchIcon) {
            Intent intent = new Intent(FeedActivity.this, ChooseMovieActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

}
