package app.moov.moov.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import app.moov.moov.R;

/**
 * Created by Sammy on 3/22/18
 * Class for testing movie searching.
 */

public class ChooseMovieActivity extends AppCompatActivity {

    private EditText etMovieQuery;
    private Button btnSearchMovies;
    String searchQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_movie);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUIViews();
    }

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
                    Intent intent = new Intent(ChooseMovieActivity.this, MovieSearchResultsActivity.class);
                    intent.putExtra("searchQuery", searchQuery);
                    startActivity(intent);
                }
            }
        });
    }

}
