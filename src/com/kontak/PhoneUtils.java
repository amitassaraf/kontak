package com.kontak;

import java.util.ArrayList;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts.Data;
import android.provider.ContactsContract.PhoneLookup;

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

public class PhoneUtils {

	/**
	 * Method to remove a contact
	 * 
	 * @param context
	 * @param phoneNumber
	 * @param contactName
	 * @return
	 */
	public static boolean removeContact(Context context, String phoneNumber, String contactName) {
		Uri contactUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
		Cursor cur = context.getContentResolver().query(contactUri, null, null, null, null);
		try {
			if (cur.moveToFirst()) {
				do {
					if (cur.getString(cur.getColumnIndex(PhoneLookup.DISPLAY_NAME)).equalsIgnoreCase(contactName)) {
						String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
						Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
						context.getContentResolver().delete(uri, null, null);
						return true;
					}

				} while (cur.moveToNext());
			}

		} catch (Exception e) {
			System.out.println(e.getStackTrace());
		} finally {
			cur.close();
		}
		return false;
	}

	/**
	 * Method to add a contact
	 * 
	 * @param context
	 * @param phoneNumber
	 * @param contactName
	 * @return
	 */
	public static boolean addContact(Context context, String phoneNumber, String contactName) {
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

		ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
				.withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
				.withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());

		// ------------------------------------------------------ Names
		if (contactName != null) {
			ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contactName).build());
		}

		// ------------------------------------------------------ Mobile Number
		if (phoneNumber != null) {
			ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber)
					.withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
							ContactsContract.CommonDataKinds.Phone.TYPE_OTHER)
					.build());
		}

		// Asking the Contact provider to create a new contact
		try {
			context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

}
