package app.moov.moov.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import app.moov.moov.R;
import app.moov.moov.fragment.TabSearchMovieFragment;
import app.moov.moov.fragment.TabSearchUserFragment;
import app.moov.moov.util.SectionsPageAdapter;

/**
 * Created by Lisa on 31/03/18.
 */

public class SearchActivity extends ToolbarBaseActivity{
    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_activity);
        Toolbar toolbar  = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolBarSetup(toolbar);


//        BottomNavigationViewEx navBar = (BottomNavigationViewEx) findViewById(R.id.navBar);
//        setUpNavBar(navBar);
        BottomNavigationView navBar = (BottomNavigationView) findViewById(R.id.navBar);
        BottomNavigationViewHelper.disableShiftMode(navBar);
        setUpNavBar(navBar);

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }
    SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
    private void setupViewPager(ViewPager viewPager) {
        adapter.addFragment(new TabSearchUserFragment(), "Search User");
        adapter.addFragment(new TabSearchMovieFragment(), "Search Movie");
        viewPager.setAdapter(adapter);
    }
}
