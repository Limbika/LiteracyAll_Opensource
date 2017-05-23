package com.limbika.material.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.limbika.material.R;

public class LockableRelativeLayout extends RelativeLayout implements Lockable {
	
	private boolean mIsLocked = false;

	public LockableRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void setLocked(boolean locked) {
    	getBackground().setState(new int[]{((locked)?1:-1)*R.attr.state_locked});
		mIsLocked = locked;
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
