package app.moov.moov;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;

public class PostActivity extends AppCompatActivity {

    private Uri uri = null;
    private EditText editTextMovieTitle;
    private EditText editTextRating;
    private EditText editTextWriteReview;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference currentUserDB;

    private FirebaseDatabase thisDatabase;
    private DatabaseReference baseRef;
    private DatabaseReference usersRef;
    private DatabaseReference postsRef;

    private Post newPost;
    private DatabaseReference newPostRef;

    DatabaseReference followerRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mAuth = FirebaseAuth.getInstance();

        thisDatabase = FirebaseDatabase.getInstance();
        baseRef = thisDatabase.getReference();
        usersRef = baseRef.child("Users");
        postsRef = baseRef.child("Posts");

        storageReference = FirebaseStorage.getInstance().getReference();
        editTextMovieTitle = (EditText) findViewById(R.id.editTextMovieTitle);
        editTextRating = (EditText) findViewById(R.id.editTextRating);
        editTextWriteReview = (EditText) findViewById(R.id.editTextWriteReview);
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
        final String movieTitle = editTextMovieTitle.getText().toString().trim();
        final String rating = editTextRating.getText().toString().trim();
        final String review = editTextWriteReview.getText().toString().trim();
        final long timePosted = Calendar.getInstance().getTimeInMillis();

            if (!TextUtils.isEmpty(movieTitle) && !TextUtils.isEmpty(rating) && !TextUtils.isEmpty(review)) {
                String user = mAuth.getCurrentUser().getUid();
//                     currentUserDB = mDatabase.child(user);
//                     Map<String, String> newPost = new HashMap<>();
//                     newPost.put("Title", movieTitle);
//                     newPost.put("Rating", rating);
//                     newPost.put("Review", review);
//                     usersRef.child(user).child("Posts").setValue(new HashMap<String, String>());
//                    currentUserDB.child("Posts").child(movieTitle).child("Rating").setValue(rating);
//                    currentUserDB.child("Posts").child(movieTitle).child("Review").setValue(review);

                usersRef.child(user).child("Username").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String user = mAuth.getCurrentUser().getUid();
                        String username = dataSnapshot.getValue(String.class);

                        newPostRef = postsRef.push();

                        final Post newPost = new Post(username, user, movieTitle, rating, review, timePosted, newPostRef.getKey(), 0); //TODO: Update w/ actual movie ID

                        newPostRef.setValue(newPost);

                        usersRef.child(user).child("Posts").child(newPostRef.getKey()).setValue(newPost);
                        usersRef.child(user).child("Feed").child(newPostRef.getKey()).setValue(newPost);

                        /**
                         * Store the new post in all user's feeds.
                         */
                        usersRef.child(user).child("Followers").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                                    String UID = postSnapshot.getKey();
                                    usersRef.child(UID).child("Feed").child(newPostRef.getKey()).setValue(newPost);

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



        }
    }


}