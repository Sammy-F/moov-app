package app.moov.moov.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import app.moov.moov.R;

/**
 * Created by Lisa on 31/03/18.
 */

public class TabUserFragment extends android.support.v4.app.Fragment {
    private static final String TAG = "TabUserFragment";
    private Button btnSearchUser;
    private EditText etUsername;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_user_fragment, container, false);
        btnSearchUser = (Button) view.findViewById(R.id.btnSearchUser);
//        etUsername = (EditText) view.findViewById(R.id.etUsername);
        btnSearchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "TESTING", Toast.LENGTH_SHORT).show();

            }
        });

        return view;
    }
}
