package com.dary.autosetairplanemode;

import java.util.Calendar;
import java.util.Date;

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
			if (autoTurnOnAirPlaneModeTime < System.currentTimeMillis()) {
				Date d = new Date(autoTurnOnAirPlaneModeTime);
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(java.lang.System.currentTimeMillis());
				calendar.set(Calendar.HOUR_OF_DAY, d.getHours());
				calendar.set(Calendar.MINUTE, d.getMinutes());
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 1);
				autoTurnOnAirPlaneModeTime = calendar.getTimeInMillis();
			}

			if (autoTurnOffAirPlaneModeTime < System.currentTimeMillis()) {
				Date d = new Date(autoTurnOffAirPlaneModeTime);
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(java.lang.System.currentTimeMillis());
				calendar.set(Calendar.HOUR_OF_DAY, d.getHours());
				calendar.set(Calendar.MINUTE, d.getMinutes());
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 1);
				autoTurnOffAirPlaneModeTime = calendar.getTimeInMillis();
			}
			boolean isRepeat = mPrefs.getBoolean("isRepeat", false);
			PreferencesActivity.setAirPlaneMode(context, true, autoTurnOnAirPlaneModeTime, isRepeat);
			PreferencesActivity.setAirPlaneMode(context, false, autoTurnOffAirPlaneModeTime, isRepeat);
		}
	}
}
