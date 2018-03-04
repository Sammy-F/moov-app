package app.moov.moov;

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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfileActivity extends AppCompatActivity {

    private TextView tvUsername;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private DatabaseReference userRef;
    private DatabaseReference userPostsRef;
    private String username;

    private RecyclerView profileFeedRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        tvUsername = (TextView) findViewById(R.id.tvUsername);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        profileFeedRecycler = (RecyclerView) findViewById(R.id.profileRecycler);
        profileFeedRecycler.setHasFixedSize(true);
        profileFeedRecycler.setLayoutManager(new LinearLayoutManager(this));

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        final String uid = user.getUid();
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference().child("Users").child(uid).child("Username");
        userPostsRef = database.getReference().child("Users").child(uid).child("Posts");

        /**
         * Inside gets current user's username
         */
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
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

        FirebaseRecyclerAdapter<Post, FeedActivity.FeedViewHolder> FBRA = new FirebaseRecyclerAdapter<Post, FeedActivity.FeedViewHolder>(

                Post.class,
                R.layout.rv_row,
                FeedActivity.FeedViewHolder.class,
                userPostsRef.orderByChild("time")

        ) {
            @Override
            protected void populateViewHolder(FeedActivity.FeedViewHolder viewHolder, Post model, int position) {

                final FeedActivity.FeedViewHolder viewHolder1 = viewHolder;

                viewHolder.setTitle(model.getMovieTitle());
                viewHolder.setRating(model.getMovieRating());
                viewHolder.setReview(model.getMovieReview());
                viewHolder.setUsername(model.getUsername());


            }
        };
        profileFeedRecycler.setAdapter(FBRA);

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
            Intent intent = new Intent(UserProfileActivity.this,PostActivity.class);
            startActivity(intent);
        }

        if (id==R.id.logoutIcon) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(UserProfileActivity.this, MainActivity.class));
        }

        if (id==R.id.searchIcon) {
            Intent intent = new Intent(UserProfileActivity.this,FindUserActivity.class);
            startActivity(intent);
        }

        if (id==R.id.profileIcon) {
            Intent intent = new Intent(UserProfileActivity.this,UserProfileActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public static class ProfileFeedHolder extends RecyclerView.ViewHolder {

        public ProfileFeedHolder(View itemView) {
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
