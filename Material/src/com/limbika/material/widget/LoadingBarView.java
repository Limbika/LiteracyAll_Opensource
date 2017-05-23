package com.limbika.material.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class LoadingBarView extends View {
	
	private static final int HEIGTH = 10;
	private static final int DEFAULT_COLOR = Color.WHITE;
	
	private int mColor = DEFAULT_COLOR;
	private boolean mIsInitialized = false;
	
	private Rect mRectProgress;
	private Rect mRectRest;
	
	private Paint mPaintProgress;
	private Paint mPaintRest;
	
	private float mMaxValue = 100;
	private float mProgress = 0;

	public LoadingBarView(Context context) {
		super(context);
	}

	public LoadingBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LoadingBarView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initialize();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
//        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = widthSize;
        int height = getPaddingBottom() + getPaddingTop() + HEIGTH;
        setMeasuredDimension(width, height);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		initialize();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawRect(mRectProgress, mPaintProgress);
		canvas.drawRect(mRectRest, mPaintRest);
	}
	
	public void setProgress(int progress) {
		mProgress = progress > mMaxValue ? mMaxValue : progress;
		
		if ( mIsInitialized ) {
			mRectProgress.right = getUnit();
			mRectRest.left = mRectProgress.right;	
			invalidate();
		}
	}
	
	public int getProgress() {
		return (int) mProgress;
	}
	
	public void setMax(int max) {
		mMaxValue = max;
	}
	
	public int getMax() {
		return (int) mMaxValue;
	}
	
	public void setColor(int color) {
		mColor = color;
		
		if ( mIsInitialized ) {
			mPaintProgress.setColor(color);
			mPaintRest.setColor( Color.argb(128, Color.red(mColor), Color.green(mColor), Color.blue(color)) );
		}
	}
	
	public int getColor() {
		return mColor;
	}
	
	private void initialize() {
		mRectProgress = new Rect(0, 0, getUnit(), HEIGTH);
		mRectRest = new Rect(mRectProgress.right, 0, getWidth(), HEIGTH);
		
		mPaintProgress = new Paint();
		mPaintProgress.setStyle(Style.FILL);
		mPaintProgress.setColor(mColor);
		
		mPaintRest = new Paint();
		mPaintRest.setStyle(Style.FILL);
		mPaintRest.setColor( Color.argb(128, Color.red(mColor), Color.green(mColor), Color.blue(mColor)) );
		
		mIsInitialized = true;
	}
	
	private int getUnit() {
		return (int) ( (getWidth()*mProgress) / mMaxValue );
	}
	
}
