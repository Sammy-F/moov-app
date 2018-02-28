package app.moov.moov;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

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
        final String timePosted = Calendar.getInstance().getTime().toString();

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

                Post newPost = new Post(user, movieTitle, rating, review, timePosted);

                DatabaseReference newPostRef = postsRef.push();
                newPostRef.setValue(newPost);
//                postsRef.push().setValue(new HashMap<String, String>().put("Title", movieTitle)); //ignore me

                usersRef.child(user).child("Posts").child(newPostRef.getKey()).setValue(newPost);
                startActivity(new Intent(PostActivity.this, FeedPage.class));



        }
    }


}