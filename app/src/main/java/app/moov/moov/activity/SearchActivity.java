package app.moov.moov.activity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import app.moov.moov.R;

/**
 * Created by Lisa on 24/03/18.
 * Modified by Sammy 3/29/2018
 *
 * Class handles user searching and
 * will handle movie searching in the future.
 */

public class SearchActivity extends AppCompatActivity{

    private TextView tvPromptUID;
    private EditText etUsername;
    private Button btnSearchUser;
    private String userID;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUIViews(); // Initialize layout object variables

        btnSearchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchText = etUsername.getText().toString().toLowerCase();
                Intent intent = new Intent(SearchActivity.this, UserSearchResultsActivity.class);
                intent.putExtra("searchString", searchText);
                startActivity(intent);
            }
        });
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void setUIViews() {
        tvPromptUID = (TextView) findViewById(R.id.tvPrompUID);
        etUsername = (EditText) findViewById(R.id.etUsername);
        btnSearchUser = (Button) findViewById(R.id.btnSearchUser);
    }
}