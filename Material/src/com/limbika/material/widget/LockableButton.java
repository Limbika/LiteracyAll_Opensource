package com.limbika.material.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import com.limbika.material.R;

public class LockableButton extends Button implements Lockable {

	private boolean mIsLocked = false;

	public LockableButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public void setLocked(boolean locked) {
		mIsLocked = locked;
    	getBackground().setState(new int[]{((locked)?1:-1)*R.attr.state_locked});
	}

	@Override
	protected int[] onCreateDrawableState(int extraSpace) {
		final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
		if (mIsLocked) {
			mergeDrawableStates(drawableState, STATE_LOCKED);
		}
		return drawableState;
	}

	@Override
	public boolean isLocked() {
		return mIsLocked;
	}

}
