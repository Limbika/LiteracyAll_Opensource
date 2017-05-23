package com.limbika.material.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.limbika.material.R;

public class LockableImageView extends ImageView implements Lockable {

	private boolean mIsLocked = false;

	public LockableImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public void setLocked(boolean locked) {
		mIsLocked = locked;
    	setImageState(new int[]{((locked)?1:-1)*R.attr.state_locked}, true);
	}

	@Override
	public int[] onCreateDrawableState(int extraSpace) {
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
