package app.moov.moov.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import app.moov.moov.R;
import app.moov.moov.activity.UserSearchResultsActivity;

/**
 * Created by Lisa on 31/03/18.
 *  * This is the tab for the user search on the bottom nav view

 */

public class TabSearchUserFragment extends Fragment {

    private TextView tvPromptUID;
    private EditText etUsername;
    private Button btnSearchUser;
    private String userID;
    private String username;
    private ConstraintLayout constraintLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_search_user_fragment, container, false);

        btnSearchUser = (Button) view.findViewById(R.id.btnSearchUser);
        btnSearchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchText = etUsername.getText().toString().toLowerCase();
                Intent intent = new Intent(getActivity(), UserSearchResultsActivity.class);
                intent.putExtra("searchString", searchText);
                startActivity(intent);
            }
        });
        etUsername = (EditText) view.findViewById(R.id.etUsername);
        tvPromptUID = (TextView) view.findViewById(R.id.tvPrompUID);
        view.findViewById(R.id.constraintLayout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                return true;
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
