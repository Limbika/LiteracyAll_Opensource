package com.limbika.material.widget;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * This class can only have two child and always switch this views. Is not wheel and haven't got a flipGestureListener.
 */
public class SimpleAnimatedFlipper extends AnimatedFlipper {
	
	private ArrayList<Integer> mPositions = new ArrayList<Integer>();
	
	public SimpleAnimatedFlipper(Context context) {
		this(context, null);
	}
	
	public SimpleAnimatedFlipper (Context context, AttributeSet attrs) {
		super (context, attrs);
		setCanFlipOnGesture(false);
		setWheel(false);
	}

	/**
	 * Switch between the two childs.
	 */
	public void switchViews() {
		int displayedChild = getDisplayedChild();
		
		if (displayedChild == 0){
			showNext();
		}
		else if (displayedChild == 1) {
			showPrevious();
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		// Round views
		final int HEIGHT = getMeasuredHeight();
		for ( int index : mPositions ) {
			roundViews(index, HEIGHT);
		}
	}
	
	private void roundViews(int index, int height) {
		ViewGroup parent = (ViewGroup) getChildAt(index);
		int count = parent.getChildCount();
		for ( int i=0;i<count;i++ ) {
			View view = parent.getChildAt(i);
			int size = view.getMeasuredWidth();
			
			if ( size > height && view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams ) {
				// Only for ViewGroups with MarginLayoutParams
				// like LinearLayout or RelativeLayout
				int margin = (size-height) / 2;
				ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
				params.height = height;
				params.leftMargin = margin;
				params.rightMargin = margin;
			}
			else {
				view.getLayoutParams().height = size;
			}
		}
	}
	
	public void roundChildViewAt(int position) {
		mPositions.add(position);
	}
	
}
