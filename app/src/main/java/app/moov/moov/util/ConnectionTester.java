package app.moov.moov.util;

import java.io.IOException;

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
