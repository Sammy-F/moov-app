package app.moov.moov.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import app.moov.moov.R;
import app.moov.moov.util.ConnectionTester;

/**
 * This class is the page where new users register onto the app
 */
public class RegistrationActivity extends AppCompatActivity {

    private Button btnRegister;
    private Button btnAlready;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etPassword2;
    private EditText etEmail;
    private EditText etFirstName;
    private EditText etLastName;

    private ProgressBar regProgress;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;

    private String userName;
    private String userEmail;
    private String userPassword;
    private String firstName;
    private String lastName;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageRef;
    private StorageReference imageRef;
    private StorageReference avatarRef;
    private StorageReference newAvatarRef;

    private FirebaseUser currentPerson;

    /**
     * Initializes all of the object (like toolbar, bottom nav bar), sets up data references
     * and specifies which layout XML to use
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setUIViews();

        firebaseStorage = FirebaseStorage.getInstance();
        storageRef = firebaseStorage.getReference();

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        btnAlready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goLogin();
            }
        });
        //If the register button is clicked, then get all the user input to upload to firebase and authenticate
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userName = etUsername.getText().toString().trim();
                userEmail = etEmail.getText().toString().trim();
                userPassword = etPassword.getText().toString().trim();
                firstName = etFirstName.getText().toString().trim();
                lastName = etLastName.getText().toString().trim();

                //If we are able to validate it, then upload all the data on firebase and authorize the user
                if (validate()) {
                    regProgress.setVisibility(View.VISIBLE);

                    database.getReference().child("lusernames").child(userName.toLowerCase()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (!dataSnapshot.exists()) {

                                Toast.makeText(RegistrationActivity.this, "Attempting registration. This may take a while!", Toast.LENGTH_LONG).show();

                                firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        //what do we do once the user is registered?

                                        if (task.isSuccessful()) {
                                            Toast.makeText(RegistrationActivity.this, "Registration successful!", Toast.LENGTH_LONG).show();

                                            currentPerson=FirebaseAuth.getInstance().getCurrentUser();
                                            DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Users").child(currentPerson.getUid());
                                            ref.child("Username").setValue(userName);
                                            ref.child("FirstName").setValue(firstName);
                                            ref.child("LastName").setValue(lastName);

                                            String thisUID = firebaseAuth.getUid();

                                            database.getReference().child("Usernames").child(userName).setValue(thisUID);
                                            database.getReference().child("Users").child(currentPerson.getUid()).child("lowername").setValue(userName.trim().toLowerCase());
                                            database.getReference().child("lusernames").child(userName.trim().toLowerCase()).setValue(thisUID);
                                            avatarSetup(thisUID);
                                            startActivity(new Intent(RegistrationActivity.this, FeedTutorialActivity.class));
                                            finish();
                                        } else {


                                            regProgress.setVisibility(View.INVISIBLE);
                                            Toast.makeText(RegistrationActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                            }
                            else {
                                Toast.makeText(RegistrationActivity.this, "Username is taken.", Toast.LENGTH_LONG).show();
                                regProgress.setVisibility(View.INVISIBLE);
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });


    }

    /**
     * Initialize the View variables used in
     * the Activity.
     */
    private void setUIViews(){
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnAlready = (Button) findViewById(R.id.btnAlready);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etPassword2 = (EditText) findViewById(R.id.etPassword2);
        etEmail = (EditText) findViewById(R.id.etEmail);
        regProgress = (ProgressBar) findViewById(R.id.regProcess);
        etFirstName = (EditText) findViewById(R.id.etFirstName);
        etLastName = (EditText) findViewById(R.id.etLastName);

        findViewById(R.id.constraintLayout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                return true;
            }
        });
    }

    /**
     * Go to the log in page, called when the user decides not to register and to go back to the log in page
     */
    private void goLogin(){
        Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * sets up the avatar to the default blue logo
     * @param newUID
     */
    private void avatarSetup(String newUID) {
        final long ONE_MEGABYTE = 1024 * 1024;

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference baseRef = firebaseStorage.getReference();
        final StorageReference avatarRef = baseRef.child("images").child("avatars").child(newUID + ".png");
        StorageReference defaultRef = firebaseStorage.getReferenceFromUrl("gs://moov-app.appspot.com/images/avatars/blue_1.png");

        defaultRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                avatarRef.putBytes(bytes);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegistrationActivity.this, "Wasn't able to get image.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * Validates the authentication - makes sure the passwords, email, username etc meet the conditions
     * @return
     */
    private Boolean validate(){
        String name = etUsername.getText().toString();
        String password1 = etPassword.getText().toString();
        String password2 = etPassword2.getText().toString();
        String email = etEmail.getText().toString();
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();

        if (name.isEmpty() || password1.isEmpty() || password2.isEmpty() || email.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
            Toast.makeText(RegistrationActivity.this, "Please enter all details!", Toast.LENGTH_LONG).show();
            return false;
        } else if (!password1.trim().equals(password2.trim())){
            Toast.makeText(RegistrationActivity.this, "The passwords do not match.", Toast.LENGTH_LONG).show();
            return false;
        } else if (name.length() > 12) {
            Toast.makeText(RegistrationActivity.this, "Username must be 12 characters or shorter", Toast.LENGTH_LONG).show();
            return false;
        } else if (!name.matches("[A-Za-z0-9]+") || !firstName.matches("[A-Za-z0-9]+") || !lastName.matches("[A-Za-z0-9]+")) {
            Toast.makeText(RegistrationActivity.this, "First name, last name, and username can only contain alphanumeric characters", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }
}
