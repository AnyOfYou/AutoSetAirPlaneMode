
package com.dary.autosetairplanemode;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import com.spazedog.lib.rootfw3.RootFW;

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
        checkOSVersionandGetRootIfNeed();
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

        if (!isRepeat.isChecked()
                && autoTurnOnAirPlaneModeTime.getOriginalTime() < System.currentTimeMillis()
                && autoTurnOffAirPlaneModeTime.getOriginalTime() < System.currentTimeMillis()) {
            autoTurnOnOffAirPlaneMode.setChecked(false);
        }

        Preference about = findPreference("about");
        about.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference preference) {
                View view = View.inflate(PreferencesActivity.this, R.layout.about, null);
                TextView tv = (TextView) view.findViewById(R.id.text_about);
                tv.setText(getResources().getString(R.string.author)
                        + getResources().getString(R.string.author_value) + "\n"
                        + getResources().getString(R.string.email)
                        + getResources().getString(R.string.email_value) + "\n"
                        + getResources().getString(R.string.version)
                        + Tools.getAppVersionName(PreferencesActivity.this) + "\n"
                        + getResources().getString(R.string.find_more) + "\n"
                        + getResources().getString(R.string.github));
                new AlertDialog.Builder(PreferencesActivity.this).setTitle(R.string.app_name)
                        .setView(view).setPositiveButton(R.string.ok, null)
                        .setIcon(R.drawable.ic_launcher).show();
                return false;
            }
        });
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().equals("airPlaneMode")) {
            Worker worker = new Worker(PreferencesActivity.this);
            worker.setAirPlaneModeMode(newValue.toString());
        } else if (preference.getKey().equals("airPlaneModeOn")) {
            Worker worker = new Worker(PreferencesActivity.this);
            worker.setAirPlaneModeOnOff((Boolean) newValue);
        } else if (preference.getKey().equals("autoTurnOnOffAirPlaneMode")) {
            if ((Boolean) newValue) {
                setAirPlaneMode(this, true, autoTurnOnAirPlaneModeTime.getTime(),
                        isRepeat.isChecked());
                setAirPlaneMode(this, false, autoTurnOffAirPlaneModeTime.getTime(),
                        isRepeat.isChecked());
            } else {
                cancel();
            }
        } else if (preference.getKey().equals("autoTurnOnAirPlaneModeTime")) {
            setAirPlaneMode(this, true, (Long) newValue, isRepeat.isChecked());
        } else if (preference.getKey().equals("autoTurnOffAirPlaneModeTime")) {
            setAirPlaneMode(this, false, (Long) newValue, isRepeat.isChecked());
        } else if (preference.getKey().equals("isRepeat")) {
            if ((Boolean) newValue) {
                setAirPlaneMode(this, true, autoTurnOnAirPlaneModeTime.getTime(), true);
                setAirPlaneMode(this, false, autoTurnOffAirPlaneModeTime.getTime(), true);
            } else {
                setAirPlaneMode(this, true, autoTurnOnAirPlaneModeTime.getTime(), false);
                setAirPlaneMode(this, false, autoTurnOffAirPlaneModeTime.getTime(), false);
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
        // 刚刚装入程序,未设置时间时
        if (time == 0) {
            Tools.makeToast(
                    context,
                    context.getString(R.string.please_select_set_airplane_mode_auto)
                            + (isOn ? context.getString(R.string.on) : context
                                    .getString(R.string.off)) + context.getString(R.string.time));
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

    private void checkOSVersionandGetRootIfNeed() {
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1) {
            RootFW root = new RootFW();
            if (!root.isRoot()) {
                Tools.showDialog(PreferencesActivity.this, "Root Failure");
            } else {
                if (!root.connect()) {
                    Tools.showDialog(PreferencesActivity.this, "Root Failure");
                }
                root.disconnect();
            }
        }
    }

}
