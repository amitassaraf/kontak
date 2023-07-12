package com.kontak;

import java.util.Arrays;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.PhoneNumberUtils;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
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

public class IncomingCall extends BroadcastReceiver {

	private SharedPreferences prefs;

	public void onReceive(Context context, Intent intent) {

		try {

			prefs = context.getSharedPreferences("com.kontak", Context.MODE_PRIVATE);

			// TELEPHONY MANAGER class object to register one listner
			TelephonyManager tmgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

			// Create Listner
			MyPhoneStateListener PhoneListener = new MyPhoneStateListener(context);

			// Register listener for LISTEN_CALL_STATE
			tmgr.listen(PhoneListener, PhoneStateListener.LISTEN_CALL_STATE);

		} catch (Exception e) {
			Log.e("Phone Receive Error", " " + e);
		}

	}

	private class MyPhoneStateListener extends PhoneStateListener {

		private Context context;
		private static final int CALL_ENDED_STATE = 0;

		public MyPhoneStateListener(Context context) {
			this.context = context;
		}

		@SuppressWarnings("deprecation")
		public void onCallStateChanged(int state, String incomingNumber) {

			incomingNumber = incomingNumber.trim();

			if (prefs == null)
				prefs = context.getSharedPreferences("com.kontak", Context.MODE_PRIVATE);

			Log.d("Kontak", "Phone state changed " + String.valueOf(state));

			if (state == CALL_ENDED_STATE && !incomingNumber.equals("")) {

				Log.d("Kontak", "Phone call ended, checking number..");
				if (!contactExists(incomingNumber)) {
					// Launch the popup

					if (prefs.getBoolean("show_popup_on_call", true)) {
						Log.d("Kontak", "Unknown number found, launching popup");
						Log.d("Kontak", "Unknown number found (" + incomingNumber + ")");

						Intent in = new Intent(context, PopupActivity.class);
						in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						in.putExtra("number", incomingNumber);
						in.putExtra("name", "");
						context.startActivity(in);
					}
				}
			}
		}

		public boolean contactExists(String number) {
			Uri lookupUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
			String[] mPhoneNumberProjection = { PhoneLookup._ID, PhoneLookup.NUMBER, PhoneLookup.DISPLAY_NAME };
			Cursor cur = context.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
			try {
				if (cur.moveToFirst()) {
					return true;
				}
			} finally {
				if (cur != null)
					cur.close();
			}
			return false;
		}
	}
}
