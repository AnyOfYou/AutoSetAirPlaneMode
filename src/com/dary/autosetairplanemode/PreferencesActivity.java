package com.dary.autosetairplanemode;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.provider.Settings;

public class PreferencesActivity extends PreferenceActivity implements OnPreferenceChangeListener {
	static CheckBoxPreference airPlaneModeOn;
	private static final String TURN_ON_OFF_AIRPLANE_MODE = "com.dary.autosetairplanemode.TurnOnOffAirPlaneMode";
	private EditTextPreference autoTurnOnAirPlaneModeTime;
	private EditTextPreference autoTurnOffAirPlaneModeTime;
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
		autoTurnOnAirPlaneModeTime = (EditTextPreference) findPreference("autoTurnOnAirPlaneModeTime");
		autoTurnOffAirPlaneModeTime = (EditTextPreference) findPreference("autoTurnOffAirPlaneModeTime");
		isRepeat = (CheckBoxPreference) findPreference("isRepeat");
		airPlaneMode.setOnPreferenceChangeListener(this);
		airPlaneModeOn.setOnPreferenceChangeListener(this);
		autoTurnOnOffAirPlaneMode.setOnPreferenceChangeListener(this);
		autoTurnOnAirPlaneModeTime.setOnPreferenceChangeListener(this);
		autoTurnOffAirPlaneModeTime.setOnPreferenceChangeListener(this);
		isRepeat.setOnPreferenceChangeListener(this);

		autoTurnOnAirPlaneModeTime.setText("0000");
		autoTurnOffAirPlaneModeTime.setText("0000");
	}

	public boolean onPreferenceChange(Preference preference, Object newValue) {
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
				setAirPlaneMode(true, stringHourMinTimetoLongMillis(autoTurnOnAirPlaneModeTime.getText()), isRepeat.isChecked());
				setAirPlaneMode(false, stringHourMinTimetoLongMillis(autoTurnOffAirPlaneModeTime.getText()), isRepeat.isChecked());
			} else {
				cancel();
			}
		} else if (preference.getKey().equals("autoTurnOnAirPlaneModeTime")) {
			setAirPlaneMode(true, stringHourMinTimetoLongMillis(newValue.toString()), isRepeat.isChecked());
		} else if (preference.getKey().equals("autoTurnOffAirPlaneModeTime")) {
			setAirPlaneMode(false, stringHourMinTimetoLongMillis(newValue.toString()), isRepeat.isChecked());
		} else if (preference.getKey().equals("isRepeat")) {
			if ((Boolean) newValue) {
				setAirPlaneMode(true, stringHourMinTimetoLongMillis(autoTurnOnAirPlaneModeTime.getText()), true);
				setAirPlaneMode(false, stringHourMinTimetoLongMillis(autoTurnOffAirPlaneModeTime.getText()), true);
			} else {
				setAirPlaneMode(true, stringHourMinTimetoLongMillis(autoTurnOnAirPlaneModeTime.getText()), false);
				setAirPlaneMode(false, stringHourMinTimetoLongMillis(autoTurnOffAirPlaneModeTime.getText()), false);
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

	private void cancel() {
		Intent intent = new Intent(TURN_ON_OFF_AIRPLANE_MODE);
		PendingIntent piOn = PendingIntent.getBroadcast(this, requestCodeOn, intent, 0);
		PendingIntent piOff = PendingIntent.getBroadcast(this, requestCodeOff, intent, 0);
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.cancel(piOn);
		am.cancel(piOff);
		Tools.makeToast(this, "Cancel All");
	}

	private long stringHourMinTimetoLongMillis(String str) {
		String h = str.substring(0, 2);
		String m = str.substring(2);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(java.lang.System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(h));
		calendar.set(Calendar.MINUTE, Integer.parseInt(m));
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
			calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 1);
		}
		return calendar.getTimeInMillis();
	}

}
