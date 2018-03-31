package app.moov.moov.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import app.moov.moov.R;
import app.moov.moov.util.SectionsPageAdapter;

/**
 * Created by Lisa on 31/03/18.
 */

public class SearchActivity2 extends AppCompatActivity{
    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search2);

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }
    SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
    private void setupViewPager(ViewPager viewPager) {
        adapter.addFragment(new TabUserFragment(), "USER_FRAGMENT");
        adapter.addFragment(new TabUserFragment2(), "USER_FRAGMENT2");

        viewPager.setAdapter(adapter);
    }
}
