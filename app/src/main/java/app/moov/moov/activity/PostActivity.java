package app.moov.moov.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;

import app.moov.moov.model.Post;
import app.moov.moov.R;

public class PostActivity extends ToolbarBaseActivity {

    private Uri uri = null;
//    private EditText editTextMovieTitle;
    private TextView tvMovieTitle;
//    private EditText editTextRating;
    private RatingBar ratingBar;
    private EditText editTextWriteReview;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference currentUserDB;

    private FirebaseDatabase thisDatabase;
    private DatabaseReference baseRef;
    private DatabaseReference usersRef;
    private DatabaseReference postsRef;
    private DatabaseReference moviePostsRef;

    private Post newPost;
    private DatabaseReference newPostRef;

    DatabaseReference followerRef;

    private int movieID;
    private String movieTitle;
    private String posterURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        toolBarSetup(toolbar);

        movieID = getIntent().getIntExtra("movieID", 0); //defaults movieID to 0 if not set
        movieTitle = getIntent().getStringExtra("movieTitle").trim(); //get standardized title from search
        posterURL = getIntent().getStringExtra("posterURL");

        mAuth = FirebaseAuth.getInstance();

        thisDatabase = FirebaseDatabase.getInstance();
        baseRef = thisDatabase.getReference();
        usersRef = baseRef.child("Users");
        postsRef = baseRef.child("Posts");
        moviePostsRef = baseRef.child("PostsByMovie").child(Integer.toString(movieID));

        storageReference = FirebaseStorage.getInstance().getReference();
//        editTextMovieTitle = (EditText) findViewById(R.id.editTextMovieTitle);
        tvMovieTitle = (TextView) findViewById(R.id.tvMovieTitle);
        tvMovieTitle.setText(movieTitle);
//        editTextRating = (EditText) findViewById(R.id.editTextRating);
        ratingBar = (RatingBar) findViewById(R.id.editTextRating);
        editTextWriteReview = (EditText) findViewById(R.id.editTextWriteReview);

        findViewById(R.id.postConstraint).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                return true;
            }
        });
    }

//    public void shareButtonClicked(View view) {
//        String movieTitle = editTextMovieTitle.getText().toString().trim();
//        String rating = editTextRating.getText().toString().trim();
//        String review = editTextWriteReview.getText().toString().trim();
//        if (!TextUtils.isEmpty(movieTitle) && !TextUtils.isEmpty(rating) && !TextUtils.isEmpty(review)) {
//            StorageReference filePath = storageReference.child("Post_Review").child(uri.getLastPathSegment());
//            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    Uri downloadurl = taskSnapshot.getDownloadUrl();
//                    Toast.makeText(PostActivity.this,"Upload Complete", Toast.LENGTH_LONG).show();
//                }
//            });
//        }
//    }
    public void shareButtonClicked(View view){
//        final String movieTitle = editTextMovieTitle.getText().toString().trim();
//        final String rating = editTextRating.getText().toString().trim();
        final int rating = (int) ratingBar.getRating();
        final String review = editTextWriteReview.getText().toString().trim();
        final long timePosted = Calendar.getInstance().getTimeInMillis();
        final String stringRating = Integer.toString(rating);

            if (!TextUtils.isEmpty(movieTitle)) {
                String user = mAuth.getCurrentUser().getUid();

                usersRef.child(user).child("Username").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String user = mAuth.getCurrentUser().getUid();
                        String username = dataSnapshot.getValue(String.class);

                        newPostRef = postsRef.push();

                        final Post newPost = new Post(username, user, movieTitle, stringRating, review, timePosted, newPostRef.getKey(), movieID, posterURL); //TODO: Update w/ actual movie ID

                        newPostRef.setValue(newPost);

                        // Switching to pointers ONLY
//                        usersRef.child(user).child("Posts").child(newPostRef.getKey()).setValue(newPost);
//                        usersRef.child(user).child("Feed").child(newPostRef.getKey()).setValue(newPost);
                        usersRef.child(user).child("Posts").child(newPostRef.getKey()).setValue(newPostRef.getKey());
                        usersRef.child(user).child("Posts").child(newPostRef.getKey()).child("timestamp").setValue(timePosted);
                        usersRef.child(user).child("Feed").child(newPostRef.getKey()).setValue(newPostRef.getKey());
                        usersRef.child(user).child("Feed").child(newPostRef.getKey()).child("timestamp").setValue(timePosted);

                        moviePostsRef.child(newPostRef.getKey()).setValue(newPostRef.getKey());
                        moviePostsRef.child(newPostRef.getKey()).child("timestamp").setValue(timePosted);

                        /**
                         * Store the new post in all user's feeds.
                         */
                        usersRef.child(user).child("Followers").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                                    String UID = postSnapshot.getKey();
                                    usersRef.child(UID).child("Feed").child(newPostRef.getKey()).setValue(newPostRef.getKey());
                                    usersRef.child(UID).child("Feed").child(newPostRef.getKey()).child("timestamp").setValue(timePosted);
//                                    usersRef.child(UID).child("Feed").child(newPostRef.getKey()).setValue(newPost);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                startActivity(new Intent(PostActivity.this, FeedActivity.class));
                finish();
        }
    }


}