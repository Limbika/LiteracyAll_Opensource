package com.limbika.material.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;

/**
 * Circular timer.
 */
public class CircleTimerView extends TimerView {
	
	private float mStep = 0.1F;
	private float mDegrees;
	private RectF mRect;

	public CircleTimerView(Context context) {
		super(context);
	}
	
	public CircleTimerView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CircleTimerView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	
	@Override
	public void setTime(long millis) {
		super.setTime(millis);
		
		// Calc and set the refresh time
		long refreshTime = (long) ((millis*mStep) / 360);
		refreshTime = Math.max(1, refreshTime);
		setRefreshTime(refreshTime);
		
		// Recalc step
		mStep = (360*((float) refreshTime))/((float) millis);
	}
	
	@Override
	protected void onCreate() {
		mDegrees = 0;
		mRect = new RectF();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		int widthSize = getMeasuredWidth();
		int heightSize = getMeasuredHeight();

		float diameter	= Math.min(widthSize, heightSize);
		float left 		= (widthSize - diameter) / 2; 
		float top 		= (heightSize - diameter) / 2;
		
		mRect.left = diameter == widthSize ? 0 : left;
		mRect.top = diameter == heightSize ? 0 : top;
		mRect.right = left + diameter;
		mRect.bottom = top + diameter;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawArc(mRect, beginAtStart() ? 270 : -90, beginAtStart() ? mDegrees : -mDegrees, true, getPaintIncrease());
		super.onDraw(canvas);
	}
	
	@Override
	protected void onTick(long currentTime) {
		// Update degrees
		mDegrees = mDegrees + mStep;
	}
	
	@Override
	protected void onFinish() {
		mDegrees = 360;
	}
	
}