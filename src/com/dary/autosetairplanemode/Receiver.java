package com.dary.autosetairplanemode;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

public class Receiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		ContentResolver cr = context.getContentResolver();
		if (intent.getBooleanExtra("AirPlaneModeOn", true)) {
			Settings.System.putString(cr, Settings.System.AIRPLANE_MODE_ON, "1");
			Intent intentOn = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
			context.sendBroadcast(intentOn);
			Tools.makeNotification(context, "AirPlane Mode On");
			PreferencesActivity.airPlaneModeOn.setChecked(true);
		} else {
			Settings.System.putString(cr, Settings.System.AIRPLANE_MODE_ON, "0");
			Intent intentOff = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
			context.sendBroadcast(intentOff);
			Tools.makeNotification(context, "AirPlane Mode Off");
			PreferencesActivity.airPlaneModeOn.setChecked(false);
		}
	}

}
