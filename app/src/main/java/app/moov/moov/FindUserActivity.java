package app.moov.moov;

import android.content.Intent;
import android.os.Bundle;
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
    private EditText etUsername;
    private Button btnFindUser;

    private FirebaseDatabase database;
    private DatabaseReference usernamesRef;
    private DatabaseReference checkUserRef;

    private String checkUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUIViews(); // Initialize layout object variables

        database = FirebaseDatabase.getInstance();
        usernamesRef = database.getReference().child("Usernames");

        btnFindUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkUsername = etUsername.getText().toString().trim();
                usernamesRef.child(checkUsername).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            goToUserProfile();
                        }

                        else {
                            Toast.makeText(FindUserActivity.this,"User not found.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });
    }

    /**
     * Initialzies layout object variables
     */
    private void setUIViews() {

        tvPromptUID = (TextView) findViewById(R.id.tvPrompUID);
        etUsername = (EditText) findViewById(R.id.etUsername);
        btnFindUser = (Button) findViewById(R.id.btnFindUser);

    }

    /**
     * If search for user exists, go to their profile and store the user's
     * UID as an extra of the Intent.
     */
    private void goToUserProfile() {

        usernamesRef.child(checkUsername).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Intent intent = new Intent(FindUserActivity.this,OtherUserProfile.class);
                intent.putExtra("thisUserID", dataSnapshot.getValue().toString());
                startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(FindUserActivity.this,"An error occurred when trying to get the UID.", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
