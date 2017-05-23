package com.limbika.material.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

/**
 * Cool and simple equalizer view.
 */
public class EqualizerView extends View {
	
	private static final int NUM_COLUMNS = 100;
	private static final int COLUMN_WIDTH  = 8;
	private static final int COLUMN_HEIGTH = 150;
	
	private final int[] COLORS = new int[NUM_COLUMNS];
	
	private Paint mPaint;
	private Rect[] mRects;
	private Handler mHandler;
	
	static int COLOR_INITIAL = Color.rgb(255, 0, 0);
	static int COLOR_FINAL   = Color.rgb(0, 255, 0);
	
	public EqualizerView(Context context) {
		super(context);
		initialize();
	}

	public EqualizerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}

	public EqualizerView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initialize();
	}
	
	private void initialize() {
		mPaint = new Paint();
		mPaint.setStyle(Style.FILL);
		
		mRects = new Rect[NUM_COLUMNS];
		for ( int i=0;i<NUM_COLUMNS;i++ ) {
			mRects[i] = new Rect(i*(COLUMN_WIDTH)+1, 0, i*(COLUMN_WIDTH)+COLUMN_WIDTH-1, COLUMN_HEIGTH);
		}
		
		// Build colors
		int next = COLOR_INITIAL;
		int count = 0;
		
		final int STEP = (4*256)/NUM_COLUMNS;
		for ( int i=0;i<COLORS.length;i++ ) {
			COLORS[i] = next;
			
			int r = Color.red(next);
			int g = Color.green(next);
			int b = Color.blue(next);
			
			//if ( r == 255 && g == 0 ) {
			if ( count < 256 ) {
				if ( b+STEP < 255 ) {
					next = Color.rgb(255, 0, b+STEP);
				}
				else {
					next = Color.rgb(255, 0, 255);
				}
			}
			//else if ( r == 255 && b == 255 ) {
			else if ( count < 2*256 ) {
				if ( r-STEP > 0 ) {
					next = Color.rgb(r-STEP, 0, 255);
				}
				else {
					next = Color.rgb(0, 0, 255);
				}
			}
			else if ( count < 3*256 ) {
			//else if ( r == 0 && g == 255 ) {
				if ( g+STEP < 255 ) {
					next = Color.rgb(0, g+STEP, 255);
				}
				else {
					next = Color.rgb(0, 255, 255);
				}
			}
			else {
				if ( b-STEP > 0 ) {
					next = Color.rgb(0, 255, b-STEP);
				}
				else {
					next = Color.rgb(0, 255, 0);
				}
			}
			
			count = count + STEP;
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(NUM_COLUMNS*COLUMN_WIDTH, COLUMN_HEIGTH);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		for ( int i=0; i<NUM_COLUMNS;i++ ) {
			mPaint.setColor( COLORS[i] );
			mRects[i].top = COLUMN_HEIGTH - randomHeigth();
			canvas.drawRect(mRects[i], mPaint);
		}
	}
	
	public void start() {
		if ( mHandler != null ) return;
		 
		mHandler = new Handler();
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				invalidate();
				mHandler.postDelayed(this, 300);
			}
		}, 100);
	}
	
	public void stop() {
		if ( mHandler == null ) return;
		mHandler.removeCallbacksAndMessages(null);
		mHandler = null;
	}
	
	private int randomHeigth() {
		return (int) (COLUMN_HEIGTH*Math.random());
	}
	
}
