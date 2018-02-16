package app.moov.moov;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    private Button btnRegister;
    private Button btnAlready;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etPassword2;
    private EditText etEmail;
    private ProgressBar regProgress;

     private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setUIViews();

        firebaseAuth = FirebaseAuth.getInstance();

        btnAlready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goLogin();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    //Database time, bitches

                    regProgress.setVisibility(View.VISIBLE);

                    final String userName = etUsername.getText().toString().trim();
                    String userEmail = etEmail.getText().toString().trim();
                    String userPassword = etPassword.getText().toString().trim();

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

                                startActivity(new Intent(RegistrationActivity.this, Feed.class));
                            } else{
                                regProgress.setVisibility(View.INVISIBLE);
                                Toast.makeText(RegistrationActivity.this, "Registration failed, please try again.", Toast.LENGTH_LONG).show();
                            }
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
