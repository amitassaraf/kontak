package com.kontak;

import com.gc.materialdesign.views.ButtonRectangle;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LauncherActivity extends AppCompatActivity {

	private static final String SAVING_STATE_SLIDER_ANIMATION = "SliderAnimationSavingState";
	private boolean isSliderAnimation = false;
	private static final String SKIP_INTRO = "skip_intro"; 
	private SharedPreferences prefs;

	@SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		KontakAlarm alarm = new KontakAlarm();
		alarm.cancelAlarm(this);
		alarm.setAlarm(this);
		
		prefs = this.getSharedPreferences("com.kontak", Context.MODE_PRIVATE);

		if (prefs.getBoolean(SKIP_INTRO, false)) {
			Intent in = new Intent(LauncherActivity.this, MainActivity.class);
			startActivity(in);
			finish();
		}
		
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.KITKAT){
			Window window = getWindow();
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
		
		setContentView(R.layout.activity_launcher);

		final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

		final ViewPagerAdapter adapter = new ViewPagerAdapter(R.array.icons, R.array.titles, R.array.hints);
		viewPager.setAdapter(adapter);

		CirclePageIndicator mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
		mIndicator.setViewPager(viewPager);

		viewPager.setPageTransformer(true, new CustomPageTransformer());

		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(final int position, float positionOffset, int positionOffsetPixels) {

				View landingBGView = findViewById(R.id.landing_backgrond);
				int colorBg[] = getResources().getIntArray(R.array.landing_bg);

				ColorShades shades = new ColorShades();
				shades.setFromColor(colorBg[position % colorBg.length])
						.setToColor(colorBg[(position + 1) % colorBg.length]).setShade(positionOffset);

				landingBGView.setBackgroundColor(shades.generate());

			}

			public void onPageSelected(int position) {
			}

			public void onPageScrollStateChanged(int state) {
			}
		});

	}

	public class ViewPagerAdapter extends PagerAdapter {

		private int iconResId, titleArrayResId, hintArrayResId;

		public ViewPagerAdapter(int iconResId, int titleArrayResId, int hintArrayResId) {

			this.iconResId = iconResId;
			this.titleArrayResId = titleArrayResId;
			this.hintArrayResId = hintArrayResId;
		}

		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public int getCount() {
			return getResources().getIntArray(iconResId).length;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {

			Drawable icon = getResources().obtainTypedArray(iconResId).getDrawable(position);
			String title = getResources().getStringArray(titleArrayResId)[position];
			String hint = getResources().getStringArray(hintArrayResId)[position];

			View itemView = null;
			
			if (position == 4)
				itemView = getLayoutInflater().inflate(R.layout.intro_page_end, container, false);
			else if (position == 2)
				itemView = getLayoutInflater().inflate(R.layout.intro_page_special, container, false);	
			else
				itemView = getLayoutInflater().inflate(R.layout.intro_page, container, false);
			
			ImageView iconView = (ImageView) itemView.findViewById(R.id.landing_img_slide);
			TextView titleView = (TextView) itemView.findViewById(R.id.landing_txt_title);
			TextView hintView = (TextView) itemView.findViewById(R.id.landing_txt_hint);
			
			if (position == 4) {
				ButtonRectangle rectView = (ButtonRectangle) itemView.findViewById(R.id.start_using);
				rectView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						prefs.edit().putBoolean(SKIP_INTRO, true).apply();
						Intent in = new Intent(LauncherActivity.this, MainActivity.class);
						startActivity(in);
						finish();
					}
				});
			}
			
			iconView.setImageDrawable(icon);
			titleView.setText(title);
			hintView.setText(hint);

			container.addView(itemView);

			return itemView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((RelativeLayout) object);

		}
	}

	public class CustomPageTransformer implements ViewPager.PageTransformer {

		public void transformPage(View view, float position) {
			int pageWidth = view.getWidth();

			View imageView = view.findViewById(R.id.landing_img_slide);
			View contentView = view.findViewById(R.id.landing_txt_hint);
			View txt_title = view.findViewById(R.id.landing_txt_title);
			View button = view.findViewById(R.id.start_using);

			if (position < -1) { // [-Infinity,-1)
				// This page is way off-screen to the left
			} else if (position <= 0) { // [-1,0]
				// This page is moving out to the left

				// Counteract the default swipe
				setTranslationX(view, pageWidth * -position);
				if (contentView != null) {
					// But swipe the contentView
					setTranslationX(contentView, pageWidth * position);
					setTranslationX(txt_title, pageWidth * position);
					
					if (button != null)
						setTranslationX(button, pageWidth * position); 

					setAlpha(contentView, 1 + position);
					setAlpha(txt_title, 1 + position);
					
					if (button != null)
						setAlpha(button, 1 + position);
				}

				if (imageView != null) {
					// Fade the image in
					setAlpha(imageView, 1 + position);
				}

			} else if (position <= 1) { // (0,1]
				// This page is moving in from the right

				// Counteract the default swipe
				setTranslationX(view, pageWidth * -position);
				if (contentView != null) {
					// But swipe the contentView
					setTranslationX(contentView, pageWidth * position);
					setTranslationX(txt_title, pageWidth * position);
					
					if (button != null)
						setTranslationX(button, pageWidth * position);

					setAlpha(contentView, 1 - position);
					setAlpha(txt_title, 1 - position);
					
					if (button != null)
						setAlpha(button, 1 - position);

				}
				if (imageView != null) {
					// Fade the image out
					setAlpha(imageView, 1 - position);
				}

			}
		}
	}

	/**
	 * Sets the alpha for the view. The alpha will be applied only if the
	 * running android device OS is greater than honeycomb.
	 * 
	 * @param view
	 *            - view to which alpha to be applied.
	 * @param alpha
	 *            - alpha value.
	 */
	private void setAlpha(View view, float alpha) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && !isSliderAnimation) {
			view.setAlpha(alpha);
		}
	}

	/**
	 * Sets the translationX for the view. The translation value will be applied
	 * only if the running android device OS is greater than honeycomb.
	 * 
	 * @param view
	 *            - view to which alpha to be applied.
	 * @param translationX
	 *            - translationX value.
	 */
	private void setTranslationX(View view, float translationX) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && !isSliderAnimation) {
			view.setTranslationX(translationX);
		}
	}

	public void onSaveInstanceState(Bundle outstate) {

		if (outstate != null) {
			outstate.putBoolean(SAVING_STATE_SLIDER_ANIMATION, isSliderAnimation);
		}

		super.onSaveInstanceState(outstate);
	}

	public void onRestoreInstanceState(Bundle inState) {

		if (inState != null) {
			isSliderAnimation = inState.getBoolean(SAVING_STATE_SLIDER_ANIMATION, false);
		}
		super.onRestoreInstanceState(inState);

	}
}
