package com.dary.autosetairplanemode;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;

public class PreferencesActivity extends PreferenceActivity implements OnPreferenceChangeListener {
	static CheckBoxPreference airPlaneModeOn;
	private static final String TURN_ON_OFF_AIRPLANE_MODE = "com.dary.autosetairplanemode.TurnOnOffAirPlaneMode";
	private TimePreference autoTurnOnAirPlaneModeTime;
	private TimePreference autoTurnOffAirPlaneModeTime;
	private CheckBoxPreference isRepeat;
	private static int requestCodeOn = 0;
	private static int requestCodeOff = 1;

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
				setAirPlaneMode(this,true, autoTurnOnAirPlaneModeTime.getTime(), isRepeat.isChecked());
				setAirPlaneMode(this,false, autoTurnOffAirPlaneModeTime.getTime(), isRepeat.isChecked());
			} else {
				cancel();
			}
		} else if (preference.getKey().equals("autoTurnOnAirPlaneModeTime")) {
			setAirPlaneMode(this,true, (Long) newValue, isRepeat.isChecked());
		} else if (preference.getKey().equals("autoTurnOffAirPlaneModeTime")) {
			setAirPlaneMode(this,false, (Long) newValue, isRepeat.isChecked());
		} else if (preference.getKey().equals("isRepeat")) {
			if ((Boolean) newValue) {
				setAirPlaneMode(this,true, autoTurnOnAirPlaneModeTime.getTime(), true);
				setAirPlaneMode(this,false, autoTurnOffAirPlaneModeTime.getTime(), true);
			} else {
				setAirPlaneMode(this,true, autoTurnOnAirPlaneModeTime.getTime(), false);
				setAirPlaneMode(this,false, autoTurnOffAirPlaneModeTime.getTime(), false);
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

	static void setAirPlaneMode(Context context, boolean isOn, long time, boolean isRepeat) {
		//刚刚装入程序,未设置时间时
		if (time == 0) {
			Tools.makeToast(context, context.getString(R.string.please_select_set_airplane_mode_auto) + (isOn ? context.getString(R.string.on) : context.getString(R.string.off)) + context.getString(R.string.time));
		} else {
			int requestCode = isOn ? requestCodeOn : requestCodeOff;
			AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
			Intent intent = new Intent(TURN_ON_OFF_AIRPLANE_MODE);
			intent.putExtra("AirPlaneModeOn", isOn);
			PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, intent, 0);
			if (isRepeat) {
				am.setRepeating(AlarmManager.RTC_WAKEUP, time, 1000 * 60 * 60 * 24, pi);
				Tools.makeToastSetAirPlaneMode(context, isOn, time, isRepeat);
			} else {
				am.set(AlarmManager.RTC_WAKEUP, time, pi);
				Tools.makeToastSetAirPlaneMode(context, isOn, time, isRepeat);
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

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, com.dary.autosetairplanemode.R.string.about);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case 0:
			// int类型的引用,不能直接相加
			new AlertDialog.Builder(this).setTitle(R.string.about).setMessage(getResources().getString(R.string.app_name) + "\n\n" + getResources().getString(R.string.github)).setPositiveButton(com.dary.autosetairplanemode.R.string.ok, null).show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
