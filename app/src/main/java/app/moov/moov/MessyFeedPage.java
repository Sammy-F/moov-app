package app.moov.moov;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class MessyFeedPage extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private RecyclerView rvView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_page);
        firebaseAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        rvView = (RecyclerView) findViewById(R.id.rvView);
        rvView.setHasFixedSize(true);

        rvView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_feed_page, menu);
        return true;
    }

    public class rvHolder extends RecyclerView.ViewHolder {

        public rvHolder(View itemView) {
            super(itemView);
            View mView = itemView;
        }

        public void setTitle(String movieTitle) {
            TextView thisMovie = (TextView) itemView.findViewById(R.id.MovieTitle);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id==R.id.addIcon) {
            Intent intent = new Intent(MessyFeedPage.this,PostActivity.class);
            startActivity(intent);
        }

        if (id==R.id.logoutIcon) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(MessyFeedPage.this, MainActivity.class));
        }

        if (id==R.id.profileIcon) {
            Intent intent = new Intent(MessyFeedPage.this, UserProfileActivity.class);
            startActivity(intent);
        }

        if (id==R.id.searchIcon) {
            Intent intent = new Intent(MessyFeedPage.this,FindUserActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

}
