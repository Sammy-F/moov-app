package app.moov.moov.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
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
 * then updates database information to reflect the changes
 * made by the user.
 *
 * Created by Sammy 3/25/18
 */

public class EditPostActivity extends ToolbarBaseActivity {

    private RatingBar rbRating;
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

    /**
     * Initializes all of the object (like toolbar, bottom nav bar), sets up data references
     * and specifies which layout XML to use
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolBarSetup(toolbar);

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

    /**
     * Initialize the View variables used in
     * the Activity.
     */
    private void setUIViews() {

        rbRating = (RatingBar) findViewById(R.id.rbRating);
        etReview = (EditText) findViewById(R.id.etReview);
        tvMovieTitle = (TextView) findViewById(R.id.tvMovieTitle);
        btnShare = (Button) findViewById(R.id.btnShare);

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
                editPost();
            }
        });

    }

    /**
     * Called when EditPost button is clicked
     */
    private void editPost() {
        final String newReview = etReview.getText().toString().trim();
        final String newRating = Integer.toString((int) rbRating.getRating());

        //update the Post in the main Posts node
        postsRef.child(postID).child("movieRating").setValue(newRating);
        postsRef.child(postID).child("movieReview").setValue(newReview);

        Intent intent = new Intent(EditPostActivity.this, UserProfileActivity.class);
        startActivity(intent);
    }

}
