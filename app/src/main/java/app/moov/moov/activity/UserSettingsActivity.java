package app.moov.moov.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import app.moov.moov.R;

/**
 * Manages a list of user settings options
 */
public class UserSettingsActivity extends ToolbarBaseActivity {

    Button btnChangePass;
    Button btnChangeEmail;
    Button btnReportIssue;
    Button btnLogOut;
    private FirebaseAuth firebaseAuth;
    Button btnChangeAv;


    /**
     * Initializes all of the object (like toolbar, bottom nav bar), sets up data references
     * and specifies which layout XML to use
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolBarSetup(toolbar);

        BottomNavigationView navBar = (BottomNavigationView) findViewById(R.id.navBar);
        BottomNavigationViewHelper.disableShiftMode(navBar);
        setUpNavBar(navBar);

        firebaseAuth = FirebaseAuth.getInstance();

        setUIViews();

    }

    /**
     * Initialize all buttons and set their onClickListeners
     */
    private void setUIViews() {

        btnChangeAv = (Button) findViewById(R.id.btnChangeAvatar);
        btnChangeAv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Class destination = ChangeAvatarActivity.class;
                btnClick(destination);
            }
        });

        btnChangePass = (Button) findViewById(R.id.btnChangePassword);
        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Class destination = ChangePasswordActivity.class;
                btnClick(destination);
            }
        });

        btnChangeEmail = (Button) findViewById(R.id.btnGoChangEmail);
        btnChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Class destination = ChangeEmailActivity.class;
                btnClick(destination);
            }
        });

        btnReportIssue = (Button) findViewById(R.id.btnReportProblem);
        btnReportIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Class destination = ReportIssueActivity.class;
                btnClick(destination);
            }
        });

        btnLogOut = (Button) findViewById(R.id.btnLogOut);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                Intent intent = new Intent(UserSettingsActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                killActivity();
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

    private void killActivity() {
        this.finish();
    }

    private void btnClick(Class destination) {
        Intent intent = new Intent(UserSettingsActivity.this, destination);
        startActivity(intent);
    }

}