package app.moov.moov;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserProfileActivity extends AppCompatActivity {

    private TextView tvUsername;
    private Button btnFollow;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        tvUsername = (TextView) findViewById(R.id.tvUsername);
        btnFollow = (Button) findViewById(R.id.btnFollow);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        String uid = user.getUid(); //gets the current user

       // String uid = user.getDisplayName(); //gets the current user
        TextView textView = (TextView) findViewById(R.id.tvUsername); //adds a textview and sets it to the textview name
        textView.setText(uid);

         database = FirebaseDatabase.getInstance();
         databaseReference = database.getReference("Users");

    }

}
