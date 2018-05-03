package app.moov.moov.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import app.moov.moov.R;

/**
 * This class handles the forgot password page
 */
public class ForgotPasswordActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    String email;

    EditText etEmailInput;
    Button btnSendEmail;

    /**
     * Initializes all of the object (like toolbar, bottom nav bar), sets up data references
     * and specifies which layout XML to use
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();

        setUIViews();

        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail();
            }
        });

    }

    /**
     * Initialize the View variables used in
     * the Activity.
     */
    private void setUIViews() {
        etEmailInput = (EditText) findViewById(R.id.etNewPasswordEmail);
        btnSendEmail = (Button) findViewById(R.id.btnSendNewPassword);
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
     * Sends the email to the user to change the password, is called when the btnSendEmail is clicked
     */
    private void sendEmail() {

        email = etEmailInput.getText().toString().trim();

        if (email.length() > 0) {
            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d("Email sent", "Email sent to user");
                        Toast.makeText(ForgotPasswordActivity.this, "Password change email sent.", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ForgotPasswordActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Toast.makeText(ForgotPasswordActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else {
            Toast.makeText(ForgotPasswordActivity.this, "Please type in an email.", Toast.LENGTH_SHORT).show();
        }


    }

}
