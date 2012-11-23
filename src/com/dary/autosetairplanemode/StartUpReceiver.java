package com.dary.autosetairplanemode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class StartUpReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		boolean isStartAtBoot = mPrefs.getBoolean("isStartAtBoot", false);
		if (isStartAtBoot) {
			System.out.println("StartUp Receiver");
			long autoTurnOnAirPlaneModeTime = mPrefs.getLong("autoTurnOnAirPlaneModeTime", 0);
			long autoTurnOffAirPlaneModeTime = mPrefs.getLong("autoTurnOffAirPlaneModeTime", 0);
			boolean isRepeat = mPrefs.getBoolean("isRepeat", false);
			PreferencesActivity.setAirPlaneMode(context,true, autoTurnOnAirPlaneModeTime, isRepeat);
			PreferencesActivity.setAirPlaneMode(context, false, autoTurnOffAirPlaneModeTime, isRepeat);
		}
	}
}
