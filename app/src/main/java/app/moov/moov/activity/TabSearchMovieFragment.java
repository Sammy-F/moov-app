package app.moov.moov.activity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import app.moov.moov.R;

/**
 * Created by Lisa on 31/03/18.
 */

public class TabSearchMovieFragment extends Fragment {

    private TextView tvPromptUID;
    private EditText etMovieTitle;
    private Button btnSearchMovie;
    private String userID;
    private String username;
    private ConstraintLayout constraintLayout;
    private String searchQuery;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_search_movie_fragment, container, false);

        btnSearchMovie = (Button) view.findViewById(R.id.btnSearchMovie);
        btnSearchMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchText = etMovieTitle.getText().toString().toLowerCase();
                Intent intent = new Intent(getActivity(), UserSearchResultsActivity.class);
                intent.putExtra("searchString", searchText);
                startActivity(intent);
            }
        });
        etMovieTitle = (EditText) view.findViewById(R.id.etMovieTitle);
        tvPromptUID = (TextView) view.findViewById(R.id.tvPrompUID);
//        view.findViewById(R.id.constraintLayout).setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
//                return true;
//            }
//        });

        btnSearchMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchQuery = etMovieTitle.getText().toString();
                if (searchQuery.length() == 0) {
                    Toast.makeText(getActivity(),"Please input a search query.", Toast.LENGTH_SHORT).show();
                } else {
//                    Intent intent = new Intent(ChooseMovieActivity.this, MovieSearchResultsActivity.class);
                    Intent intent = new Intent(getActivity(), MovieSearchResultsActivity.class);
                    intent.putExtra("searchQuery", searchQuery);
                    startActivity(intent);
                }
            }
        });
        return view;
    }
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
