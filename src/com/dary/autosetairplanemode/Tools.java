package com.dary.autosetairplanemode;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);
		notification.setLatestEventInfo(context, str, str, contentIntent);
		notificationManager.notify(R.drawable.ic_launcher, notification);
	}

	public static void makeToastSetAirPlaneMode(Context context, boolean isOn, long time, boolean isRepeat) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Toast.makeText(context, "Set AirPlane Mode Auto " + (isRepeat ? "Repeat Everyday " : "") + (isOn ? "On" : "Off") + " At " + formatter.format(calendar.getTime()), Toast.LENGTH_SHORT).show();
	}
	
	public static void makeToast(Context context,String str){
		Toast.makeText(context,str,Toast.LENGTH_SHORT).show();
	}
}
