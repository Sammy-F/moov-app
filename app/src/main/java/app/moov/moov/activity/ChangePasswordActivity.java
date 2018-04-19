package app.moov.moov.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
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

/**
 * Modified by Sammy 4/5/2018
 *
 * Class that handles the Activity for changing
 * the user's password.
 */

public class ChangePasswordActivity extends ToolbarBaseActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    private EditText etNewPass;
    private EditText etNewPassAgain;
    private EditText etCurrentPass;
    private EditText etEmail;

    private Button btnChangePass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolBarSetup(toolbar);

        BottomNavigationView navBar = (BottomNavigationView) findViewById(R.id.navBar);
        BottomNavigationViewHelper.disableShiftMode(navBar);
        setUpNavBar(navBar);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        setUIViews();

    }

    /**
     * Method run when the change password button
     * is pressed.
     * Reauthenticates the user and then changes their password
     * if their inputs are valid.
     */
    private void changePass() {

        final String newPass = etNewPass.getText().toString().trim();
        String newPassAgain = etNewPassAgain.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String currentPass = etCurrentPass.getText().toString().trim();

        // Check if authentication strings exist.
        if (newPass.length() == 0 || newPassAgain.length() == 0 || email.length() == 0 || currentPass.length() == 0) {
            Toast.makeText(ChangePasswordActivity.this, "Please input all details!", Toast.LENGTH_SHORT).show();
        }
        else if (!email.equals(user.getEmail())){
            Toast.makeText(ChangePasswordActivity.this, "The email you've entered is not correct!", Toast.LENGTH_SHORT).show();
        }
        else if (!newPass.equals(newPassAgain)) { // Check if the password strings match
            Toast.makeText(ChangePasswordActivity.this, "The passwords do not match!", Toast.LENGTH_SHORT).show();
        }
        else {
            // Initialize credentials using the user's input login info
            AuthCredential credential = EmailAuthProvider.getCredential(email, currentPass);
            user.reauthenticate(credential)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    user.updatePassword(newPass).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ChangePasswordActivity.this, "Password successfully updated", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ChangePasswordActivity.this, FeedActivity.class);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    /**
     * Initialize the View variables used in
     * the Activity.
     */
    private void setUIViews() {
        etNewPass = (EditText) findViewById(R.id.etPass);
        etNewPassAgain = (EditText) findViewById(R.id.etPassAgain);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etCurrentPass = (EditText) findViewById(R.id.etPass);
        btnChangePass = (Button) findViewById(R.id.btnChangePass);

        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePass();
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

}
