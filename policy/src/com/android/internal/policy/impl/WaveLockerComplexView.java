/*
 * Copyright (C) 2012-2013 AChep@xda <artemchep@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.internal.policy.impl;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.android.internal.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.graphics.Typeface;
import android.os.Handler;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WaveLockerComplexView extends RelativeLayout implements KeyguardScreen {

	public WaveLockerComplexView(Context context,
			KeyguardScreenCallback callback) {
		super(context);

		DigitalClock dc = new DigitalClock(context);
		LayoutParams dcLayoutParams = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		dcLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

		DateView dv = new DateView(context);
		LayoutParams dvLayoutParams = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		dvLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		dvLayoutParams.addRule(RelativeLayout.BELOW, dc.getId());

		WaveView wv = new WaveView(context, callback);

		addView(dc, dcLayoutParams);
		addView(dv, dvLayoutParams);
		addView(wv);

		// Set layout parameters
		setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));

        	setFocusable(true);
        	setFocusableInTouchMode(true);
        	setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
	}

   	public boolean needsInput(){
       		return false;
	}

	public void onPause() {			
   	}

    	public void onResume() {
    	}

    	public void cleanUp() {
    	}

	private class WaveView extends WaveViewBase implements
			WaveViewBase.OnActionListener {

		private KeyguardScreenCallback mCallback;

		public WaveView(Context context, KeyguardScreenCallback callback) {
			super(context);
			mCallback = callback;

			setOnActionListener(this);
		}

		@Override
		public void onAction() {
			if (mCallback == null)
				return;
			mCallback.goToUnlockScreen();
		}

		@Override
		public void onTouchDown(float x, float y) {
		}

		@Override
		public void onTouchUp() {
		}
	}

	private class DigitalClock extends LinearLayout {

		private final static String HOURS_24 = "kk";
		private final static String HOURS = "h";
		private final static String MINUTES = ":mm";

		private Calendar mCalendar;
		private String mHoursFormat;
		private TextView mTimeDisplayHours, mTimeDisplayMinutes;
		private AmPm mAmPm;
		private ContentObserver mFormatChangeObserver;
		private boolean mAttached;

		/* called by system on minute ticks */
		private final Handler mHandler = new Handler();
		private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)) {
					mCalendar = Calendar.getInstance();
				}
				// Post a runnable to avoid blocking the broadcast.
				mHandler.post(new Runnable() {
					public void run() {
						updateTime();
					}
				});
			}
		};

		public DigitalClock(Context context) {
			this(context, null);
		}

		public DigitalClock(Context context, AttributeSet attrs) {
			super(context, attrs);

			mTimeDisplayHours = new TextView(context);
			mTimeDisplayHours.setTextAppearance(context,
					R.style.TextAppearance_RingLockScreen_Clock_Hours);
			mTimeDisplayMinutes = new TextView(context);
			mTimeDisplayMinutes.setTextAppearance(context,
					R.style.TextAppearance_RingLockScreen_Clock_Minutes);
			// TODO:  
			//mTimeDisplayMinutes.setTypeface(Typeface.createFromAsset(
			//		context.getAssets(), "fonts/Roboto-Thin.ttf"));
			mAmPm = new AmPm(context);
			mCalendar = Calendar.getInstance();

			this.setBaselineAligned(false);
			this.setGravity(Gravity.TOP);
			this.setPadding((int) (mAmPm.mAmPm.getTextSize() * 1.8f), 0, 0, 0);

			this.addView(mTimeDisplayHours);
			this.addView(mTimeDisplayMinutes);
			this.addView(mAmPm.mAmPm);

			setDateFormat();
		}

		@Override
		protected void onAttachedToWindow() {
			super.onAttachedToWindow();

			if (mAttached)
				return;
			mAttached = true;

			/* monitor time ticks, time changed, timezone */
			IntentFilter filter = new IntentFilter();
			filter.addAction(Intent.ACTION_TIME_TICK);
			filter.addAction(Intent.ACTION_TIME_CHANGED);
			filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
			getContext().registerReceiver(mIntentReceiver, filter);

			/* monitor 12/24-hour display preference */
			mFormatChangeObserver = new FormatChangeObserver();
			getContext().getContentResolver().registerContentObserver(
					Settings.System.CONTENT_URI, true, mFormatChangeObserver);

			updateTime();
		}

		@Override
		protected void onDetachedFromWindow() {
			super.onDetachedFromWindow();

			if (!mAttached)
				return;
			mAttached = false;

			getContext().unregisterReceiver(mIntentReceiver);
			getContext().getContentResolver().unregisterContentObserver(
					mFormatChangeObserver);
		}

		private void updateTime() {
			mCalendar.setTimeInMillis(System.currentTimeMillis());

			StringBuilder fullTimeStr = new StringBuilder();
			CharSequence newTime = DateFormat.format(mHoursFormat, mCalendar);
			mTimeDisplayHours.setText(newTime);
			fullTimeStr.append(newTime);
			newTime = DateFormat.format(MINUTES, mCalendar);
			fullTimeStr.append(newTime);
			mTimeDisplayMinutes.setText(newTime);

			boolean isMorning = mCalendar.get(Calendar.AM_PM) == 0;
			mAmPm.setIsMorning(isMorning);
			if (!get24HourMode()) {
				fullTimeStr.append(mAmPm.getAmPmText());
			}

			// Update accessibility string.
			setContentDescription(fullTimeStr);
		}

		private void setDateFormat() {
			boolean hour24Mode = get24HourMode();

			mHoursFormat = hour24Mode ? HOURS_24 : HOURS;
			mAmPm.setShowAmPm(!hour24Mode);
		}

		private boolean get24HourMode() {
			return DateFormat.is24HourFormat(getContext());
		}

		private class AmPm {
			private final TextView mAmPm;
			private final String mAmString, mPmString;

			AmPm(Context context) {
				mAmPm = new TextView(context);
				mAmPm.setTextAppearance(context,
						R.style.TextAppearance_RingLockScreen_Clock_AmPm);

				String[] ampm = new DateFormatSymbols().getAmPmStrings();
				mAmString = ampm[0];
				mPmString = ampm[1];
			}

			void setShowAmPm(boolean show) {
				mAmPm.setVisibility(show ? View.VISIBLE : View.GONE);
			}

			void setIsMorning(boolean isMorning) {
				mAmPm.setText((isMorning ? mAmString : mPmString).toUpperCase());
			}

			CharSequence getAmPmText() {
				return mAmPm.getText();
			}
		}

		private class FormatChangeObserver extends ContentObserver {
			public FormatChangeObserver() {
				super(new Handler());
			}

			@Override
			public void onChange(boolean selfChange) {
				setDateFormat();
				updateTime();
			}
		}
	}

	private class DateView extends TextView {

		private static final String FORMAT = "EEE, MMMM d";

		public DateView(Context context) {
			super(context);
			setTextAppearance(context,
					R.style.TextAppearance_RingLockScreen_Date);
		}

		@Override
		protected void onAttachedToWindow() {
			super.onAttachedToWindow();

			Calendar calendar = Calendar.getInstance();
			setText(new SimpleDateFormat(FORMAT).format(calendar.getTime())
					.toUpperCase());
		}
	}
}
