package app.moov.moov.activity;

/**
 * MainActivity initializes the objects
 * in the login pages and handles the
 * listeners such as the video loop in the background.
 *
 * Modified 3/14 by Sammy Fritsche
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import app.moov.moov.R;
import app.moov.moov.util.ConnectionTester;

/**
 * This class is the log in page
 */
public class MainActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnRegister;
    private Button btnForgotPass;
    private int counter = 5;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private VideoView mVideoView;


    /**
     * Initializes all of the object (like toolbar, bottom nav bar), sets up data references
     * and specifies which layout XML to use
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mVideoView = (VideoView) findViewById(R.id.bgVideoView);
        mVideoView = (VideoView) findViewById(R.id.bgVideoView);
        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.bg_video);

        mVideoView.setVideoURI(uri);
        mVideoView.start();

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
            }
        });

        activitySetup();

        ConnectionTester connectionTester = new ConnectionTester();

        if (connectionTester.connectionExists()) {
            activitySetup();
        } else {
            Toast.makeText(this, "No internet connection detected. Please check your internet connection and restart the app.", Toast.LENGTH_LONG).show();
        }

    }


    /**
     * When we return to the log in page, start playing the video again
     */
    @Override
    protected void onPostResume() {
        super.onPostResume();
        mVideoView = (VideoView) findViewById(R.id.bgVideoView);

        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.bg_video);

        mVideoView.setVideoURI(uri);
        mVideoView.start();

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
            }
        });

    }

    /**
     * Go to the registration page.
     */
    private void register() {
        Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
        startActivity(intent);
    }

    /**
     * Check if the editText is empty
     */
    private boolean isEmpty(EditText eText) {
        if (eText.getText().toString().trim().length() == 0) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Validate that the input values for the user's registration
     * are valid inputs.
     * @param userName
     * @param userPassword
     */
    private void validate(String userName, String userPassword) {

        progressDialog.setMessage("Just wait a bit!");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(userName, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, FeedActivity.class));
                    finish();
                } else{
                    Toast.makeText(MainActivity.this, "Login failed. Number of attempts remaining: " + counter, Toast.LENGTH_SHORT).show();
                    counter--;
                    if (counter == 0) {
                        btnLogin.setEnabled(false);
                    }
                    progressDialog.dismiss();
                }
            }
        });


    }

    private void activitySetup() {

        setUIViews();

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        // If user is already signed in, bring them to the Feed
        if (user != null) {
            finish();
            startActivity(new Intent(MainActivity.this,FeedActivity.class));
            finish();
        }

        // Call register() when register button is clicked
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        // Check if login details are valid and do so
        // if they are by calling validate()
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isEmpty(etEmail) && !isEmpty(etPassword)) {
                    validate(etEmail.getText().toString().trim(), etPassword.getText().toString().trim());
                }
                else {
                    Toast.makeText(MainActivity.this, "Please input username and password.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /**
     * Initialize layout items
     * Called in onCreate()
     */
    private void setUIViews() {
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnForgotPass = (Button) findViewById(R.id.btnForgot);
        btnForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
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
