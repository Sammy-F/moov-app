package app.moov.moov;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegistrationActivity extends AppCompatActivity {

    private Button btnRegister;
    private Button btnAlready;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etPassword2;
    private EditText etEmail;
    private TextView tvIssue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        setUIViews();

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
                    //TODO: validation
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
        tvIssue = (TextView) findViewById(R.id.tvIssue);
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
            tvIssue.setText("Please enter all of the details!");
            return false;
        } else if (!password1.equals(password2)){
            tvIssue.setText("The passwords do not match!");
            return false;
        } else {
            return true;
        }
    }
}
