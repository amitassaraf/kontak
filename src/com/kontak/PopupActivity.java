package com.kontak;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.angmarch.views.NiceSpinner;

import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.views.Slider;
import com.gc.materialdesign.views.Slider.OnValueChangedListener;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PopupActivity extends Activity {

	private static Random rand = new Random();

	List<Pair<String, Integer>> time_options;
	List<Pair<Integer, Integer>> backgrounds;
	EditText edt = null;

	int[] background_resources = { R.drawable.background1, R.drawable.background2, R.drawable.background3,
			R.drawable.background4, R.drawable.background5 };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_popup);

		final HashMap<String, Integer> time_values = new HashMap<String, Integer>();
		time_values.put("Minutes", 60);
		time_values.put("Hours", 60 * 60);
		time_values.put("Days", 60 * 60 * 24);
		time_values.put("Weeks", 60 * 60 * 24 * 7);
		time_values.put("Months", 60 * 60 * 24 * 7 * 4);
		time_values.put("Years", 60 * 60 * 24 * 7 * 4 * 12);

		Intent intent = getIntent();
		final String number = intent.getStringExtra("number") == null ? "050-000-0000"
				: intent.getStringExtra("number");
		String name = intent.getStringExtra("name") == null ? "Unknown" : intent.getStringExtra("name");
		final boolean editableNumber = intent.getBooleanExtra("editNum", false);

		Log.d("Kontak", "Launching popup with " + number + name);

		time_options = new ArrayList<Pair<String, Integer>>();
		time_options.add(new Pair<String, Integer>("Minutes", 60));
		time_options.add(new Pair<String, Integer>("Hours", 24));
		time_options.add(new Pair<String, Integer>("Days", 7));
		time_options.add(new Pair<String, Integer>("Weeks", 4));
		time_options.add(new Pair<String, Integer>("Months", 12));
		time_options.add(new Pair<String, Integer>("Years", 5));

		LinearLayout background = (LinearLayout) findViewById(R.id.popup_background);
		int selectedResource = background_resources[rand.nextInt(background_resources.length)];
		background.setBackgroundResource(selectedResource);

		final ButtonRectangle btn = (ButtonRectangle) findViewById(R.id.popup_add_temp_contact);

		if (editableNumber) {
			edt = (EditText) findViewById(R.id.popup_number_edt);
			edt.setVisibility(View.VISIBLE);
			TextView txt = (TextView) findViewById(R.id.popup_number);
			txt.setVisibility(View.GONE);
			edt.setText(PhoneNumberUtils.formatNumber(number));
		} else {
			TextView txt = (TextView) findViewById(R.id.popup_number);
			txt.setText(PhoneNumberUtils.formatNumber(number));
		}

		final EditText editText = (EditText) findViewById(R.id.popup_name);
		editText.setText(name);

		Bitmap bg = BitmapFactory.decodeResource(this.getResources(), selectedResource);
		long redBucket = 0;
		long greenBucket = 0;
		long blueBucket = 0;

		int[] pixels = new int[bg.getHeight() * bg.getWidth()];
		bg.getPixels(pixels, 0, bg.getWidth(), 0, 0, bg.getWidth(), bg.getHeight());
		for (int y = 0; y < pixels.length; y++) {
			redBucket += Color.red(pixels[y]);
			greenBucket += Color.green(pixels[y]);
			blueBucket += Color.blue(pixels[y]);
		}

		final int averageColor = Color.rgb((int) redBucket / pixels.length, (int) greenBucket / pixels.length,
				(int) blueBucket / pixels.length);
		btn.setBackgroundColor(averageColor);

		ImageView exit = (ImageView) findViewById(R.id.popup_exit_btn);
		exit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				if (!editableNumber)
					System.exit(0);
			}
		});

		final TextView sliderValue = (TextView) findViewById(R.id.popup_slider_value);
		final Slider slider = (Slider) findViewById(R.id.popup_slider);
		slider.setBackgroundColor(averageColor);
		slider.setOnValueChangedListener(new OnValueChangedListener() {
			@Override
			public void onValueChanged(int value) {
				sliderValue.setText(String.valueOf(slider.getValue()));
			}
		});

		final NiceSpinner niceSpinner = (NiceSpinner) findViewById(R.id.popup_dropdown);
		ArrayList<String> options = new ArrayList<String>();
		for (Pair<String, Integer> pair : time_options) {
			options.add(pair.first);
		}
		niceSpinner.attachDataSource(options);
		niceSpinner.setArrowTint(averageColor);

		niceSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				slider.setMax(time_options.get(position).second);
				slider.setValue(0);
				sliderValue.setText(String.valueOf(slider.getValue()));
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("Kontak", "Adding!");

				btn.setEnabled(false);

				Date today = new Date();
				String option = time_options.get(niceSpinner.getSelectedIndex()).first;
				int value = slider.getValue();

				Date expirationDate = new Date(today.getTime() + (time_values.get(option) * value * 1000));

				KontakDB db = new KontakDB(PopupActivity.this);

				String contact = editText.getText().toString() == "" ? "Temporary Unknown"
						: editText.getText().toString();

				String num = null;
				if (editableNumber)
					num = edt.getText().toString();
				else
					num = number;

				if (!num.matches("^[0-9-+#*]*$") || contact.length() <= 0) {
					btn.setText("Bad number or name :(");
					btn.setBackgroundColor(Color.rgb(231, 76, 60));

					new Thread() {
						public void run() {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							
							PopupActivity.this.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									btn.setText("Add contact");
									btn.setBackgroundColor(averageColor);
									btn.setEnabled(true);
								}
							});
						}
					}.start();
				} else {

					num = PhoneNumberUtils.formatNumber(num);

					Log.d("Kontak", "Saving contacts as " + contact + " with number " + num + " at "
							+ expirationDate.toString());
					if (!db.addContactToDB(contact, num, expirationDate))
						Log.d("Kontak", "Failed to add to DB");

					if (!PhoneUtils.addContact(PopupActivity.this, num, contact))
						Log.d("Kontak", "Failed to add to phone..");

					db.close();

					btn.setText("Success!");
					btn.setBackgroundColor(Color.rgb(39, 174, 96));

					new Thread() {
						public void run() {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							PopupActivity.this.finish();
							if (!editableNumber)
								System.exit(0);
						}
					}.start();
				}
			}
		});

		if (!editableNumber) {
			LinearLayout callBtn = (LinearLayout) findViewById(R.id.popup_call_btn);
			callBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(Intent.ACTION_DIAL);
					intent.setData(Uri.parse("tel:" + number));
					startActivity(intent);
					PopupActivity.this.finish();
					System.exit(0);
				}
			});
		}

	}
}
