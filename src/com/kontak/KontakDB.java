package com.kontak;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Pair;

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

public class KontakDB {

	private static final String TEXT_TYPE = " TEXT";
	private static final String DATE_TYPE = " LONG";
	private static final String COMMA_SEP = ",";

	private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + ContactEntry.TABLE_NAME + " (" + ContactEntry._ID
			+ " INTEGER PRIMARY KEY," + ContactEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP
			+ ContactEntry.COLUMN_NAME_NUMBER + TEXT_TYPE + COMMA_SEP + ContactEntry.COLUMN_NAME_CREATION_DATE
			+ DATE_TYPE + COMMA_SEP + ContactEntry.COLUMN_NAME_EXPIRE_DATE + DATE_TYPE + " )";

	private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + ContactEntry.TABLE_NAME;

	private KontakDBHelper mDbHelper;

	public KontakDB(Context context) {
		this.mDbHelper = new KontakDBHelper(context);
	}

	public void deleteContactFromDB(String contactName, String phoneNumber) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		db.execSQL("DELETE FROM " + ContactEntry.TABLE_NAME + " WHERE " + ContactEntry.COLUMN_NAME_NAME + "='"
				+ contactName + "' AND " + ContactEntry.COLUMN_NAME_NUMBER + "='" + phoneNumber + "';");
	}

	public boolean addContactToDB(String contactName, String phoneNumber, Date expireDate) {
		// Gets the data repository in write mode
		SQLiteDatabase db = mDbHelper.getWritableDatabase();

		Date today = new Date();

		// Create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		values.put(ContactEntry.COLUMN_NAME_NAME, contactName);
		values.put(ContactEntry.COLUMN_NAME_NUMBER, phoneNumber);
		values.put(ContactEntry.COLUMN_NAME_CREATION_DATE, today.getTime() / 1000);
		values.put(ContactEntry.COLUMN_NAME_EXPIRE_DATE, expireDate.getTime() / 1000);

		// Insert the new row, returning the primary key value of the new row
		long newRowId;
		newRowId = db.insert(ContactEntry.TABLE_NAME, null, values);
		return newRowId != -1;
	}

	public List<Pair<String, String>> getAllExpiredContacts() {
		SQLiteDatabase db = mDbHelper.getReadableDatabase();

		// Define a projection that specifies which columns from the database
		// you will actually use after this query.
		String[] projection = { ContactEntry._ID, ContactEntry.COLUMN_NAME_NAME, ContactEntry.COLUMN_NAME_NUMBER, };

		// Define 'where' part of query.
		String selection = ContactEntry.COLUMN_NAME_EXPIRE_DATE + " <= ?";
		// Specify arguments in placeholder order.
		String[] selectionArgs = { String.valueOf(new Date().getTime() / 1000) };

		Cursor cursor = db.query(ContactEntry.TABLE_NAME, // The table to query
				projection, // The columns to return
				selection, // The columns for the WHERE clause
				selectionArgs, // The values for the WHERE clause
				null, // don't group the rows
				null, // don't filter by row groups
				null // The sort order
		);

		List<Pair<String, String>> contacts = new ArrayList<Pair<String, String>>();

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			String name = cursor.getString(cursor.getColumnIndex(ContactEntry.COLUMN_NAME_NAME));
			String number = cursor.getString(cursor.getColumnIndex(ContactEntry.COLUMN_NAME_NUMBER));
			contacts.add(new Pair<String, String>(name, number));
			cursor.moveToNext();
		}

		return contacts;
	}

	public List<Pair<String, String>> getAllContacts() {
		SQLiteDatabase db = mDbHelper.getReadableDatabase();

		// Define a projection that specifies which columns from the database
		// you will actually use after this query.
		String[] projection = { ContactEntry._ID, ContactEntry.COLUMN_NAME_NAME, ContactEntry.COLUMN_NAME_NUMBER, };

		Cursor cursor = db.query(ContactEntry.TABLE_NAME, // The table to query
				projection, // The columns to return
				null, // The columns for the WHERE clause
				null, // The values for the WHERE clause
				null, // don't group the rows
				null, // don't filter by row groups
				null // The sort order
		);

		List<Pair<String, String>> contacts = new ArrayList<Pair<String, String>>();

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			String name = cursor.getString(cursor.getColumnIndex(ContactEntry.COLUMN_NAME_NAME));
			String number = cursor.getString(cursor.getColumnIndex(ContactEntry.COLUMN_NAME_NUMBER));
			contacts.add(new Pair<String, String>(name, number));
			cursor.moveToNext();
		}

		return contacts;
	}

	/* Inner class that defines the table contents */
	private static abstract class ContactEntry implements BaseColumns {
		public static final String TABLE_NAME = "temp_contacts";
		public static final String COLUMN_NAME_NAME = "name";
		public static final String COLUMN_NAME_NUMBER = "phone";
		public static final String COLUMN_NAME_CREATION_DATE = "create_date";
		public static final String COLUMN_NAME_EXPIRE_DATE = "expire_date";
	}

	private class KontakDBHelper extends SQLiteOpenHelper {
		// If you change the database schema, you must increment the database
		// version.
		public static final int DATABASE_VERSION = 1;
		public static final String DATABASE_NAME = "Kontak.db";

		public KontakDBHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		public void onCreate(SQLiteDatabase db) {
			db.execSQL(SQL_CREATE_ENTRIES);
		}

		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// This database is only a cache for online data, so its upgrade
			// policy is
			// to simply to discard the data and start over
			db.execSQL(SQL_DELETE_ENTRIES);
			onCreate(db);
		}

		public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			onUpgrade(db, oldVersion, newVersion);
		}
	}

	public void close() {
		this.mDbHelper.close();
	}

}
