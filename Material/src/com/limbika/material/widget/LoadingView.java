package com.limbika.material.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class LoadingView extends View {
	
	private static final int SIZE = 30;
	private static final int TOTAL = 8*SIZE;
	
	private Rect mRect = new Rect();
	private Paint mPaint = new Paint();
	private int mPosition = 0;
	private int mLeft = 0;
	private int mTop = 0;

	public LoadingView(Context context) {
		super(context);
	}

	public LoadingView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// Make the view adaptable.
		// Change SIZE from constant to variable.
		setMeasuredDimension(100, 100);
		calcPadding();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		drawBoxes(canvas);
		update();
		invalidate();
	}
	
	private void calcPadding() {
//		int width = getWidth();
//		int height = getHeight();
//		
//		mLeft = (width-3*SIZE)/2;
//		mTop = (height-3*SIZE)/2;
	}
	
	private void drawBoxes(Canvas canvas) {
		for ( int i=0;i<8;i++ ) {
			int color = findColorByPosition(i);
			Rect rect = findRectByPosition(i);
			
			mPaint.setColor(color);
			canvas.drawRect(rect, mPaint);
		}
	}
	
	private void update() {
		mPosition++;
		if ( mPosition > TOTAL ) {
			mPosition = 0;
		}
	}
	
	private int findColorByPosition(int position) {
		switch ( position ) {
			case 0:
			case 4:
				return Color.parseColor("#fffcd604");
				
			case 1:
			case 6:
				return Color.parseColor("#ff59be5b");
			
			case 2:
			case 7:
				return Color.parseColor("#ffd63129");

			case 3:
			case 5:
				return Color.parseColor("#ff4c97d0");
				
			default:
				return 0;
		}
	}
	
	private Rect findRectByPosition(int position) {
		// Current positon
		int current = SIZE*position + mPosition;
		if ( current > TOTAL ) {
			current = current % TOTAL;
		}
		
		// Update rect
		if ( current <= 2*SIZE ) {
			mRect.left = current;
			mRect.top = 0;
		}
		else if ( current <= 4*SIZE ) {
			mRect.left = SIZE*2;
			mRect.top = current - (2*SIZE);
		}
		else if ( current <= 6*SIZE) {
			mRect.left = 2*SIZE - (current - (SIZE*4));
			mRect.top = 2*SIZE;
		}
		else {
			mRect.left = 0;
			mRect.top = SIZE*2 - (current - (SIZE*6)); 
		}	
		
		mRect.left = mRect.left + mLeft;
		mRect.top = mRect.top + mTop;
		mRect.right = mRect.left + SIZE;
		mRect.bottom  = mRect.top + SIZE;
		
		return mRect;
	}
	
}
