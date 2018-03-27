package app.moov.moov;

/**
 * MainActivity initializes the objects
 * in the login pages and handles any
 * listeners.
 *
 * Modified 3/14 by Sammy Fritsche
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnRegister;
    private int counter = 5;
    private TextView numAttempts;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                    Toast.makeText(MainActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                    counter--;
                    numAttempts.setText("Number of attempts remaining: " + counter);
                    if (counter == 0) {
                        btnLogin.setEnabled(false);
                    }
                    progressDialog.dismiss();
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
        numAttempts = (TextView) findViewById(R.id.numAttempts);
        numAttempts.setText("Number of attempts remaining: 5");
    }
}
