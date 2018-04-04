package app.moov.moov.util;

import android.content.Context;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by Lisa on 04/04/18.
 */

public class MovieProfileAdapter {

    private Context c;
    private JSONObject movieJSONObject;

    public MovieProfileAdapter(Context c, JSONObject movieJSONObject) {
        this.c = c;
        this.movieJSONObject = movieJSONObject;
    }

}
