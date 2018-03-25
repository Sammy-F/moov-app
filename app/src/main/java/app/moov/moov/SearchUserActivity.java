package app.moov.moov;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Lisa on 24/03/18.
 */

public class SearchUserActivity extends AppCompatActivity{

    private TextView tvPromptUID;
    private EditText etUsername;
    private Button btnSearchUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference usernamesRef;
    private RecyclerView searchResultList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUIViews(); // Initialize layout object variables

        database = FirebaseDatabase.getInstance();
        usernamesRef = database.getReference().child("Users");
        firebaseAuth = FirebaseAuth.getInstance();

        btnSearchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firebaseUserSearch();
            }
        });
    }


    private void firebaseUserSearch() {
        FirebaseRecyclerAdapter<User, SearchUserActivity.UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, SearchUserActivity.UsersViewHolder>(
                User.class,
                R.layout.user_search_layout,
                SearchUserActivity.UsersViewHolder.class,
                usernamesRef
        ) {

            @Override
            protected void populateViewHolder(SearchUserActivity.UsersViewHolder viewHolder, User model, int position) {
                viewHolder.setUsername(model.getUsername());
            }

        };

        searchResultList.setAdapter(firebaseRecyclerAdapter);
    }

    //VIEWHOLDER CLASS
    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setUsername(String userName) {
            TextView tvUserName = (TextView) itemView.findViewById(R.id.tvUsername);
            tvUserName.setText(userName);
        }
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
