package com.dary.autosetairplanemode;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.graphics.YuvImage;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

public class TimePreference extends DialogPreference {
	private TimePicker mPicker = null;

	public TimePreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		setDialogLayoutResource(R.layout.time_preference);
	}

	@Override
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);

		mPicker = (TimePicker) view.findViewById(R.id.timePicker_preference);
		mPicker.setIs24HourView(true);
		Date d = new Date(getSharedPreferences().getLong(getKey(), System.currentTimeMillis()));
		mPicker.setCurrentHour(d.getHours());
		mPicker.setCurrentMinute(d.getMinutes());
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		if (positiveResult) {
			// 很重要.否则用户直接输入的数值会获取不到
			mPicker.clearFocus();
			long value = stringHourMinTimetoLongMillis(mPicker.getCurrentHour(), mPicker.getCurrentMinute());
			if (callChangeListener(value)) {
				getSharedPreferences().edit().putLong(getKey(), value).commit();
			}
		}
	}

	private long stringHourMinTimetoLongMillis(int h, int m) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(java.lang.System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, h);
		calendar.set(Calendar.MINUTE, m);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
			calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 1);
		}
		return calendar.getTimeInMillis();
	}

	public long getTime() {
		Date d = new Date(getSharedPreferences().getLong(getKey(), (long) 0));

		if (d.getTime() < System.currentTimeMillis()) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(java.lang.System.currentTimeMillis());
			calendar.set(Calendar.HOUR_OF_DAY, d.getHours());
			calendar.set(Calendar.MINUTE, d.getMinutes());
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 1);
			return calendar.getTimeInMillis();
		} else {
			return d.getTime();
		}
	}
}
