package com.limbika.material.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;

/**
 * Bar shape timer.
 */
public class BarTimerView extends TimerView {

	/**
	 * The constant to define horizonal bar.
	 */
	public static final int HORIZONTAL = 1;
	/**
	 * The constant to define vertical bar.
	 */
	public static final int VERTICAL = 2;
	
	private RectF mRectIncreas;
	private RectF mRectContent;
	private float mLength;
	private float mCurrent;
	private int mOrientation;
	
	public BarTimerView(Context context) {
		super(context);
	}

	public BarTimerView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public BarTimerView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	
	/**
	 * Set the orientation of the clock.
	 * @param Orientation To set horizontal orientation use {@link #HORIZONTAL}
	 * or {@link #VERTICAL} to set vertical.
	 */
	public void setOrientation(int Orientation) {
		mOrientation = Orientation;
	}
	
	/**
	 * @return The orientation of the timer.
	 * @see #HORIZONTAL
	 * @see #VERTICAL
	 */
	public int getOrientation() {
		return mOrientation;
	}
	
	@Override
	public void start() {
		post(new Runnable() {
			
			@Override
			public void run() {
				// Calc and set the refresh time
				long refreshTime = (long) ((getTime()) / mLength);
				setRefreshTime(refreshTime);
				
				BarTimerView.super.start();
			}
		});
	}
	
	@Override
	protected void onCreate() {
		mCurrent = 0;
		mOrientation = VERTICAL;
		mRectIncreas = new RectF();
		mRectContent = new RectF();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		initRects();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if ( getOrientation() == HORIZONTAL ) {
			mRectIncreas.left 	= beginAtStart() ? mRectIncreas.left 	: mLength-mCurrent;
			mRectIncreas.right 	= beginAtStart() ? mCurrent 			: mRectIncreas.right;
		}
		else {
			mRectIncreas.top 	= beginAtStart() ? mRectIncreas.top : mLength-mCurrent;
			mRectIncreas.bottom = beginAtStart() ? mCurrent			: mRectIncreas.bottom;
		}
		
		canvas.drawRect(mRectIncreas, getPaintIncrease());
		super.onDraw(canvas);
	}
	
	@Override
	protected void onTick(long currentTime) {
		mCurrent++;
	}
	
	@Override
	protected void onFinish() {
		mCurrent = mLength;
	}
	
	private void initRects() {
		int l = getPaddingLeft();
		int t = getPaddingTop();
		int r = getPaddingRight();
		int b = getPaddingBottom();
		
		mRectContent.left 	= l; 
		mRectContent.top 	= t;
		mRectContent.right 	= getMeasuredWidth() - r;
		mRectContent.bottom = getMeasuredHeight() - b;
		
		if ( getOrientation() == HORIZONTAL ) {
			mRectIncreas.left 	= beginAtStart() ? mRectContent.left : mRectContent.right; 
			mRectIncreas.top 	= mRectContent.top;
			mRectIncreas.right 	= beginAtStart() ? mRectContent.left : mRectContent.right;
			mRectIncreas.bottom = mRectContent.bottom;
		}
		else {
			mRectIncreas.left 	= mRectContent.left; 
			mRectIncreas.bottom = beginAtStart() ? mRectContent.top : mRectContent.bottom;
			mRectIncreas.right 	= mRectContent.right;
			mRectIncreas.top 	= beginAtStart() ? mRectContent.top : mRectContent.bottom;
		}
		
		mLength = mOrientation == HORIZONTAL ? mRectContent.right : mRectContent.bottom;
	}

}