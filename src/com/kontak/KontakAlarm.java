package com.kontak;

import java.util.List;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;

/**
 * Author: Amit Assaraf 2015-2017 Israel, Jerusalem | Givon Hahadasha Egoz
 * St. House 6 | All rights to this code are reserved to/for the author
 * i.e. Amit Assaraf. Any publishing of this code without authorization from the
 * author will lead to a law suit. Therfore do not redistribute this
 * file/code. No snippets of this code may be redistributed/published.
 * 
 * Contact information: Amit Assaraf - Email: soit48@gmail.com Phone: 0505964411
 * (Israel)
 *
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;
import android.util.Pair;

public class KontakAlarm extends BroadcastReceiver {
	
	public static final long CHECK_EVERY_X_TIME = 1000 * 60 * 1; 
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("Kontak", "Alarm called...");
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
		wl.acquire();

		// Remove all expired contacts
		Log.d("Kontak", "Removing expired contacts");
		removeAllExpiredContacts(context);
		
		wl.release();
		Log.d("Kontak", "Alarm done...");
		setAlarm(context);
	}

	private void removeAllExpiredContacts(Context context) {
		KontakDB db = new KontakDB(context);
		
		List<Pair<String, String>> lst = db.getAllExpiredContacts();
		for (Pair<String, String> contact : lst) {
			PhoneUtils.removeContact(context, contact.second, contact.first);
			db.deleteContactFromDB(contact.first, contact.second);
		}
				
		db.close();
	}

	@SuppressLint("NewApi")
	public void setAlarm(Context context) {
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, KontakAlarm.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
		     // only for gingerbread and newer versions
			am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + CHECK_EVERY_X_TIME, pi);
		} else {
			am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + CHECK_EVERY_X_TIME, pi);
		}
	}

	public void cancelAlarm(Context context) {
		Intent intent = new Intent(context, KontakAlarm.class);
		PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
	}
}