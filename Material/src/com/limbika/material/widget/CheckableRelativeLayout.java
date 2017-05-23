package com.limbika.material.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.RelativeLayout;

public class CheckableRelativeLayout extends RelativeLayout implements Checkable {
	
	private static final int[] CHECKED_STATE_SET = { android.R.attr.state_checked };
	private boolean mIsChecked = false;
	
	public CheckableRelativeLayout(Context context) {
		super(context);
	}

	public CheckableRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected int[] onCreateDrawableState(int extraSpace) {
		final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if ( isChecked() ) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
	}
	
	@Override
	public void setChecked(boolean checked) {
		mIsChecked = checked;
		refreshDrawableState();
	}

	@Override
	public boolean isChecked() {
		return mIsChecked;
	}

	@Override
	public void toggle() {
		setChecked( !isChecked() );
	}
}
