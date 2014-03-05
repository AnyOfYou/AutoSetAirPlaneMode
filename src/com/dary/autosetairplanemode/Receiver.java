
package com.dary.autosetairplanemode;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ContentResolver cr = context.getContentResolver();
        if (intent.getBooleanExtra("AirPlaneModeOn", true)) {
            Worker worker = new Worker(null);
            worker.setAirPlaneModeOnOff(true);
            if (PreferencesActivity.airPlaneModeOn != null) {
                PreferencesActivity.airPlaneModeOn.setChecked(true);
            }
            doNotification(context, context.getString(R.string.notification_airplane_mode_on));

        } else {
            Worker worker = new Worker(null);
            worker.setAirPlaneModeOnOff(false);
            if (PreferencesActivity.airPlaneModeOn != null) {
                PreferencesActivity.airPlaneModeOn.setChecked(false);
            }
            doNotification(context, context.getString(R.string.notification_airplane_mode_off));
        }
    }

    private void doNotification(Context context, String str) {
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
