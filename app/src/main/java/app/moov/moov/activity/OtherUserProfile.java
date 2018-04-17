package app.moov.moov.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import app.moov.moov.model.Post;
import app.moov.moov.R;
import app.moov.moov.util.FirebaseSwitchingAdapter;
import app.moov.moov.util.PaginatingPostsActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class OtherUserProfile extends PaginatingPostsActivity {

    private Context thisContext;

    private String thisUserID;
    private String currentUID;

    private TextView tvNumFollowers;
    private TextView tvNumFollowing;
    private TextView tvUsername;
    private Button btnFollow;

    private DatabaseReference usersRef;
    private DatabaseReference currentUserRef;
    private DatabaseReference thisUserRef;

    private LinearLayout llFollowers;
    private LinearLayout llFollowing;

    private CircleImageView ivAvatar;

    private FirebaseStorage firebaseStorage;
    private StorageReference avatarRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolBarSetup(toolbar);

        BottomNavigationView navBar = (BottomNavigationView) findViewById(R.id.navBar);
        setUpNavBar(navBar);

        thisContext = this;

        thisUserID = getIntent().getStringExtra("thisUserID");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference baseRef = database.getReference();
        usersRef = baseRef.child("Users");
        currentUID = firebaseAuth.getCurrentUser().getUid();
        currentUserRef = usersRef.child(currentUID);
        thisUserRef = usersRef.child(thisUserID);
        DatabaseReference postsRef = thisUserRef.child("Posts");

        firebaseStorage = FirebaseStorage.getInstance();
        avatarRef = firebaseStorage.getReference().child("images").child("avatars").child(thisUserID + ".png");

        RecyclerView feedRecycler = (RecyclerView) findViewById(R.id.userRecycler);

        setUIViews();

        paginationSetup(thisContext, firebaseAuth, database, baseRef, postsRef, feedRecycler);
    }

    private void setUIViews() {

        tvUsername = (TextView) findViewById(R.id.tvUsername);
        btnFollow = (Button) findViewById(R.id.btnFollow);

        ivAvatar = (CircleImageView) findViewById(R.id.ivAvatar);

        avatarRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(thisContext).asBitmap().load(uri.toString()).into(ivAvatar);
            }
        });

        tvNumFollowers = (TextView) findViewById(R.id.tvNumFollowers);
        tvNumFollowing = (TextView) findViewById(R.id.tvNumFollowing);

        llFollowers = (LinearLayout) findViewById(R.id.llFollowers);
        llFollowing = (LinearLayout) findViewById(R.id.llFollowing);

        llFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OtherUserProfile.this, FollowersFollowingActivity.class);
                intent.putExtra("type", "followers");
                intent.putExtra("uid", thisUserID);
                startActivity(intent);
            }
        });

        llFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OtherUserProfile.this, FollowersFollowingActivity.class);
                intent.putExtra("type", "following");
                intent.putExtra("uid", thisUserID);
                startActivity(intent);
            }
        });


        // Set the text for the follow/unfollow button
        thisUserRef.child("Followers").child(currentUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    btnFollow.setText("Follow");
                }
                else {
                    btnFollow.setText("Unfollow");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        // Display number of user's followers
        thisUserRef.child("Followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tvNumFollowers.setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Display how many people the user is following
        thisUserRef.child("Following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tvNumFollowing.setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /**
         * Inside method gets user's posts and displays them
         */


        /**
         * Inside gets current user's username and sets the
         * TextView to display it
         */
        thisUserRef.child("Username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = dataSnapshot.getValue(String.class);

                tvUsername.setText(username);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(OtherUserProfile.this,"Getting username failed.", Toast.LENGTH_SHORT).show();

            }
        });

        // Handle following/unfollowing when button is clicked
        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                thisUserRef.child("Followers").child(currentUID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            thisUserRef.child("Followers").child(currentUID).setValue(true);
                            currentUserRef.child("Following").child(thisUserID).setValue(true);
                            btnFollow.setText("Unfollow");
                        }
                        else {
                            thisUserRef.child("Followers").child(currentUID).removeValue();
                            currentUserRef.child("Following").child(thisUserID).removeValue();
                            btnFollow.setText("Follow");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

}
