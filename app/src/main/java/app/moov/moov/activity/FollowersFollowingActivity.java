package app.moov.moov.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import app.moov.moov.R;

public class FollowersFollowingActivity extends ToolbarBaseActivity {

    private RecyclerView rvFollow;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private String userID;

    private FirebaseDatabase database;
    private DatabaseReference baseRef;
    private DatabaseReference usersRef;
    private DatabaseReference userRef;
    private DatabaseReference followerRef;
    private DatabaseReference followingRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers_following);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        userID = user.getUid();

        database = FirebaseDatabase.getInstance();
        baseRef = database.getReference();
        usersRef = baseRef.child("Users");
        userRef = usersRef.child(userID);
        followerRef = userRef.child("Followers");
        followingRef = userRef.child("Following");

        setUIViews();
    }

    private void setUIViews() {
        rvFollow = (RecyclerView) findViewById(R.id.rvFollow);
    }

}
