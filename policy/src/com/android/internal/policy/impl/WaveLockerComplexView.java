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

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WaveLockerComplexView extends RelativeLayout implements KeyguardScreen  {

	private KeyguardScreenCallback mCallback;

	private LinearLayout mContentLayout;

	private LinearLayout mClockRootView;
	private TextView mClockMinutesView;
	private TextView mClockHoursView;
	private TextView mClockAmPmView;

	private TextView mDateView;

	public WaveLockerComplexView(Context context,
			KeyguardScreenCallback callback) {
		super(context);
		mCallback = callback;

		// Inflate and add content to view
		final LayoutInflater inflater = LayoutInflater.from(context);
		mContentLayout = (LinearLayout) inflater.inflate(
				R.layout.keyguard_ring_locker, null, true);
		addView(mContentLayout, new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		mClockRootView = (LinearLayout) mContentLayout
				.findViewById(R.id.clock_root);
		mClockMinutesView = (TextView) mContentLayout
				.findViewById(R.id.clock_minutes);
		mClockHoursView = (TextView) mContentLayout
				.findViewById(R.id.clock_hours);
		mClockAmPmView = (TextView) mContentLayout
				.findViewById(R.id.clock_ampm);

		mDateView = (TextView) mContentLayout.findViewById(R.id.date);

		// Add wave control
		WaveViewBase waveViewBase = new WaveViewBase(context, new WaveViewBase.OnActionListener(){

			@Override
			public void onAction() {
				if (mCallback != null) {
					mCallback.goToUnlockScreen();
				}
			}

			@Override
			public void onTouchDown(float x, float y) {
			}

			@Override
			public void onTouchUp() {
			}

		});
		addView(waveViewBase);


		// Set lockscreen props
		setFocusable(true);
		setFocusableInTouchMode(true);
		setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

		// Setup branding if we want to do it
		setupBranding();
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

	private void setupBranding() {
		TextView brandingLeft = new TextView(getContext());
		brandingLeft.setText("GingerDX "
			+ android.os.SystemProperties.get("ro.gdx.version.id"));
		brandingLeft.setTextSize(12);
		TextView brandingRight = new TextView(getContext());
		brandingRight.setText("TeamFun");
		brandingRight.setTextSize(12);

		LayoutParams brandingLeftLp = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		brandingLeftLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

		LayoutParams brandingRightLp = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		brandingRightLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		brandingRightLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

		addView(brandingLeft, brandingLeftLp);
		addView(brandingRight, brandingRightLp);
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		Calendar calendar = Calendar.getInstance();

		boolean hour24Mode = get24HourMode();
		int mins = calendar.get(Calendar.MINUTE);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);

		CharSequence hours = "" + (hour > 12 && !hour24Mode ? hour - 12 : hour);
		CharSequence minutes = ":" + (mins > 9 ? mins : "0" + mins);
		CharSequence ampm = !hour24Mode ? getAmPmText(calendar
				.get(Calendar.AM_PM)) : null;

		setClockContent(hours, minutes, ampm);

		// Update date
		mDateView.setText(new SimpleDateFormat("EEE, MMMM d").format(
				calendar.getTime()).toUpperCase());
	}

	private void setClockContent(CharSequence h, CharSequence m,
			CharSequence ampm) {
		mClockHoursView.setText(h);
		mClockMinutesView.setText(m);

		if (ampm != null) {
			if (!mClockAmPmView.getText().equals(ampm)) {
				setClockAmPm((int) (mClockAmPmView.getTextSize() * 1.7f),
						VISIBLE, ampm);
			}
		} else {
			if (mClockAmPmView.getVisibility() != GONE) {
				setClockAmPm(0, GONE, "");
			}
		}
	}

	private void setClockAmPm(int paddingLeft, int visibility, CharSequence ampm) {
		mClockRootView.setPadding(paddingLeft, 0, 0, 0);
		mClockAmPmView.setVisibility(visibility);
		mClockAmPmView.setText(ampm);
	}

	private String getAmPmText(int i) {
		return new DateFormatSymbols().getAmPmStrings()[i];
	}

	private boolean get24HourMode() {
		return DateFormat.is24HourFormat(getContext());
	}
}
