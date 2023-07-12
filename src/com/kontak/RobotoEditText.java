package com.kontak;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

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

public class RobotoEditText extends EditText {

	public RobotoEditText(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public RobotoEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RobotoEditText(Context context) {
		super(context);
		init();
	}
	
	public void init() {
		if (RobotoTextView.typeFace == null)
			RobotoTextView.typeFace = Typeface.createFromAsset(getContext().getAssets(), "fonts/roboto.ttf");
		setTypeface(RobotoTextView.typeFace);
	}
	

}
