package com.kontak;

import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.views.Switch;
import com.gc.materialdesign.views.Switch.OnCheckListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

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

public class SettingsActivity extends Activity {

	private SharedPreferences prefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		prefs = this.getSharedPreferences("com.kontak", Context.MODE_PRIVATE);
		
		Switch showPopup = (Switch) findViewById(R.id.settings_switch_show_popup);
		showPopup.setChecked(prefs.getBoolean("show_popup_on_call", true));
		showPopup.setOncheckListener(new OnCheckListener() {
			@Override
			public void onCheck(Switch view, boolean check) {
				prefs.edit().putBoolean("show_popup_on_call", check).apply();
			}
		});
		
		ButtonRectangle btn = (ButtonRectangle) findViewById(R.id.settings_switch_show_intro);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				prefs.edit().putBoolean("skip_intro", false).apply();
				Intent in = new Intent(SettingsActivity.this, LauncherActivity.class);
				SettingsActivity.this.startActivity(in);
			}
		});
		
	}
	
	

}
