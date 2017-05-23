package com.limbika.material.widget;

import com.limbika.material.R;

public interface Lockable {
	
	/* package */ static final int[] STATE_LOCKED = {R.attr.state_locked};
	
	public void setLocked(boolean locked);
	public boolean isLocked();
	
}
