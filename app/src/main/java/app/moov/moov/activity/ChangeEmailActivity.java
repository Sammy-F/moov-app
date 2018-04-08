package app.moov.moov.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import app.moov.moov.R;

public class ChangeEmailActivity extends ToolbarBaseActivity {

    private EditText etCurrentEmail;
    private EditText etNewEmail;
    private EditText etNewEmailAgain;
    private EditText etPassword;
    private Button btnChangeEmail;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolBarSetup(toolbar);
        BottomNavigationView navBar = (BottomNavigationView) findViewById(R.id.navBar);
        setUpNavBar(navBar);


        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        setUIViews();
    }

    private void setUIViews() {
        etCurrentEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPass);
        etNewEmail = (EditText) findViewById(R.id.etNewUsername);
        etNewEmailAgain = (EditText) findViewById(R.id.etNewEmailAgain);
        btnChangeEmail = (Button) findViewById(R.id.btnChangeEmail);

        btnChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeEmail();
            }
        });
        findViewById(R.id.constraintLayout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                return true;
            }
        });
    }

    private void changeEmail() {
        String currentEmail = etCurrentEmail.getText().toString().trim().toLowerCase();
        String password = etPassword.getText().toString().trim();
        final String newEmail = etNewEmail.getText().toString().trim().toLowerCase();
        String newEmailAgain = etNewEmailAgain.getText().toString().trim().toLowerCase();

        if (currentEmail.length() == 0 || password.length() == 0 || newEmail.length() == 0 || newEmailAgain.length() == 0) {
            Toast.makeText(this, "Please input all details!", Toast.LENGTH_SHORT).show();
        }
        else if (!currentEmail.equals(user.getEmail())){
            Toast.makeText(this, "Your current e-mail is not correct!", Toast.LENGTH_SHORT).show();
        }
        else if (!newEmail.equals(newEmailAgain)) {
            Toast.makeText(this, "The emails do not match!", Toast.LENGTH_LONG).show();
        }
        else {

            AuthCredential credential = EmailAuthProvider.getCredential(currentEmail, password);
            user.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    user.updateEmail(newEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ChangeEmailActivity.this, "Email successfully updated", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ChangeEmailActivity.this, FeedActivity.class);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ChangeEmailActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ChangeEmailActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        }
    }

}
