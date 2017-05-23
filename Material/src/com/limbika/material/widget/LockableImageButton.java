package com.limbika.material.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;

public class LockableImageButton extends ImageButton implements Lockable {
	
	private boolean mIsLocked = false;
	
	public LockableImageButton(Context context) {
		super(context);
	}

	public LockableImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public void setLocked(boolean locked) {
		mIsLocked = locked;
		refreshDrawableState();
	}

	@Override
	public int[] onCreateDrawableState(int extraSpace) {
		final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
		if ( isLocked() ) {
			mergeDrawableStates(drawableState, STATE_LOCKED);
		}
		return drawableState;
	}

	@Override
	public boolean isLocked() {
		return mIsLocked;
	}

}
