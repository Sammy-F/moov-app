package app.moov.moov.activity;

import android.os.Bundle;
import android.support.design.internal.NavigationMenuItemView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import com.google.firebase.auth.FirebaseAuth;
import app.moov.moov.R;

/**
 * Created by Lisa on 31/03/18.
 * This class sets up the Bottom Navigation View
 */

public class BottomNavbarActivity extends AppCompatActivity{

    FirebaseAuth signOutAuth;

    /**
     * Sets the layout, and initializes the NavBar
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navbar);
        NavigationMenuItemView navBar = (NavigationMenuItemView) findViewById(R.id.navBar);
    }

    /**
     * Inflates the navBar with the items (Post/Search/Home/Profile) using the menu_nav_bar resource file
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_nav_bar, menu);
        return true;
    }

}
