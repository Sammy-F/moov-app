package app.moov.moov.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

import app.moov.moov.R;

/**
 * This is the class that all classes in our app.
 * It sets up the toolbar on every page.
 */
public class ToolbarBaseActivity extends AppCompatActivity {

    FirebaseAuth signOutAuth;

    /**
     * Sets up the toolbar
     * @param toolbar
     */
    public void toolBarSetup(Toolbar toolbar) {
        if (Build.VERSION.SDK_INT > 21) {
            toolbar.setElevation(5);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    /**
     * Creates intents for when the different icons are selected (directs the user to a new page)
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

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

    /**
     * References the menu_feed_page layout that it will use to inflate the toolbar
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_feed_page, menu);
        return true;
    }

    /**
     * Creates intents for when different icons in the bottom nav bar are selected (Directs the user to different pages)
     * @param navBar
     */
    public void setUpNavBar(BottomNavigationView navBar) {
        navBar.setOnNavigationItemSelectedListener(                new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.navBarHomeIcon) {
                    if (!(ToolbarBaseActivity.this instanceof FeedActivity)) { //check if clicked is same as existing
                        Intent intent = new Intent(ToolbarBaseActivity.this, FeedActivity.class);
                        startActivity(intent);
                    }
                }

                if (item.getItemId() == R.id.navBarSearchIcon) {
                    if (!(ToolbarBaseActivity.this instanceof SearchActivity)) {
                        Intent intent = new Intent(ToolbarBaseActivity.this, SearchActivity.class);
                        startActivity(intent);
                    }
                }

                if (item.getItemId() == R.id.navBarPersonIcon) {
                    if (!(ToolbarBaseActivity.this instanceof  UserProfileActivity)) {
                        Intent intent = new Intent(ToolbarBaseActivity.this, UserProfileActivity.class);
                        startActivity(intent);
                    }
                }

                if (item.getItemId() == R.id.navBarAddIcon) {
                    if (!(ToolbarBaseActivity.this instanceof ChooseMovieActivity)) {
                        Intent intent = new Intent(ToolbarBaseActivity.this, ChooseMovieActivity.class);
                        startActivity(intent);
                    }
                }

                return true;
            }
        });

    }

}
