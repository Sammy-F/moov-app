package app.moov.moov.util;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.moov.moov.R;

/**
 * Created by EFrost on 3/28/2018.
 */



public class FragmentSummary extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle
                                     savedInstanceState) {

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.content_movie_profile,
                container, false);
        return view;
    }
}
