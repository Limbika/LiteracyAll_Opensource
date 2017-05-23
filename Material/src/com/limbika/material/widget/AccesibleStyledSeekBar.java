package com.limbika.material.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.limbika.material.R;

/**
 * Accesible SeekBar.
 * <p>
 * To be accesible the SeekBar has two buttons to move
 * the bar to the left or to the rigth.
 * 
 * @author Limbika
 *
 */
public class AccesibleStyledSeekBar extends LinearLayout {
	
	private SeekBar	mSeekBar;

	@SuppressWarnings("deprecation")
	public AccesibleStyledSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.view_seekbar_accesible, this);
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AccesibleStyledSeekBar, 0, 0);
		Drawable styleLess = ta.getDrawable(R.styleable.AccesibleStyledSeekBar_left);
		Drawable styleMore = ta.getDrawable(R.styleable.AccesibleStyledSeekBar_rigth);
		ta.recycle();
		
		mSeekBar = (SeekBar) findViewById(R.id.bar);
		
		ImageButton less = (ImageButton) findViewById(R.id.less);
		if ( Build.VERSION.SDK_INT < 16 ) {
			less.setBackgroundDrawable(styleLess);
		}
		else {
			less.setBackground(styleLess);
		}
		less.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if ( mSeekBar.getProgress() > 0 )
					mSeekBar.setProgress(mSeekBar.getProgress() - 1);
			}
		});
		
		ImageButton more = (ImageButton) findViewById(R.id.more);
		if ( Build.VERSION.SDK_INT > 15 ) {
			more.setBackgroundDrawable(styleMore);
		}
		else {
			more.setBackground(styleMore);
		}
		more.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if ( mSeekBar.getProgress() < mSeekBar.getMax() )
					mSeekBar.setProgress(mSeekBar.getProgress() + 1);
			}
		});
		
	}

	public void setMax(int max) {
		mSeekBar.setMax(max);
	}
	
	public void setOnSeekBarChangeListener(OnSeekBarChangeListener listener) {
		mSeekBar.setOnSeekBarChangeListener(listener);
	}
	
	public void setProgress(int progress) {
		mSeekBar.setProgress(progress);
	}
	
	public int getProgress() {
		return mSeekBar.getProgress();
	}

}
