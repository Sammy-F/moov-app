package app.moov.moov.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import app.moov.moov.R;

public class ToolbarBaseActivity extends AppCompatActivity {

    FirebaseAuth signOutAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar_base);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id==R.id.addIcon) {
            Intent intent = new Intent(this,ChooseMovieActivity.class);
            startActivity(intent);
        }

        if (id==R.id.logoutIcon) {
            signOutAuth = FirebaseAuth.getInstance();
            signOutAuth.signOut();
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }

        if (id==R.id.profileIcon) {
            Intent intent = new Intent(this, UserProfileActivity.class);
            startActivity(intent);
        }

        if (id==R.id.searchIcon) {
            Intent intent = new Intent(this,SearchActivity.class);
            startActivity(intent);
        }

        if (id==R.id.settingsIcon) {
            Intent intent = new Intent(this,UserSettingsActivity.class);
            startActivity(intent);
        }

        if (id == android.R.id.home) {
            Intent intent = new Intent(this, FeedActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_feed_page, menu);
        return true;
    }

}
