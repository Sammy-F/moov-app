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

public class ToolbarBaseActivity extends AppCompatActivity {

    FirebaseAuth signOutAuth;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_toolbar_base);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//    }

    public void toolBarSetup(Toolbar toolbar) {
        if (Build.VERSION.SDK_INT > 21) {
            toolbar.setElevation(5);
        }
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

//        if (id==R.id.profileIcon) {
//            Intent intent = new Intent(this, UserProfileActivity.class);
//            startActivity(intent);
//        }

//        if (id==R.id.searchIcon) {
//            Intent intent = new Intent(this,SearchActivity.class);
//            startActivity(intent);
//        }

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

    public void setUpNavBar(BottomNavigationView navBar) {
        navBar.setOnNavigationItemSelectedListener(                new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.navBarHomeIcon) {
                    Intent intent = new Intent(getApplicationContext(), FeedActivity.class);
                    startActivity(intent);
                }

                if (item.getItemId() == R.id.navBarSearchIcon) {
                    Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                    startActivity(intent);
                }

                if (item.getItemId() == R.id.navBarPersonIcon) {
                    Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                    startActivity(intent);
                }

//                if (item.getItemId() == R.id.addIcon) {
//                    Intent intent = new Intent(getApplicationContext(), ChooseMovieActivity.class);
//                    startActivity(intent);
//                }

                return true;
            }
        });

    }

}
