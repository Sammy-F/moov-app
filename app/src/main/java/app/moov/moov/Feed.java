package app.moov.moov;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class Feed extends AppCompatActivity {

    private Button btnLogout;
    private FirebaseAuth firebaseAuth;

    private RecyclerView rvFeed;
    private RecyclerView.Adapter feedAdapter;
    private RecyclerView.LayoutManager feedManager;

    private ArrayList<String> testStrings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        firebaseAuth = FirebaseAuth.getInstance();
        setUIViews();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

    }

    private void logout() {
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(Feed.this, MainActivity.class));
    }

    private void setUIViews(){

        testStrings.add("lorem");
        testStrings.add("ipsum");
        testStrings.add("placeholder");

        btnLogout = (Button) findViewById(R.id.btnLogout);
        rvFeed = (RecyclerView) findViewById(R.id.rvFeed);

        //improve performance if all posts have fixed size
        rvFeed.hasFixedSize();

        //set recycler view manager
        feedManager = new LinearLayoutManager(this);
        rvFeed.setLayoutManager(feedManager);

        //specify adapter
        feedAdapter = new FeedAdapter(testStrings);

    }
}
