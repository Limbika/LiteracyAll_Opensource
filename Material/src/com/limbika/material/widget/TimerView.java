package com.limbika.material.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;

import com.limbika.material.R;

public class TimerView extends View {

	 /**
     * Interface definition for a callback to be invoked when the clock is finished.
     */
	public interface OnFinishListener {
		/**
		 * Called when the timer is finished.
		 */
		public void onFinish();
	}
	
	protected static final String TAG = "TimerView";
	
	private GestureDetector mGestureDetector;
	private boolean mIsDoubleTapEnabled = false;
	private boolean mIsAutoStart = false;
	private Thread mThread;
	private OnFinishListener mListener;
	private long mTimeCurrent = 0;
	private long mTimeTotal = 0;
	private long mRefreshTime;
	private boolean mIsRunning;
	private boolean mIsFinish = false;
	private int mClockColor= Color.WHITE;
	private Paint mPaintIncrease;
	private boolean mBeginAtStart = true;
	private Bitmap mPauseBitmap;
	private int mPauseLeft = -1;
	private int mPauseTop = -1;
	private Paint mPaintPause;
	
	public TimerView(Context context) {
		super(context);
		create();
	}

	public TimerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		create();
	}
	
	public TimerView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		create();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSize  = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		setMeasuredDimension(widthSize, heightSize);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if ( mPauseTop == -1 ) 	mPauseTop = canvas.getHeight()/2 - mPauseBitmap.getHeight()/2;
		if ( mPauseLeft == -1 ) mPauseLeft = canvas.getWidth()/2 - mPauseBitmap.getWidth()/2;
		if ( !isRunning() && !isFinish() ) {
			canvas.drawCircle(canvas.getWidth()/2, canvas.getHeight()/2, Math.max(mPauseBitmap.getHeight()/2, mPauseBitmap.getWidth()/2), mPaintPause);
			canvas.drawBitmap(mPauseBitmap, mPauseLeft, mPauseTop, null);
		}
	}
	
	private static final int Y_OFFSET = 300;
	private float mFirstTouchY = -1;
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// Two fingers - Edit mode
		int maskedAction = event.getActionMasked();
		switch ( maskedAction ) {
			case MotionEvent.ACTION_POINTER_DOWN: {
				mFirstTouchY = event.getRawY();
			}
			break;
			
			case MotionEvent.ACTION_POINTER_UP: {
				if (mFirstTouchY != -1 && event.getRawY() - mFirstTouchY >= Y_OFFSET) {
					// Forze finish
					finish();
					return true;
				}
			}
		}

		return mGestureDetector.onTouchEvent(event);
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		
		// Autostart
		if ( mIsAutoStart ) start();
	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mIsRunning = false;
	}
	
	public void setBeginAtStart(boolean flag) {
		mBeginAtStart = flag;
	}
	
	public boolean beginAtStart() {
		return mBeginAtStart;
	}
	
	/**
	 * Set the timer starts automatically.
	 */
	public void setAutoStart() {
		mIsAutoStart = true;
	}
	
	/**
	 * Enable or disable double tap to play/stop timer.
	 * @param flag True to enable, false otherwise.
	 */
	public void setEnabledDoubleTap(boolean flag) {
		mIsDoubleTapEnabled = flag;
	}
	
	/**
	 * @return True if it is enabled, false otherwise.
	 */
	public boolean isEnabledDoubleTap() {
		return mIsDoubleTapEnabled; 
	}
	
    /**
     * Register a callback to be invoked when this timer finish.
     * @param listener The callback that will run.
     */
	public void setOnFinishListener(OnFinishListener listener) {
		mListener = listener;
	}
	
	/**
	 * Start the timer.
	 */
	public void start() {
		if ( mTimeTotal == 0 ) {
			finish();
			return;
		}
		if ( mIsRunning ) {
			return;
		}
		
		final Handler handler = new Handler();
		mIsRunning = true;
		mThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while ( mTimeCurrent < mTimeTotal ) {
					if ( !mIsRunning ) return;
					
					long sleepTime = 0;
					try {
						long t1 = System.currentTimeMillis();
						onTick(mTimeCurrent);
						long t2 = System.currentTimeMillis();
						sleepTime = getRefreshTime() - (t2-t1);
						Thread.sleep( sleepTime < 0 ? 0 : sleepTime );
						mTimeCurrent = mTimeCurrent + getRefreshTime();
					}
					catch (Exception e) {
						Log.e(TAG, "Thread crash! sleep time=" + sleepTime, e);
						mIsRunning = false;
					}
					finally {
						handler.post(new Runnable() {
							
							@Override
							public void run() {
								invalidate();
							}
						});
					}
				}
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						finish();
						invalidate();
					}
				});
			}
		});
		mThread.start();
	}
	
	/**
	 * Stop the timer.
	 */
	public void stop() {
		try {
			mIsRunning = false;
			mThread.join();
		}
		catch (InterruptedException e) {
			Log.e(TAG, "Error stoping thread!", e);
		}
		finally {
			mThread = null;
		}
	}
	
	/**
	 * Reset the timer.
	 */
	public void reset() {
		onCreate();
		requestLayout();
	}
	
	/**
	 * @return True it is running, false otherwise.
	 */
	public boolean isRunning() {
		return mIsRunning;
	}
	
	/**
	 * Set the time of the timer.
	 * @param millis The time in millisenconds.
	 */
	public void setTime(long millis) {
		mTimeTotal = millis;
	}
	
	/**
	 * @return The total time.
	 */
	public long getTime() {
		return mTimeTotal;
	}
	
	/**
	 * Set the color of the timer.
	 * @param color The timer.
	 */
	public void setColor(int color) {
		mClockColor = color;
		mPaintIncrease.setColor( getColor() );
	}
	
	/**
	 * @return color of the timer.
	 */
	public final int getColor() {
		return mClockColor;
	}
	
	/**
	 * @return True if the timer is finished.
	 */
	public boolean isFinish() {
		return mIsFinish;
	}

	protected Paint getPaintIncrease() {
		return mPaintIncrease;
	}
	
	/**
	 * Set the refresh time for update position.
	 * @param time The time in milliseconds.
	 */
	protected final void setRefreshTime(long time) {
		mRefreshTime = time;
	}

	/**
	 * You must set the time before with {@link #setRefreshTime(long)}.
	 * @return The refresh time in milliseconds.
	 */
	protected final long getRefreshTime() {
		return mRefreshTime;
	}
	
	/**
	 * Called when the view is created.
	 */
	protected void onCreate() {}
	
	/**
	 * Called when the timer finish.
	 */
	protected void onFinish() {}
	
	/**
	 * Called each tick.
	 * @see {@link #getRefreshTime()}.
	 * @param currentTime The current spent time.
	 */
	protected void onTick(long currentTime) {}
	
	private void create() {
		// Gesture detector
		mGestureDetector = new GestureDetector(getContext(), new OnGestureListener() {
			
			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				return false;
			}
			
			@Override
			public void onShowPress(MotionEvent e) {
			}
			
			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
				return false;
			}
			
			@Override
			public void onLongPress(MotionEvent e) {
			}
			
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				return false;
			}
			
			@Override
			public boolean onDown(MotionEvent e) {
				return true;
			}
		});
		mGestureDetector.setOnDoubleTapListener(new OnDoubleTapListener() {
			
			@Override
			public boolean onSingleTapConfirmed(MotionEvent e) {
				return false;
			}
			
			@Override
			public boolean onDoubleTapEvent(MotionEvent e) {
				return false;
			}
			
			@Override
			public boolean onDoubleTap(MotionEvent e) {
				if ( isEnabledDoubleTap() ) {
					if ( isRunning() ) stop();
					else start();
					return true;
				}
				return false;
			}
		});
		
		// Init running flag
		mIsRunning = false;
		
		// Init paint
		mPaintIncrease = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaintIncrease.setStyle(Paint.Style.FILL);
		mPaintIncrease.setColor( getColor() );
		
		// Load bitmap
		mPauseBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_pause);
		mPaintPause = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaintPause.setColor( Color.argb(128, 255, 255, 255) );
		
		// On create
		onCreate();
	}
	
	private void finish() {
		mIsFinish = true;
		mIsRunning = false;
		if ( mListener != null ) {
			mListener.onFinish();
		}
		onFinish();
	}
	
}
