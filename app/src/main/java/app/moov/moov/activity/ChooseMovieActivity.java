package app.moov.moov.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import app.moov.moov.R;

/**
 * Created by Sammy on 3/22/18
 * Class for one of the main functions of testing movie searching.
 */

public class ChooseMovieActivity extends ToolbarBaseActivity {

    private EditText etMovieQuery;
    private Button btnSearchMovies;
    String searchQuery;

    /**
     * Initializes all of the object (like toolbar, bottom nav bar), sets up data references
     * and specifies which layout XML to use
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_movie);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolBarSetup(toolbar);

        BottomNavigationView navBar = (BottomNavigationView) findViewById(R.id.navBar);
        BottomNavigationViewHelper.disableShiftMode(navBar);
        setUpNavBar(navBar);

        setUIViews();
    }

    /**
     * Initialize the View variables used in
     * the Activity.
     */

    private void setUIViews() {
        etMovieQuery = (EditText) findViewById(R.id.etMovieQuery);
        btnSearchMovies = (Button) findViewById(R.id.btnSearchMovies);

        btnSearchMovies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchQuery = etMovieQuery.getText().toString();
                if (searchQuery.length() == 0) {
                    Toast.makeText(ChooseMovieActivity.this,"Please input a search query.", Toast.LENGTH_SHORT).show();
                } else {
//                    Intent intent = new Intent(ChooseMovieActivity.this, MovieSearchResultsActivity.class);
                    Intent intent = new Intent(ChooseMovieActivity.this, MovieSearchResultsActivity.class);
                    intent.putExtra("searchQuery", searchQuery);
                    startActivity(intent);
                }
            }
        });

        findViewById(R.id.constraintLayout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                return true;
            }
        });
    }

}
