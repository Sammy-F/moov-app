package app.moov.moov.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import app.moov.moov.R;

public class ChangeEmailActivity extends AppCompatActivity {

    private EditText etCurrentEmail;
    private EditText etNewEmail;
    private EditText etNewEmailAgain;
    private EditText etPassword;
    private Button btnChangeEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUIViews();
    }

    private void setUIViews() {
        etCurrentEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPass);
        etNewEmail = (EditText) findViewById(R.id.etNewEmail);
        etNewEmailAgain = (EditText) findViewById(R.id.etNewEmailAgain);
        btnChangeEmail = (Button) findViewById(R.id.btnChangEmail);

        btnChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeEmail();
            }
        });
    }

    private void changeEmail() {
        String currentEmail = etCurrentEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String newEmail = etNewEmail.getText().toString().trim();
        String newEmailAgain = etNewEmailAgain.getText().toString().trim();

        if (currentEmail.length() == 0 || password.length() == 0 || newEmail.length() == 0 || newEmailAgain.length() == 0) {
            Toast.makeText(this, "Please input all details!", Toast.LENGTH_SHORT).show();
        }
    }

}
