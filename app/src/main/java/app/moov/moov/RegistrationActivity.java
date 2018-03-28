package app.moov.moov;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
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
import java.net.URL;

public class RegistrationActivity extends AppCompatActivity {

    private Button btnRegister;
    private Button btnAlready;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etPassword2;
    private EditText etEmail;
    private ProgressBar regProgress;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;

    private String userName;
    private String userEmail;
    private String userPassword;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageRef;
    private StorageReference imageRef;
    private StorageReference avatarRef;
    private StorageReference newAvatarRef;

    private Uri downloadURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setUIViews();

        firebaseStorage = FirebaseStorage.getInstance();
        storageRef = firebaseStorage.getReference();
        imageRef = storageRef.child("images");
        avatarRef = imageRef.child("avatars");

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        btnAlready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goLogin();
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userName = etUsername.getText().toString().trim();
                userEmail = etEmail.getText().toString().trim();
                userPassword = etPassword.getText().toString().trim();

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

                                            FirebaseUser currentPerson=FirebaseAuth.getInstance().getCurrentUser();
                                            DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Users").child(currentPerson.getUid());
                                            ref.child("Username").setValue(userName);

                                            database.getReference().child("Usernames").child(userName).setValue(firebaseAuth.getCurrentUser().getUid());
                                            database.getReference().child("Users").child(currentPerson.getUid()).child("lowername").setValue(userName.trim().toLowerCase());
                                            database.getReference().child("lusernames").child(userName.trim().toLowerCase()).setValue(firebaseAuth.getCurrentUser().getUid());
                                            startActivity(new Intent(RegistrationActivity.this, FeedActivity.class));
                                            finish();
                                        } else {

                                            regProgress.setVisibility(View.INVISIBLE);
                                            Toast.makeText(RegistrationActivity.this, "Registration failed, please try again.", Toast.LENGTH_LONG).show();
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

    private void setUIViews(){
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnAlready = (Button) findViewById(R.id.btnAlready);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etPassword2 = (EditText) findViewById(R.id.etPassword2);
        etEmail = (EditText) findViewById(R.id.etEmail);
        regProgress = (ProgressBar) findViewById(R.id.regProcess);
    }

    private void goLogin(){
        Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private Boolean validate(){
        String name = etUsername.getText().toString();
        String password1 = etPassword.getText().toString();
        String password2 = etPassword2.getText().toString();
        String email = etEmail.getText().toString();

        if (name.isEmpty() || password1.isEmpty() || password2.isEmpty() || email.isEmpty()) {
            Toast.makeText(RegistrationActivity.this, "Please enter all details!", Toast.LENGTH_LONG).show();
            return false;
        } else if (!password1.trim().equals(password2.trim())){
            Toast.makeText(RegistrationActivity.this, "The passwords do not match.", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }
}
