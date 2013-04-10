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
		boolean autoTurnOnOffAirPlaneMode = mPrefs.getBoolean("autoTurnOnOffAirPlaneMode",false);
		boolean isStartAtBoot = mPrefs.getBoolean("isStartAtBoot", false);
		if (autoTurnOnOffAirPlaneMode && isStartAtBoot) {
			System.out.println("StartUp Receiver");
			long autoTurnOnAirPlaneModeTime = TimePreference.getLastTime(mPrefs.getLong("autoTurnOnAirPlaneModeTime", 0));
			long autoTurnOffAirPlaneModeTime = TimePreference.getLastTime(mPrefs.getLong("autoTurnOffAirPlaneModeTime", 0));
			mPrefs.edit().putLong("autoTurnOnAirPlaneModeTime", autoTurnOnAirPlaneModeTime).commit();
			mPrefs.edit().putLong("autoTurnOffAirPlaneModeTime", autoTurnOffAirPlaneModeTime).commit();
			boolean isRepeat = mPrefs.getBoolean("isRepeat", false);
			PreferencesActivity.setAirPlaneMode(context, true, autoTurnOnAirPlaneModeTime, isRepeat);
			PreferencesActivity.setAirPlaneMode(context, false, autoTurnOffAirPlaneModeTime, isRepeat);
		}
	}
}
