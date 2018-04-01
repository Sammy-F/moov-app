package app.moov.moov.activity;

import android.os.Bundle;
import android.support.design.internal.NavigationMenuItemView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.google.firebase.auth.FirebaseAuth;

import app.moov.moov.R;

/**
 * Created by Lisa on 31/03/18.
 */

public class BottomNavbarActivity extends AppCompatActivity{

    FirebaseAuth signOutAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navbar);
        NavigationMenuItemView navBar = (NavigationMenuItemView) findViewById(R.id.navBar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_nav_bar, menu);
        return true;
    }


}
