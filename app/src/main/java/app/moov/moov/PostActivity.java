package app.moov.moov;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PostActivity extends AppCompatActivity {

    private Uri uri = null;
    private EditText editTextMovieTitle;
    private EditText editTextRating;
    private EditText editTextWriteReview;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference currentUserDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storageReference = FirebaseStorage.getInstance().getReference();
        setContentView(R.layout.activity_post);
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

            if (!TextUtils.isEmpty(movieTitle) && !TextUtils.isEmpty(rating) && !TextUtils.isEmpty(review)) {
                    String user = mAuth.getCurrentUser().getUid();
                     currentUserDB = mDatabase.child(user);
                     currentUserDB.child("Posts").child("Title").setValue(movieTitle);
                    currentUserDB.child("Posts").child(movieTitle).child("Rating").setValue(rating);
                    currentUserDB.child("Posts").child(movieTitle).child("Review").setValue(review);

        }
    }


}