package app.moov.moov.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import app.moov.moov.R;

/**
 * This class is the page that lets user report issues in the settings panel
 */
public class ReportIssueActivity extends ToolbarBaseActivity {

    EditText etSubject;
    EditText etMessageBody;
    Button btnSendMessage;

    final String[] recipient = {"sfritsch@macalester.edu"};

    /**
     * Initializes all of the object (like toolbar, bottom nav bar), sets up data references
     * and specifies which layout XML to use
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_issue);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolBarSetup(toolbar);

        BottomNavigationView navBar = (BottomNavigationView) findViewById(R.id.navBar);
        BottomNavigationViewHelper.disableShiftMode(navBar);
        setUpNavBar(navBar);

        setUIViews();

    }

    /**
     * Initialize the View variables used in
     * the Activity.
     */
    private void setUIViews() {
        etSubject = (EditText) findViewById(R.id.etSubject);
        etMessageBody = (EditText) findViewById(R.id.etMessageBody);
        btnSendMessage = (Button) findViewById(R.id.btnSendReport);

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail();
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

    /**
     * When the btnSendMessage is clicked, then it will get the subject that the user typed in, and then
     * after making sure it is not blank, then it will send the email to us
     */
    private void sendEmail() {
        String subject = etSubject.getText().toString().trim();
        String body = etSubject.getText().toString().trim();

        if (subject.length() == 0 || body.length() == 0) {
            Toast.makeText(this, "Please input a subject and message!", Toast.LENGTH_SHORT).show();
        }
        else {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL, recipient);
            i.putExtra(Intent.EXTRA_SUBJECT, subject);
            i.putExtra(Intent.EXTRA_TEXT, body);
            try {
                startActivity(Intent.createChooser(i, "Send issue report..."));
            } catch (android.content.ActivityNotFoundException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }

}
