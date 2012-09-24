package com.android.systemui.statusbar;

import com.android.systemui.R;
import com.android.systemui.R.CmStatusBarView;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class StatusBarJellyButtonsView extends RelativeLayout implements
		View.OnClickListener, View.OnLongClickListener {

	private final Context context;

	private ImageButton mBackButton, mHomeButton, mMenuButton;
	private View mRootView;

	public StatusBarJellyButtonsView(Context context) {
		super(context);
		this.context = context;
		initValues();
	}

	public StatusBarJellyButtonsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initValues();
	}

	private void initValues() {
		mRootView = LayoutInflater.from(context).inflate(R.layout.jelly_test,
				null);

		mBackButton = (ImageButton) mRootView.findViewById(R.id.back);
		mHomeButton = (ImageButton) mRootView.findViewById(R.id.home);
		mMenuButton = (ImageButton) mRootView.findViewById(R.id.menu);

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT, 40);
		layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		addView(mRootView, layoutParams);
		
		mBackButton.setOnLongClickListener(this);
		mHomeButton.setOnLongClickListener(this);
		mBackButton.setOnClickListener(this);
		mHomeButton.setOnClickListener(this);
		mMenuButton.setOnClickListener(this);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}

	@Override
	public boolean onLongClick(View v) {
		if (v == mBackButton) {
            simulateKeypress(CmStatusBarView.KEYCODE_VIRTUAL_BACK_LONG);
			return true;
		} else if (v == mHomeButton) {
            simulateKeypress(CmStatusBarView.KEYCODE_VIRTUAL_HOME_LONG);
			return true;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		if (v == mBackButton) {
			simulateKeypress(KeyEvent.KEYCODE_BACK);
		} else if (v == mHomeButton) {
			Intent setIntent = new Intent(Intent.ACTION_MAIN);
			setIntent.addCategory(Intent.CATEGORY_HOME);
			setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(setIntent);
		} else if (v == mMenuButton) {
			simulateKeypress(KeyEvent.KEYCODE_MENU);
		}
	}
	
	public static void simulateKeypress(final int keyCode) {
		CmStatusBarView.KeyEventInjector.simulateKeypress(keyCode);
    }
}