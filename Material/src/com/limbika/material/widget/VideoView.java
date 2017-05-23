package com.limbika.material.widget;

import java.io.File;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.ToggleButton;

import com.limbika.material.R;

public class VideoView extends RelativeLayout {
	
	private static final String TAG = "VieoView";
	
	private SurfaceView 		mSurfaceView;
	private SurfaceHolder	 	mSurfaceHolder;
	private MediaPlayer			mMediaPlayer;
	private SeekBar				mProgressBar;
	private LinearLayout		mControls;
	private boolean				mFlag = false;
	private Runnable			mStopCallback;

	public VideoView(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.view_video, this, true);
		
		mControls = (LinearLayout) findViewById(R.id.controls);
		
		mSurfaceView = (SurfaceView) findViewById(R.id.surface_view);
		mSurfaceView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switchControlVisibility();
			}
		
		});
		
		mSurfaceHolder = mSurfaceView.getHolder();
		
		mProgressBar = (SeekBar)findViewById(R.id.progress_bar);
		mProgressBar.setProgress(0);
		mProgressBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if ( fromUser && mMediaPlayer != null ) {
					mMediaPlayer.seekTo( progress );
				}
			}
		});
		
		ToggleButton button = (ToggleButton) findViewById(R.id.mediaplayer_playpause);
		button.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if ( mMediaPlayer != null && !mMediaPlayer.isPlaying() ) {
					play();
				}
				else if ( mMediaPlayer != null && mMediaPlayer.isPlaying() ) {
					pause();
				}
			}
		});
		
		findViewById(R.id.btn_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				stop();
			}
		});
	}
	
	public void setWihtoutControls() {
		mControls.setVisibility(View.GONE);
	}
	
	/**
	 * 
	 * @param file
	 */
	public void setPath(File file) {
		setPath( file.getAbsolutePath() );
	}
	
	/**
	 * 
	 * @param path
	 */
	public void setPath(String path) {
		try {
			mMediaPlayer = new MediaPlayer();
			mMediaPlayer.setDataSource(path);
			mMediaPlayer.prepare();
			mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer mp) {
					stop();
				}
			});
			mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {
				
				@Override
				public void onPrepared(MediaPlayer mp) {
					post(new Runnable() {
						
						@Override
						public void run() {
							// Autoplay
							play();
							// Initially show the controls
							startWatchdog();
						}
					});
				}
			});
			
			mProgressBar.setMax( (int) mMediaPlayer.getDuration() );
		}
		catch (Exception e) {
			Log.e(TAG, "Error preparing the video!", e);
			stop();
			return;
		}
	}
	
	public int getVideoWith() {
		if ( mMediaPlayer != null ) {
			return mMediaPlayer.getVideoWidth();
		}
		return 0;
	}
	
	public int getVideoHeigth() {
		if ( mMediaPlayer != null ) {
			return mMediaPlayer.getVideoHeight();
		}
		return 0;
	}
	
	public void setOnFinishCallback(Runnable runnable) {
		mStopCallback = runnable;
	}
	
	public void play() {
		try {
			mMediaPlayer.setDisplay(mSurfaceHolder);
			mMediaPlayer.start();
			mFlag = true;
			
			final Handler handler = new Handler();
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						while ( mFlag ) {
							Thread.sleep(100);
							handler.post(new Runnable() {
								
								@Override
								public void run() {
									if ( mFlag ) mProgressBar.setProgress( mMediaPlayer.getCurrentPosition() );
								}
							});
						}
					}
					catch (Exception e) {
						Log.e(TAG, "Error in update thread!", e);
					}
				}
			}).start();
		} 
		
		catch (Exception e) {
			Log.e(TAG, "Play the video. Error!: " + e);
		}
	}
	
	public void pause() {
		mMediaPlayer.pause();
		mFlag = false;
	}
	
	public void stop() {
		try {
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
		catch (Exception e) {
			Log.e(TAG, "Error in stop() " + e );
		}
		finally {
			mFlag = false;
			if ( mStopCallback != null ) mStopCallback.run();
		}
	}
	
	private void switchControlVisibility() {
		if ( mControls.getVisibility() == View.VISIBLE ) {
			mControls.setVisibility(View.INVISIBLE);
			stopWatchdog();
		}
		else if ( mControls.getVisibility() == View.INVISIBLE ) {
			mControls.setVisibility(View.VISIBLE);
			startWatchdog();
		}
	}
	
	//-------------------------------------------------------------------------
	// Watchdog
	private static final long CHECK_TIME = 1000L;	// 1s
	private static final long WATCH_TIME = 5000L; 	// 5s
	private boolean mWatchdog = false;
	private long mTime;
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		mTime = System.currentTimeMillis();
		return super.dispatchTouchEvent(ev);
	}

	private void startWatchdog() {
		if ( mWatchdog ) {
			return;
		}
		mWatchdog = true;
		final Handler handler = new Handler();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					while ( mWatchdog ) {
						Thread.sleep(CHECK_TIME);
						if ( mWatchdog ) {
							long t = System.currentTimeMillis();
							if ( (t -mTime) > WATCH_TIME ) {
								handler.post(new Runnable() {
									
									@Override
									public void run() {
										switchControlVisibility();										
									}
								});
							}
						}
					}
				}
				catch (Exception e) {
					Log.e(TAG, "Error in watchdog thread!", e);
				}
			}
		}).start();
	}
	
	private void stopWatchdog() {
		mWatchdog = false;
	}

}
