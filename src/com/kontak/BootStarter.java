package com.kontak;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Author: Amit Assaraf 2015-2017 Israel, Jerusalem | Givon Hahadasha Egoz St.
 * House 6 | All rights to this code are reserved to/for the author i.e. Amit
 * Assaraf. Any publishing of this code without authorization from the author
 * will lead to a law suit. Therfore do not redistribute this file/code. No
 * snippets of this code may be redistributed/published.
 * 
 * Contact information: Amit Assaraf - Email: soit48@gmail.com Phone: 0505964411
 * (Israel)
 *
 */

public class BootStarter extends BroadcastReceiver {


	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			KontakAlarm alarm = new KontakAlarm();
			alarm.cancelAlarm(context);
			alarm.setAlarm(context);
			Log.d("Kontak", "Setting alarm boot...");
		}
	}
}