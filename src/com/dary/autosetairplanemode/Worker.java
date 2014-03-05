/**
 * 
 */

package com.dary.autosetairplanemode;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.provider.Settings;
import android.widget.Toast;

import com.spazedog.lib.rootfw3.RootFW;
import com.spazedog.lib.rootfw3.extenders.ShellExtender.ShellResult;

/**
 * @author Dary
 */
public class Worker {
    private Activity mActivity;

    public Worker(Activity activity) {
        mActivity = activity;
    }

    public void setAirPlaneModeMode(String value) {
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1) {
            SetAirPlaneModeModeTask sapmmt = new SetAirPlaneModeModeTask();
            sapmmt.execute(value);
        } else {
            ContentResolver cr = AppApplication.getInstance().getContentResolver();
            Settings.System
                    .putString(cr, Settings.System.AIRPLANE_MODE_RADIOS, value);
        }
    }

    public void setAirPlaneModeOnOff(boolean isOn) {
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1) {
            SetAirPlaneModeOnOffTask aspmoot = new SetAirPlaneModeOnOffTask();
            aspmoot.execute(isOn);
        } else {
            ContentResolver cr = AppApplication.getInstance().getContentResolver();
            if (isOn) {
                Settings.System.putString(cr, Settings.System.AIRPLANE_MODE_ON, "1");
                Intent intentOn = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
                AppApplication.getInstance().sendBroadcast(intentOn);
            } else {
                Settings.System.putString(cr, Settings.System.AIRPLANE_MODE_ON, "0");
                Intent intentOff = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
                AppApplication.getInstance().sendBroadcast(intentOff);
            }
        }
    }

    class SetAirPlaneModeModeTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            RootFW root = new RootFW();
            if (root.connect()) {
                ShellResult result = root.shell().run(
                        "settings put global airplane_mode_radios " + params[0]);
                if (result.wasSuccessful()) {
                    String line = result.getLine();
                    System.out.println(line);
                }
                root.disconnect();
            } else {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(AppApplication.getInstance(), "Done", Toast.LENGTH_LONG).show();
            } else {
                Tools.showDialog(mActivity, "Root Failure");
            }
            super.onPostExecute(result);
        }

    }

    class SetAirPlaneModeOnOffTask extends AsyncTask<Boolean, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Boolean... params) {
            if (params[0]) {
                RootFW root = new RootFW();
                if (root.connect()) {
                    ShellResult result = root
                            .shell()
                            .addCommands("settings put global airplane_mode_on 1",
                                    "am broadcast -a android.intent.action.AIRPLANE_MODE --ez state true")
                            .run();
                    if (result.wasSuccessful()) {
                        String line = result.getLine();
                        System.out.println(line);
                    }
                    root.disconnect();
                } else {
                    return false;
                }
            } else {
                RootFW root = new RootFW();
                if (root.connect()) {
                    ShellResult result = root
                            .shell()
                            .addCommands("settings put global airplane_mode_on 0",
                                    "am broadcast -a android.intent.action.AIRPLANE_MODE --ez state false")
                            .run();
                    if (result.wasSuccessful()) {
                        String line = result.getLine();
                        System.out.println(line);
                    }
                    root.disconnect();
                } else {
                    return false;
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(AppApplication.getInstance(), "Done", Toast.LENGTH_LONG).show();
            } else {
                Tools.showDialog(mActivity, "Root Failure");
            }
            super.onPostExecute(result);
        }

    }
}
