package app.moov.moov.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Sammy on 4/16/2018.
 *
 * Class used to check if
 */

public class ConnectionTester {

    private Context mContext;

    public ConnectionTester(Context mContext) {

        this.mContext = mContext;

    }

    public boolean connectionExists() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork;

        try {
            activeNetwork = cm.getActiveNetworkInfo();
        } catch (NullPointerException e) {
            activeNetwork = null;
        }
        return  (activeNetwork != null)
                && (activeNetwork.isConnectedOrConnecting());
    }
}
