package com.kontak;

import java.util.List;

import com.gc.materialdesign.views.ButtonFloat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	}

	@Override
	protected void onResume() {
		
		Log.d("Kontak", "ON RESUME!!");
		
		KontakDB db = new KontakDB(this);

		List<Pair<String, String>> data = db.getAllContacts();

		final ListView lstView = (ListView) findViewById(R.id.main_listview);

		if (data.size() > 0) {
			ListAdapter adapter = new ListAdapter(this, R.layout.contact_item, data);
			lstView.setAdapter(adapter);
			lstView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					@SuppressWarnings("unchecked")
					Pair<String, String> contact = (Pair<String, String>) lstView.getItemAtPosition(position);
					Intent intent = new Intent(Intent.ACTION_DIAL);
					intent.setData(Uri.parse("tel:" + contact.second));
					startActivity(intent);
				}
			});
			TextView tx = (TextView) findViewById(R.id.no_contacts);
			tx.setVisibility(View.GONE);
			lstView.setVisibility(View.VISIBLE);
			adapter.notifyDataSetChanged();
		} else {
			lstView.setVisibility(View.GONE);
			TextView tx = (TextView) findViewById(R.id.no_contacts);
			tx.setVisibility(View.VISIBLE);
		}

		ButtonFloat add = (ButtonFloat) findViewById(R.id.new_contact_btn);
		add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent in = new Intent(MainActivity.this, PopupActivity.class);
				in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				in.putExtra("number", "");
				in.putExtra("name", "");
				in.putExtra("editNum", true);
				MainActivity.this.startActivity(in);
			}
		});
		
		ImageView icon = (ImageView) findViewById(R.id.main_icon);
		icon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences prefs = MainActivity.this.getSharedPreferences("com.kontak", Context.MODE_PRIVATE);
				prefs.edit().putBoolean("skip_intro", false).apply();
				Intent in = new Intent(MainActivity.this, LauncherActivity.class);
				MainActivity.this.startActivity(in);
			}
		});
		
		ImageView settings = (ImageView) findViewById(R.id.main_settings_btn);
		settings.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent in = new Intent(MainActivity.this, SettingsActivity.class);
				MainActivity.this.startActivity(in);
			}
		});

		db.close();
		
		lstView.invalidate();
		
		super.onResume();
	}

	public int dp2px(int dip) {
		float scale = this.getResources().getDisplayMetrics().density;
		return (int) (dip * scale + 0.5f);
	}

	public class ListAdapter extends ArrayAdapter<Pair<String, String>> {

		public ListAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
		}

		public ListAdapter(Context context, int resource, List<Pair<String, String>> items) {
			super(context, resource, items);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View v = convertView;

			if (v == null) {
				LayoutInflater vi;
				vi = LayoutInflater.from(getContext());
				v = vi.inflate(R.layout.contact_item, null);
			}

			Pair<String, String> p = getItem(position);

			if (p != null) {
				TextView name = (TextView) v.findViewById(R.id.contact_name);
				TextView number = (TextView) v.findViewById(R.id.contact_number);

				name.setText(p.first);
				number.setText(p.second);
			}

			return v;
		}

	}
}
