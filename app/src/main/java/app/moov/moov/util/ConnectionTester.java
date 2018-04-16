package app.moov.moov.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;

/**
 * Created by Sammy on 4/16/2018.
 *
 * Class used to check if there is an internet
 * connection.
 */

public class ConnectionTester {

    public ConnectionTester() {}

    /**
     * Returns true if internet is accessible, or false if
     * it is not.
     * @return
     */
    public boolean connectionExists() {
        //Old version test
//        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        NetworkInfo activeNetwork;
//
//        try {
//            activeNetwork = cm.getActiveNetworkInfo();
//        } catch (NullPointerException e) {
//            activeNetwork = null;
//        }
//        return  (activeNetwork != null)
//                && (activeNetwork.isConnectedOrConnecting());

        // Code for test by E J Chathuranga

        try {
            Process ipProcess = Runtime.getRuntime().exec("/system/bin/ping -c 1 8.8.8.8");
            return (ipProcess.waitFor() == 0);
        } catch (IOException e){
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;

    }
}
