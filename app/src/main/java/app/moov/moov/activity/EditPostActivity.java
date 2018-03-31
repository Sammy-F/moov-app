package app.moov.moov.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import app.moov.moov.R;

/**
 * Handles post editing by taking the existing
 * review and rating in a post from the intent and
 * then updates database info to reflect the changes.
 *
 * Created by Sammy 3/25/18
 */

public class EditPostActivity extends AppCompatActivity {

    private EditText etRating;
    private EditText etReview;
    private TextView tvMovieTitle;
    private Button btnShare;

    private String initRating;
    private String initReview;
    private String movieTitle;

    private Intent thisIntent;

    private FirebaseDatabase database;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference baseRef;
    private DatabaseReference postsRef;
    private DatabaseReference usersRef;
    private DatabaseReference userRef;
    private DatabaseReference followersRef;

    private String uid;

    private String postID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();

        uid = firebaseAuth.getCurrentUser().getUid();

        database = FirebaseDatabase.getInstance();
        baseRef = database.getReference();
        usersRef = baseRef.child("Users");
        postsRef = baseRef.child("Posts");

        userRef = usersRef.child(uid);
        followersRef = userRef.child("Followers");

        thisIntent = getIntent();

        initRating = thisIntent.getStringExtra("rating");
        initReview = thisIntent.getStringExtra("review");
        movieTitle = thisIntent.getStringExtra("movieTitle");
        postID = thisIntent.getStringExtra("postID");

        setUIViews();
    }

    private void setUIViews() {

        etRating = (EditText) findViewById(R.id.etRating);
        etReview = (EditText) findViewById(R.id.etReview);
        tvMovieTitle = (TextView) findViewById(R.id.tvMovieTitle);
        btnShare = (Button) findViewById(R.id.btnShare);

        etRating.setText(initRating);
        etReview.setText(initReview);
        tvMovieTitle.setText(movieTitle);

        findViewById(R.id.constraintLayout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                return true;
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etRating.getText().toString().length() > 0 && etReview.getText().toString().length() > 0) {
                    editPost();
                }
                else {
                    Toast.makeText(EditPostActivity.this,"Please input a review and rating!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    /**
     * Called when EditPost button is clicked
     */
    private void editPost() {
        final String newReview = etReview.getText().toString().trim();
        final String newRating = etRating.getText().toString().trim();

        //update the Post in the main Posts node
        postsRef.child(postID).child("movieRating").setValue(newRating);
        postsRef.child(postID).child("movieReview").setValue(newReview);

        //update the Post in the user's Posts
        userRef.child("Posts").child(postID).child("movieRating").setValue(newRating);
        userRef.child("Posts").child(postID).child("movieReview").setValue(newReview);

        //update the Post in the user's Feed
        userRef.child("Feed").child(postID).child("movieRating").setValue(newRating);
        userRef.child("Feed").child(postID).child("movieReview").setValue(newReview);

        //update the Post in the Feed of all of the user's Followers
        followersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot followerSnapshot : dataSnapshot.getChildren()) {
                    String followerUID = followerSnapshot.getKey();
                    usersRef.child(followerUID).child("Feed").child(postID).child("movieRating").setValue(newRating);
                    usersRef.child(followerUID).child("Feed").child(postID).child("movieReview").setValue(newReview);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Return the user to the feed
        Intent intent = new Intent(EditPostActivity.this, FeedActivity.class);
        startActivity(intent);
    }

}
