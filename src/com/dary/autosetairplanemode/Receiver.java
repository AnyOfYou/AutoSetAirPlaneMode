
package com.dary.autosetairplanemode;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;

public class Receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ContentResolver cr = context.getContentResolver();
        if (intent.getBooleanExtra("AirPlaneModeOn", true)) {
            Settings.System.putString(cr, Settings.System.AIRPLANE_MODE_ON, "1");
            Intent intentOn = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            context.sendBroadcast(intentOn);

            if (PreferencesActivity.airPlaneModeOn != null) {
                PreferencesActivity.airPlaneModeOn.setChecked(true);
            }
            try {
                doNotification(context, context.getString(R.string.notification_airplane_mode_on));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Settings.System.putString(cr, Settings.System.AIRPLANE_MODE_ON, "0");
            Intent intentOff = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            context.sendBroadcast(intentOff);
            if (PreferencesActivity.airPlaneModeOn != null) {
                PreferencesActivity.airPlaneModeOn.setChecked(false);
            }
            try {
                doNotification(context, context.getString(R.string.notification_airplane_mode_off));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void doNotification(Context context, String str) throws Exception {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String notificationType = mPrefs.getString("notificationType", "none");
        if (notificationType.equals("none")) {

        } else if (notificationType.equals("notification")) {
            Tools.makeNotification(context, str);
        } else if (notificationType.equals("vibrate")) {
            Tools.Vibrator(context, 1000);
        } else if (notificationType.equals("sound")) {
            Tools.makeSound(context);
        } else if (notificationType.equals("notification,vibrate")) {
            Tools.makeNotification(context, str);
            Tools.Vibrator(context, 1000);
        } else if (notificationType.equals("notification,sound")) {
            Tools.makeNotification(context, str);
            Tools.makeSound(context);
        } else if (notificationType.equals("vibrate,sound")) {
            Tools.Vibrator(context, 1000);
            Tools.makeSound(context);
        } else if (notificationType.equals("notification,vibrate,sound")) {
            Tools.makeNotification(context, str);
            Tools.Vibrator(context, 1000);
            Tools.makeSound(context);
        }
    }
}
