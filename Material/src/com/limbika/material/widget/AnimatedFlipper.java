package com.limbika.material.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ViewFlipper;

/**
 * AnimatedFlipper encapsule the animations for the ViewFlipper and handle the flip between views. 
 * It can have wheel behavior if is enabled and can flip with a gesture if is enabled too.
 * @author Limbika
 *
 */
public abstract class AnimatedFlipper extends ViewFlipper {
	
	private static final int DURATION = 500;
	
	private boolean isWheel = false;
	private boolean canFlipWithGesture = false;
	private OnFlipGestureListener mGestureListener= null;
	
	/**
	 * Interface to handle flip gesture events. 
	 * A flip gesture method is fired when the view detect a touch event moving horizontally in one direction and the distance is grater than the offset.
	 * <p>
	 * Both method are called after the showNext of showPrevios methods.
	 */
	public interface OnFlipGestureListener {
		public boolean onLeftToRightGesture (AnimatedFlipper parent);
		public boolean onRightToLeftGesture (AnimatedFlipper parent);
	}
	
	private float initialX;
	private long initialTime;
	private float offset = 50.0f;
	private OnTouchListener mTouchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (!canFlipWithGesture)
				return false;
			else 
			{
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					initialX = event.getX();
					initialTime = event.getDownTime();
					return false;
				}
				if (event.getAction() == MotionEvent.ACTION_UP && initialTime == event.getDownTime()) {
					float varianceX = event.getX() - initialX;
					//Gesture left to right
					if (varianceX > 0 && varianceX > offset ) {
						showPrevious();
						if (mGestureListener != null)
							return mGestureListener.onLeftToRightGesture(AnimatedFlipper.this);
						return false;
					} 
					//Gesture from right to left
					else if (varianceX < 0 && varianceX > -1*offset) {
						showNext();
						if (mGestureListener != null)
							return mGestureListener.onRightToLeftGesture(AnimatedFlipper.this);
						return false;
					} 
						
				}
				return false;
			}
		}
	};

	public AnimatedFlipper(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnTouchListener(mTouchListener);
	}
	
	public AnimatedFlipper(Context context) {
		this(context, null);
	}
	
	@Override
	public void showNext() {
		if (getDisplayedChild() >= getChildCount())
			if (isWheel)
				setDisplayedChild(0);
			else 
				return;
		setInAnimation(inFromRightAnimation());
		setOutAnimation(outToLeftAnimation());
		super.showNext();
	}
	
	@Override
	public void showPrevious() {
		if (getDisplayedChild() <= 0)
			if (isWheel)
				setDisplayedChild(getChildCount() -1);
			else
				return;
		setInAnimation(inFromLeftAnimation());
		setOutAnimation(outToRightAnimation());
		super.showPrevious();
	}
	
	public void setWheel(boolean enabled) {
		isWheel = enabled;
	}
	
	public boolean isWheel () {
		return isWheel;
	}
	
	/*
	 * Set the OnFlipGestureListener and start to listen touch event to fire the flipGestureListener.
	 * If is needed, you can setCanFlipWithGesture to false and the flipGestureListener will be ignored;
	 */
	public void setOnFlipGestureListener (OnFlipGestureListener flipGestureListener) {
		canFlipWithGesture = true;
		mGestureListener = flipGestureListener;
	}
	
	public boolean canFlipOnGesture() {
		return canFlipWithGesture;
	}
	
	public void setCanFlipOnGesture (boolean enable) {
		canFlipWithGesture = enable;
	}
	
	public void setFlipGestureOffset (float offset) {
		this.offset = offset;
	}

	private Animation inFromRightAnimation() {
		 
        Animation inFromRight = new TranslateAnimation(
        Animation.RELATIVE_TO_PARENT,  +1.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
        Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f );
 
        inFromRight.setDuration(DURATION);
        inFromRight.setInterpolator(new AccelerateInterpolator());
 
        return inFromRight;
 
    }
 
    private Animation outToLeftAnimation() {
        Animation outtoLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoLeft.setDuration(DURATION);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }
 
    private Animation inFromLeftAnimation() {
        Animation inFromLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromLeft.setDuration(DURATION);
        inFromLeft.setInterpolator(new AccelerateInterpolator());
        return inFromLeft;
    }
 
    private Animation outToRightAnimation() {
        Animation outtoRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoRight.setDuration(DURATION);
        outtoRight.setInterpolator(new AccelerateInterpolator());
        return outtoRight;
    }

}
