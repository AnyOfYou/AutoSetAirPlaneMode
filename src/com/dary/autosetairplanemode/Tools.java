package com.dary.autosetairplanemode;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Vibrator;
import android.widget.Toast;

public class Tools {
	public static void Vibrator(Context context, int time) {
		Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		long[] pattern = { 0, time };
		vibrator.vibrate(pattern, -1);
	}

	public static void makeNotification(Context context, String str) {
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.ic_launcher, str, System.currentTimeMillis());
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		Intent intent = new Intent();
		intent.setClass(context, PreferencesActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);
		notification.setLatestEventInfo(context, str, str, contentIntent);
		notificationManager.notify(R.drawable.ic_launcher, notification);
	}

	public static void makeToastSetAirPlaneMode(Context context, boolean isOn, long time, boolean isRepeat) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss",Locale.getDefault());
		Toast.makeText(context, context.getString(R.string.set_airplane_mode_auto) + (isRepeat ? context.getString(R.string.repeat_everyday) : "") + (isOn ? context.getString(R.string.on) : context.getString(R.string.off)) + context.getString(R.string.at) + formatter.format(calendar.getTime()), Toast.LENGTH_SHORT).show();
	}

	public static void makeToast(Context context, String str) {
		Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
	}

	public static void makeSound(Context context) {
		MediaPlayer mediaPlayer = new MediaPlayer();
		try {
			mediaPlayer.setDataSource(context, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
		try {
			mediaPlayer.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mediaPlayer.start();
		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

			public void onCompletion(MediaPlayer mp) {
				mp.release();
			}
		});
	}

	public static String getAppVersionName(Context context) {
		String versionName = "";
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionName = pi.versionName;
			if (versionName == null || versionName.length() <= 0) {
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return versionName;
	}
}
