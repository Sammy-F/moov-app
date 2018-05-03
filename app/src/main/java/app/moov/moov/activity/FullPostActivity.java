package app.moov.moov.activity;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import app.moov.moov.R;

/**
 * This class is the page that shows the full post (for posts which have longer reviews and the
 * review is not contained in the cardview
 */
public class FullPostActivity extends ToolbarBaseActivity {

    private TextView tvUsername;
    private TextView tvMovieTitle;
    private TextView tvMovieReview;
    private RatingBar rbRating;
    private ImageView ivMoviePoster;

    private String username;
    private String review;
    private String title;
    private String posterURL;
    private int numStars;

    /**
     * Initializes all of the object (like toolbar, bottom nav bar), sets up data references
     * and specifies which layout XML to use
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_post);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolBarSetup(toolbar);

        BottomNavigationView navBar = (BottomNavigationView) findViewById(R.id.navBar);
        BottomNavigationViewHelper.disableShiftMode(navBar);
        setUpNavBar(navBar);

        username = getIntent().getStringExtra("username");
        review = getIntent().getStringExtra("review");
        title = getIntent().getStringExtra("title");
        posterURL = getIntent().getStringExtra("posterURL");
        numStars = getIntent().getIntExtra("numStars", 0);

        setUIViews();

    }

    /**
     * Initialize the View variables used in
     * the Activity.
     */
    private void setUIViews() {
        tvUsername = (TextView) findViewById(R.id.Username);
        tvMovieReview = (TextView) findViewById(R.id.MovieReview);
        tvMovieTitle = (TextView) findViewById(R.id.MovieTitle);
        ivMoviePoster = (ImageView) findViewById(R.id.ivPoster);
        rbRating = (RatingBar) findViewById(R.id.ratingBar);

        if (stringIsValid(username)) {
            tvUsername.setText(username);
        } else {
            tvUsername.setText("");
        }
        if (stringIsValid(review)) {
            tvMovieReview.setText(review);
        } else {
            tvMovieReview.setText("Error getting review.");
        }
        if (stringIsValid(title)) {
            tvMovieTitle.setText(title);
        } else {
            tvMovieTitle.setText("Unable to get movie title.");
        }

        rbRating.setRating(numStars);

        try {
            Glide.with(this).asBitmap().load(posterURL).into(ivMoviePoster);
        } catch (NullPointerException e) {
            Toast.makeText(this, "Error loading poster", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * A method to make sure that the string is not null or empty
     * @param checkStr
     * @return
     */
    private boolean stringIsValid(String checkStr) {
        if (checkStr != null && !checkStr.trim().equals("")) {
            return true;
        } else {
            return false;
        }
    }

}
