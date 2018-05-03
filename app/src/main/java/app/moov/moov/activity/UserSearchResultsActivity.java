package app.moov.moov.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
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

import app.moov.moov.R;
import app.moov.moov.model.User;
import app.moov.moov.util.UserResultViewHolder;

/**
 * Created by Lisa on 30/03/18.
 * Modified by Sammy 3/30/2019
 */

/**
 * This class is the page that displays the results of the user search from the bottom nav view
 */
public class UserSearchResultsActivity extends ToolbarBaseActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference usernamesRef;
    private RecyclerView searchResultList;
    private String userID;
    private String username;
    private ProgressBar progressBar;
    private String searchString;

    /**
     * Initializes all of the object (like toolbar, bottom nav bar), sets up data references
     * and specifies which layout XML to use
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_search_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolBarSetup(toolbar);

        database = FirebaseDatabase.getInstance();
        usernamesRef = database.getReference().child("Users");
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
        searchString = getIntent().getStringExtra("searchString");

        setUIViews(); // Initialize layout object variables
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Firebase Query using the string that was entered
        final Query firebaseSearchQuery = usernamesRef.orderByChild("lowername")
                .startAt(searchString).endAt(searchString + "\uf8ff").limitToFirst(20); //capping at 20 users

        //Generate a FirebaseRecyclerOptions object that lets us set the query and model class
        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(firebaseSearchQuery, User.class)
                        .build();

        FirebaseRecyclerAdapter<User, UserResultViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, UserResultViewHolder>(options) {

            private FirebaseStorage firebaseStorage;
            private StorageReference allAvRef;

            /**
             * Creates the cardview (specifies the layout), and then gets the data reference to what is going
             * to be populated in the cards
             * @param parent
             * @param viewType
             * @return
             */
            @Override
            public UserResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.cv_user_search_result, parent, false);

                firebaseStorage = FirebaseStorage.getInstance();
                allAvRef = firebaseStorage.getReference().child("images").child("avatars");

                return new UserResultViewHolder(view);
            }

            /**
             * Sets the actual data from firebase to each card (username, avatar, full name)
             * @param viewHolder
             * @param position
             * @param model
             */
            @Override
            protected void onBindViewHolder(final UserResultViewHolder viewHolder, int position, User model) {
                viewHolder.setUsername(model.getUsername()); //changed back
                viewHolder.setFullName(model.getFirstName() + " " + model.getLastName());
                username = model.getlowername();

                //Retrieve the user information from Firebase
                database.getReference().child("lusernames").child(username).addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String uid = (String) dataSnapshot.getValue();
                        viewHolder.setUID(uid);
                        StorageReference avatarRef = allAvRef.child(uid + ".png");
                        avatarRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Glide.with(UserSearchResultsActivity.this).asBitmap().load(uri.toString()).into(viewHolder.getIvAvatar());
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Clicked
                        if (viewHolder.getUid().equals(firebaseAuth.getCurrentUser().getUid())) {  //If the user's own profile is selected, go to a self user profile
                            Intent intent = new Intent(UserSearchResultsActivity.this, UserProfileActivity.class);
                            startActivity(intent);
                        }
                        else { //If another user's profile is selected, go to an other user profile for them
                            Intent intent = new Intent(UserSearchResultsActivity.this, OtherUserProfile.class);
                            intent.putExtra("thisUserID", viewHolder.getUid());
                            startActivity(intent);
                        }
                    }
                });
            }
        };
        firebaseRecyclerAdapter.startListening();
        searchResultList.setAdapter(firebaseRecyclerAdapter);
        progressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Set up the Views in the Activity.
     */
    private void setUIViews() {
        searchResultList = (RecyclerView) findViewById(R.id.userSearchRecycler);
        searchResultList.setHasFixedSize(true);
        searchResultList.setLayoutManager(new LinearLayoutManager(this));
        progressBar = (ProgressBar) findViewById(R.id.resultsProgress);
        progressBar.setVisibility(View.VISIBLE);

        BottomNavigationView navBar = findViewById(R.id.navBar);
        BottomNavigationViewHelper.disableShiftMode(navBar);
        setUpNavBar(navBar);

    }

}



