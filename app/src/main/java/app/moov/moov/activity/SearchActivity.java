package app.moov.moov.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;


import app.moov.moov.R;
import app.moov.moov.fragment.TabSearchMovieFragment;
import app.moov.moov.fragment.TabSearchUserFragment;
import app.moov.moov.util.SectionsPageAdapter;

/**
 * Created by Lisa on 31/03/18.
 * This class is to search for both users and movies, included in the bottom nav view.
 */

public class SearchActivity extends ToolbarBaseActivity{
    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;

    /**
     * Initializes all of the object (like toolbar, bottom nav bar), sets up data references
     * and specifies which layout XML to use
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_activity);
        Toolbar toolbar  = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolBarSetup(toolbar);

        BottomNavigationView navBar = (BottomNavigationView) findViewById(R.id.navBar);
        BottomNavigationViewHelper.disableShiftMode(navBar);
        setUpNavBar(navBar);

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    //We are using fragments, so sets up the adapter for that
    SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());

    //Sets up the fragments
    private void setupViewPager(ViewPager viewPager) {
        adapter.addFragment(new TabSearchUserFragment(), "Search User");
        adapter.addFragment(new TabSearchMovieFragment(), "Search Movie");
        viewPager.setAdapter(adapter);
    }
}
