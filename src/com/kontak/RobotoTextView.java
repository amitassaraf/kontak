package com.kontak;

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

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class RobotoTextView extends TextView {
	
	public static Typeface typeFace;

	public RobotoTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}

	public RobotoTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);

	}

	public RobotoTextView(Context context) {
		super(context);
		init(null);
	}

	private void init(AttributeSet attrs) {
		if (typeFace == null)
			typeFace = Typeface.createFromAsset(getContext().getAssets(), "fonts/roboto.ttf");
		setTypeface(typeFace);
	}

}