package app.moov.moov;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FindUserActivity extends AppCompatActivity {

    private TextView tvPromptUID;
    private EditText etUID;
    private Button btnFindUser;

    private FirebaseDatabase database;
    private DatabaseReference usersRef;
    private DatabaseReference checkUserRef;

    private Boolean userExists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUIViews(); // Initialize layout object variables

        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference().child("Users");

        btnFindUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userExists()) {
                    goToUserProfile();
                }
                else {
                    Toast.makeText(FindUserActivity.this, "User not found.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    /**
     * Initialzies layout object variables
     */
    private void setUIViews() {

        tvPromptUID = (TextView) findViewById(R.id.tvPrompUID);
        etUID = (EditText) findViewById(R.id.etUID);
        btnFindUser = (Button) findViewById(R.id.btnFindUser);

    }

    private void goToUserProfile() {

        Intent intent = new Intent(FindUserActivity.this,OtherUserProfile.class);
        intent.putExtra("thisUserID", etUID.getText().toString());

        startActivity(intent);

    }

    /**
     * Confirm that a user exists before attempting to go to their profile.
     */
    private Boolean userExists() {
        String checkID = etUID.getText().toString();
        usersRef.child(checkID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userExists = true;
                }
                else {
                    userExists = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return userExists;

    }

}
