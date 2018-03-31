package app.moov.moov.util;

import android.content.Context;

/**
 * Created by Sammy on 3/30/2018.
 */

public class MyApp extends android.app.Application {
    private static MyApp instance;

    public MyApp() {
        instance = this;
    }

    public static Context getContext() {
        return instance;
    }
}
