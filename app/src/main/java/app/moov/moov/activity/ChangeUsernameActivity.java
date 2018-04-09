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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import app.moov.moov.R;

/**
 * Created by Lisa on 04/04/18.
 */

public class ChangeUsernameActivity extends ToolbarBaseActivity{

    private EditText etNewUserName;
    private Button btnChangeUsername;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private DatabaseReference usernameReference;
    private FirebaseDatabase database;

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

        database = FirebaseDatabase.getInstance();
        usernameReference = database.getReference().child("Usernames");
        setUIViews();
    }

    private void setUIViews() {
        etNewUserName = (EditText) findViewById(R.id.etChangeUsername);
        btnChangeUsername = (Button) findViewById(R.id.btnChangeUsername);

        btnChangeUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeUsername();
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

    private void changeUsername() {
        final String newUsername = etNewUserName.getText().toString().trim();
        //Check to see whether the username exists
        usernameReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (newUsername.exists()) {
//                    Toast.makeText(ChangeUsernameActivity.this, dataSnapshot + " already exists! Please choose another username", Toast.LENGTH_LONG).show();
//                } else {
//
//                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

}
