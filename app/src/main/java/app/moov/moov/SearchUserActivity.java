package app.moov.moov;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Lisa on 24/03/18.
 * Modified by Sammy 3/29/2018
 *
 * Class handles user searching and
 * will handle movie searching in the future.
 */

public class SearchUserActivity extends AppCompatActivity{

    private TextView tvPromptUID;
    private EditText etUsername;
    private Button btnSearchUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference usernamesRef;
    private RecyclerView searchResultList;
    private String userID;
    private String username;
//    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUIViews(); // Initialize layout object variables

        database = FirebaseDatabase.getInstance();
        //usernamesRef = database.getReference().child("Users");
        usernamesRef = database.getReference().child("Users");
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();

        btnSearchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchText = etUsername.getText().toString();
                firebaseUserSearch(searchText);
            }
        });
    }


    private void firebaseUserSearch(String searchText) {
        Query firebaseSearchQuery = usernamesRef.orderByChild("Username").startAt(searchText).endAt(searchText + "\uf8ff");

        FirebaseRecyclerAdapter<User, SearchUserActivity.UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, SearchUserActivity.UsersViewHolder>(
                User.class,
                R.layout.user_search_layout,
                SearchUserActivity.UsersViewHolder.class,
                firebaseSearchQuery
        ) {

            @Override
            protected void populateViewHolder(final SearchUserActivity.UsersViewHolder viewHolder, User model, int position) {
                viewHolder.setUsername(model.getUsername());
                username = model.getUsername();

                database.getReference().child("Usernames").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
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
                                Intent intent = new Intent(SearchUserActivity.this, UserProfileActivity.class);
                                startActivity(intent);
                            }
                            else {
                                Intent intent = new Intent(SearchUserActivity.this, OtherUserProfile.class);
                                intent.putExtra("thisUserID", viewHolder.getUid());
                                startActivity(intent);
                            }
                    }
                });
            }
        };
        searchResultList.setAdapter(firebaseRecyclerAdapter);
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    //VIEWHOLDER CLASS
    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        View mView;
        private String thisUid;
        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setUsername(String userName) {
            TextView tvUserName = (TextView) itemView.findViewById(R.id.tvUsername);
            tvUserName.setText(userName);
        }

        public void setUID(String UID) {
            this.thisUid = UID;
        }

        public String getUid() { return thisUid; }
    }

    private void setUIViews() {
        tvPromptUID = (TextView) findViewById(R.id.tvPrompUID);
        etUsername = (EditText) findViewById(R.id.etUsername);
        btnSearchUser = (Button) findViewById(R.id.btnSearchUser);
        searchResultList = (RecyclerView) findViewById(R.id.rvSearchResult);
        searchResultList.setHasFixedSize(true);
        searchResultList.setLayoutManager(new LinearLayoutManager(this));
    }



}
