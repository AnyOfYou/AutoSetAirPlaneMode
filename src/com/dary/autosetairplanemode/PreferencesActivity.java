package com.dary.autosetairplanemode;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.provider.Settings;

public class PreferencesActivity extends PreferenceActivity implements OnPreferenceChangeListener {
	static CheckBoxPreference airPlaneModeOn;
	private static final String TURN_ON_OFF_AIRPLANE_MODE = "com.dary.autosetairplanemode.TurnOnOffAirPlaneMode";
	private TimePreference autoTurnOnAirPlaneModeTime;
	private TimePreference autoTurnOffAirPlaneModeTime;
	private CheckBoxPreference isRepeat;
	private int requestCodeOn = 0;
	private int requestCodeOff = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		ListPreference airPlaneMode = (ListPreference) findPreference("airPlaneMode");
		airPlaneModeOn = (CheckBoxPreference) findPreference("airPlaneModeOn");
		CheckBoxPreference autoTurnOnOffAirPlaneMode = (CheckBoxPreference) findPreference("autoTurnOnOffAirPlaneMode");
		autoTurnOnAirPlaneModeTime = (TimePreference) findPreference("autoTurnOnAirPlaneModeTime");
		autoTurnOffAirPlaneModeTime = (TimePreference) findPreference("autoTurnOffAirPlaneModeTime");
		isRepeat = (CheckBoxPreference) findPreference("isRepeat");
		airPlaneMode.setOnPreferenceChangeListener(this);
		airPlaneModeOn.setOnPreferenceChangeListener(this);
		autoTurnOnOffAirPlaneMode.setOnPreferenceChangeListener(this);
		autoTurnOnAirPlaneModeTime.setOnPreferenceChangeListener(this);
		autoTurnOffAirPlaneModeTime.setOnPreferenceChangeListener(this);
		isRepeat.setOnPreferenceChangeListener(this);
	}

	public boolean onPreferenceChange(Preference preference, Object newValue) {
		System.out.println(System.currentTimeMillis());
		System.out.println(preference.getKey());
		System.out.println(newValue.toString());
		if (preference.getKey().equals("airPlaneMode")) {
			ContentResolver cr = getContentResolver();
			Settings.System.putString(cr, Settings.System.AIRPLANE_MODE_RADIOS, newValue.toString());
		} else if (preference.getKey().equals("airPlaneModeOn")) {
			ContentResolver cr = getContentResolver();
			if ((Boolean) newValue) {
				Settings.System.putString(cr, Settings.System.AIRPLANE_MODE_ON, "1");
				Intent intentOn = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
				sendBroadcast(intentOn);
			} else {
				Settings.System.putString(cr, Settings.System.AIRPLANE_MODE_ON, "0");
				Intent intentOff = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
				sendBroadcast(intentOff);
			}
		} else if (preference.getKey().equals("autoTurnOnOffAirPlaneMode")) {
			if ((Boolean) newValue) {
				setAirPlaneMode(true, autoTurnOnAirPlaneModeTime.getTime(), isRepeat.isChecked());
				setAirPlaneMode(false, autoTurnOffAirPlaneModeTime.getTime(), isRepeat.isChecked());
			} else {
				cancel();
			}
		} else if (preference.getKey().equals("autoTurnOnAirPlaneModeTime")) {
			setAirPlaneMode(true, (Long) newValue, isRepeat.isChecked());
		} else if (preference.getKey().equals("autoTurnOffAirPlaneModeTime")) {
			setAirPlaneMode(false, (Long) newValue, isRepeat.isChecked());
		} else if (preference.getKey().equals("isRepeat")) {
			if ((Boolean) newValue) {
				setAirPlaneMode(true, autoTurnOnAirPlaneModeTime.getTime(), true);
				setAirPlaneMode(false, autoTurnOffAirPlaneModeTime.getTime(), true);
			} else {
				setAirPlaneMode(true, autoTurnOnAirPlaneModeTime.getTime(), false);
				setAirPlaneMode(false, autoTurnOffAirPlaneModeTime.getTime(), false);
			}
		}
		return true;
	}

	@Override
	protected void onResume() {
		ContentResolver cr = getContentResolver();
		if (Settings.System.getString(cr, Settings.System.AIRPLANE_MODE_ON).equals("0")) {
			airPlaneModeOn.setChecked(false);
		} else {
			airPlaneModeOn.setChecked(true);
		}
		super.onResume();
	}

	private void setAirPlaneMode(boolean isOn, long time, boolean isRepeat) {
		if (time == 0) {
			Tools.makeToast(this, getString(R.string.please_select_set_airplane_mode_auto) + (isOn ? getString(R.string.on) : getString(R.string.off)) + getString(R.string.time));
		} else {
			int requestCode = isOn ? requestCodeOn : requestCodeOff;
			AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
			Intent intent = new Intent(TURN_ON_OFF_AIRPLANE_MODE);
			intent.putExtra("AirPlaneModeOn", isOn);
			PendingIntent pi = PendingIntent.getBroadcast(this, requestCode, intent, 0);
			if (isRepeat) {
				am.setRepeating(AlarmManager.RTC, time, 1000 * 60 * 60 * 24, pi);
				Tools.makeToastSetAirPlaneMode(this, isOn, time, isRepeat);
			} else {
				am.set(AlarmManager.RTC, time, pi);
				Tools.makeToastSetAirPlaneMode(this, isOn, time, isRepeat);
			}
		}
	}

	private void cancel() {
		Intent intent = new Intent(TURN_ON_OFF_AIRPLANE_MODE);
		PendingIntent piOn = PendingIntent.getBroadcast(this, requestCodeOn, intent, 0);
		PendingIntent piOff = PendingIntent.getBroadcast(this, requestCodeOff, intent, 0);
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.cancel(piOn);
		am.cancel(piOff);
		Tools.makeToast(this, getString(R.string.cancel_all));
	}
}
