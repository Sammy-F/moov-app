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
    private EditText etFindUser;
    private RecyclerView rvFindUser;
    private ArrayList<String> userNameList;
    private SearchAdapter searchAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Move to setUIviews later
        etFindUser = (EditText) findViewById(R.id.etFindUser);
        rvFindUser = (RecyclerView) findViewById(R.id.rvFindUser);

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

        rvFindUser.setHasFixedSize(true);
        rvFindUser.setLayoutManager(new LinearLayoutManager(this));
        rvFindUser.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        userNameList = new ArrayList<>();


        etFindUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty()) {
                    setAdapter(editable.toString());
                    userNameList.clear();
                    rvFindUser.removeAllViews();

                }

            }

        });
    }


    private void setAdapter(final String searchString) {

        database.getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userNameList.clear();
                rvFindUser.removeAllViews();
                int counter = 0;
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    String uid = snapshot.getKey();
                    String userName = snapshot.child("Username").getValue(String.class);
                    if (userName.contains(searchString)) {
                        userNameList.add(userName);
                        counter++;
                    }
                    if (counter == 15)
                        break;
                }

                searchAdapter = new SearchAdapter(FindUserActivity.this, userNameList);
                rvFindUser.setAdapter(searchAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
