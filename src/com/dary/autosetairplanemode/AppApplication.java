/**
 * 
 */

package com.dary.autosetairplanemode;

import android.app.Application;

/**
 * @author Dary
 */
public class AppApplication extends Application {
    private static AppApplication instance;

    @Override
    public void onCreate() {

        super.onCreate();
        instance = this;
    }

    public static AppApplication getInstance() {
        return instance;
    }

}
