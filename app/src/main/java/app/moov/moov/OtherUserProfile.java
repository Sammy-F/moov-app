package app.moov.moov;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class OtherUserProfile extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        userRecycler = (RecyclerView) findViewById(R.id.userRecycler);
        userRecycler.setHasFixedSize(true);

        orderedManager = new LinearLayoutManager(this);
        orderedManager.setReverseLayout(true);
        orderedManager.setStackFromEnd(true);

        userRecycler.setLayoutManager(orderedManager);

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


        thisUserRef.child("Followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tvNumFollowers.setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id==R.id.addIcon) {
            Intent intent = new Intent(OtherUserProfile.this,PostActivity.class);
            startActivity(intent);
        }

        if (id==R.id.logoutIcon) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(OtherUserProfile.this, MainActivity.class));
        }

        if (id==R.id.profileIcon) {
            Intent intent = new Intent(OtherUserProfile.this, UserProfileActivity.class);
            startActivity(intent);
        }

        if (id==R.id.searchIcon) {
            Intent intent = new Intent(OtherUserProfile.this,FindUserActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {

        super.onStart();

        FirebaseRecyclerAdapter<Post, FeedActivity.FeedViewHolder> FBRA = new FirebaseRecyclerAdapter<Post, FeedActivity.FeedViewHolder>(

                Post.class,
                R.layout.rv_row,
                FeedActivity.FeedViewHolder.class,
                thisUserRef.child("Posts").orderByChild("time")

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
        userRecycler.setAdapter(FBRA);

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
