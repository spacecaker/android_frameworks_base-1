package com.android.systemui.statusbar;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class StatusBarDateAChep extends TextView {

	Calendar mCalendar;
	private final static String format = "MMMMMMMM dd, yyyy";

	public StatusBarDateAChep(Context context) {
		super(context);
		initDate(context);
	}

	public StatusBarDateAChep(Context context, AttributeSet attrs) {
		super(context, attrs);
		initDate(context);
	}

	private void initDate(Context context) {
		if (mCalendar == null) {
			mCalendar = Calendar.getInstance();
		}
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		setText(new SimpleDateFormat(format).format(mCalendar.getTime()));
		invalidate();
	}
}