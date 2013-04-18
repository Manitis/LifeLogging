package com.example.habit1;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import com.example.habit1.MainActivity.PreferenceContainer;

public class AlarmReceiver extends BroadcastReceiver {

	private Context context;
	private SharedPreferences settings;
	private boolean notificationIsEnabled;
	private Uri notif_sound;
	private int unfinishedCount;
	private double xpGainIfFinished, hpLossIfUnfinished;
	PreferenceContainer preferences;

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		settings = PreferenceManager.getDefaultSharedPreferences(context);
		getNotificationSettings();
		if (notificationIsEnabled) {
			getNotificationValues();
			showNotification();
		}
		//
	}

	private void getNotificationValues() {
		unfinishedCount = settings.getInt("unfinishedCount", 0);
		xpGainIfFinished = settings.getFloat("xpGainIfFinished", 0);
		hpLossIfUnfinished = settings.getFloat("hpLossIfUnfinished", 0);
	}

	private void getNotificationSettings() {
		notificationIsEnabled = settings.getBoolean("notificationsEnabled",
				true);
		getNotificationSound();
	}

	private void getNotificationSound() {
		final String savedUri = settings.getString("notificationSound", "");
		if (savedUri.length() > 0) {
			// If the stored string is the bogus string...
			if (savedUri.equals("defaultRingtone")) {
				notif_sound = Settings.System.DEFAULT_ALARM_ALERT_URI;
				final SharedPreferences.Editor saveEditor = settings.edit();
				saveEditor.putString("notificationSound",
						notif_sound.toString());
				saveEditor.commit();
			} else {
				notif_sound = Uri.parse(savedUri);
			}
		}
	}

	public void showNotification() {
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				new Intent(context, MainActivity.class), 0);
		int numberOfUnfinished = unfinishedCount;
		NotificationCompat.Builder notification = new NotificationCompat.Builder(
				context)
				.setSmallIcon(R.drawable.character)
				.setContentTitle(
						String.format("You have %d unfinished items due today",
								numberOfUnfinished))
				.setTicker(
						String.format("You have %d unfinished items due today",
								numberOfUnfinished))
				.setContentText(
						String.format(
								"You can gain %6.2f XP by doing them. You will lose %6.2f HP by not finishing them.",
								xpGainIfFinished, hpLossIfUnfinished))
				.setContentIntent(contentIntent).setSound(notif_sound)
				.setAutoCancel(true);
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(1, notification.build());

	}
}
