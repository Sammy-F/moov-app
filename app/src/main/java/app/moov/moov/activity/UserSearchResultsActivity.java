package app.moov.moov.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import app.moov.moov.R;
import app.moov.moov.model.Post;
import app.moov.moov.model.User;
import app.moov.moov.util.UserResultViewHolder;

/**
 * Created by Lisa on 30/03/18.
 *
 * Modified by Sammy 3/30/2019
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        Query firebaseSearchQuery = usernamesRef.orderByChild("lowername").startAt(searchString).endAt(searchString + "\uf8ff");

        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(firebaseSearchQuery, User.class)
                        .build();

        FirebaseRecyclerAdapter<User, UserResultViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, UserResultViewHolder>(options) {

            @Override
            public UserResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.cv_user_search_result, parent, false);

                return new UserResultViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final UserResultViewHolder viewHolder, int position, User model) {
                viewHolder.setUsername(model.getUsername()); //changed back
                username = model.getlowername();
                database.getReference().child("lusernames").child(username).addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        uid = (String) dataSnapshot.getValue();
                        viewHolder.setUID((String) dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Clicked
                        if (viewHolder.getUid().equals(firebaseAuth.getCurrentUser().getUid())) {
                            Intent intent = new Intent(UserSearchResultsActivity.this, UserProfileActivity.class);
                            startActivity(intent);
                        }
                        else {
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

    private void setUIViews() {
        searchResultList = (RecyclerView) findViewById(R.id.userSearchRecycler);
        searchResultList.setHasFixedSize(true);
        searchResultList.setLayoutManager(new LinearLayoutManager(this));
        progressBar = (ProgressBar) findViewById(R.id.resultsProgress);
        progressBar.setVisibility(View.VISIBLE);
    }

//    //VIEWHOLDER CLASS
//    public static class UsersViewHolder extends RecyclerView.ViewHolder {
//        View mView;
//        private String thisUid;
//        public UsersViewHolder(View itemView) {
//            super(itemView);
//            mView = itemView;
//        }
//
//        public void setUsername(String userName) {
//            TextView tvUserName = (TextView) itemView.findViewById(R.id.tvUsername);
//            tvUserName.setText(userName);
//        }
//
//        public void setUID(String UID) {
//            this.thisUid = UID;
//        }
//
//        public String getUid() { return thisUid; }
//    }
}



