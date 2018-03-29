package app.moov.moov;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FindUserActivity extends AppCompatActivity {

    private TextView tvPromptUID;
    private EditText etUsername;
    private Button btnFindUser;
    private FirebaseDatabase database;
    private DatabaseReference usernamesRef;
    private DatabaseReference checkUserRef;
    private FirebaseAuth firebaseAuth;
    private String checkUsername;
    private String userID;
    private ArrayList<String> userNameList;
    private SearchAdapter searchAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Move to setUIviews later

        setUIViews(); // Initialize layout object variables

        database = FirebaseDatabase.getInstance();
        usernamesRef = database.getReference().child("Usernames");
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();

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
                if (dataSnapshot.getValue(String.class).equals(userID)) {
                    Intent intent = new Intent(FindUserActivity.this, UserProfileActivity.class);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(FindUserActivity.this,OtherUserProfile.class);
                    intent.putExtra("thisUserID", dataSnapshot.getValue().toString());
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(FindUserActivity.this,"An error occurred when trying to get the UID.", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
