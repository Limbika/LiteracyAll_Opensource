package com.limbika.material.widget;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.limbika.material.R;

/**
 * Color panel with a default colors, a color picker and
 * a memory of {@link #NUM_RECORDS} colors stored.
 * 
 * Usage as a {@link View} and use the methods:
 * <ul>
 * 	<li><{@link #setColor(int)}</li>
 * 	<li><{@link #getColor()}</li>
 * 	<li><{@link #setOnColorSelectedListener(OnColorSelectedListener)()}</li>
 * </ul>
 */
public class ColorPanel extends LinearLayout {
	
	public interface OnColorSelectedListener {
		public void onColorSelected(int color);
	}
	
	enum ColorRow {
		
		ROW1 ( new String[]{"#ffAFDDE9", "#ff0066ff", "#ffccaaff", "#ff7f2aff", "#ffffaaaa"} ),
		ROW2 ( new String[]{"#ffd40000", "#ffFFCBFF", "#ffd42aff", "#ffFFCC99", "#ffff6600"} ),
		ROW3 ( new String[]{"#ffffd8c0", "#ffa05a2c", "#FFFFFF9C", "#ffffd42a", "#ffD0D0D0"} ),
		ROW4 ( new String[]{"#ff1a1a1a", "#ffCBFECB", "#ff55d400", "#ffffffff", "#00000000"} );
		
		String[] colors;
		private ColorRow(String[] colors) {
			this.colors = colors;
		}
	}
	
	private static final int NUM_RECORDS = 9;
	private static final String NAME = "colors";
	private static final String KEY_COUNT = "count";
	private static final String KEY_CURSOR = "cursor";
	
	private SharedPreferences mPrefs;
	private ArrayList<Integer> mColors;
	private int mCursor;
	private OnColorSelectedListener mListener;
	
	private LinearLayout mRecordsView;
	private ColorPickerView mColorPicker;
	
	public ColorPanel(Context context) {
		super(context);
		initialize();
	}

	public ColorPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}
	
	/**
	 * Set the color of the color picker.
	 * @param color The color.
	 */
	public void setColor(int color) {
		mColorPicker.setColor(color);
	}
	
	/**
	 * @return The selected color or -1 if is not selected.
	 */
	public int getColor() {
		return mColorPicker.getColor();
	}
	
	public void setOnColorSelectedListener(OnColorSelectedListener listener) {
		mListener = listener;
	}
	
	private void initialize() {
		// -------------------------------------------------------------------------
		// Saved colors
		mPrefs = getContext().getSharedPreferences(NAME, Context.MODE_PRIVATE);
		mColors = new ArrayList<Integer>();
		mCursor = mPrefs.getInt(KEY_CURSOR, 0);
		
		int count = mPrefs.getInt(KEY_COUNT, 0);
		for ( int i=0;i<count;i++ ) {
			String key = String.valueOf(i);
			int color = mPrefs.getInt(key, 0);
			mColors.add(color);
		}
		
		// -------------------------------------------------------------------------
		// View
		
		// ColorPicker
		LinearLayout.LayoutParams pickerParmas = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		pickerParmas.weight = 1;
		
		mColorPicker = new ColorPickerView(getContext());
		mColorPicker.setLayoutParams(pickerParmas);
		
		// Default colors
		LinearLayout.LayoutParams defaultsParmas = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		pickerParmas.weight = 1;
		
		LinearLayout wrapper = new LinearLayout(getContext());
		wrapper.setOrientation(LinearLayout.VERTICAL);
		wrapper.setLayoutParams(defaultsParmas);
		
		for ( ColorRow row : ColorRow.values() ) {
			LinearLayout rowLayout =  new LinearLayout(getContext());
			rowLayout.setOrientation(LinearLayout.HORIZONTAL);
			
			for ( String hex : row.colors ) {
				final int color = android.graphics.Color.parseColor(hex);
				
				ImageView view = new ImageView(getContext());
				setDrawableBackground(view, buildRoundDrawable(color) );
				view.setLayoutParams( getLayoutParamsForColorBox() );
				view.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						setSelectedColor(color);
					}
				});
				
				if ( color == 0 ) view.setImageResource( R.drawable.ic_rec_remove );
				//else 				view.setImageDrawable( new ColorDrawable(color) );
				
				rowLayout.addView(view);
			}
			
			wrapper.addView(rowLayout);
		}
		
		// Stored colors
		LayoutParams recordsParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		recordsParams.topMargin = getContext().getResources().getDimensionPixelOffset(R.dimen.dp_05);
		
		mRecordsView = new LinearLayout(getContext());
		mRecordsView.setId( generateId() );
		mRecordsView.setLayoutParams(recordsParams);
		mRecordsView.setOrientation(LinearLayout.VERTICAL);
		mRecordsView.setGravity(Gravity.CENTER);
		
		wrapper.addView(mRecordsView);
		updateRecordsView();
		
		// Main view
		setOrientation(HORIZONTAL);
		setGravity(Gravity.CENTER);
		addView(mColorPicker);
		addView(wrapper);
	}
	
	private LinearLayout.LayoutParams getLayoutParamsForColorBox() {
		int size = (int) getResources().getDimension(R.dimen.dp_07);
		int margin = (int) getResources().getDimension(R.dimen.dp_03);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
		lp.setMargins(margin, margin, margin, margin);
		return lp;
	}
	
	private void updateRecordsView() {
		mRecordsView.removeAllViews();
		
		LinearLayout row1 = new LinearLayout(getContext());
		row1.setGravity(Gravity.CENTER);
		row1.setOrientation(HORIZONTAL);
		
		LinearLayout row2 = new LinearLayout(getContext());
		row2.setGravity(Gravity.CENTER);
		row2.setOrientation(HORIZONTAL);
		
		for ( int i=0;i<NUM_RECORDS;i++ ) {
			final int position = i;
			int color = 0;
			if ( position < mColors.size() ) color = mColors.get(i);
			
			ImageView view = new ImageView(getContext());
			setDrawableBackground(view, buildRoundDrawable(color) ) ;
			view.setLayoutParams( getLayoutParamsForColorBox() );
			view.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if ( position < mColors.size() ) setSelectedColor( mColors.get(position) );
				}
			});
			
			if ( i < 5 ) 	row1.addView(view);
			else 			row2.addView(view);
		}
		
		Button add = new Button(getContext());
		add.setLayoutParams(getLayoutParamsForColorBox());
		add.setText("+");
		add.setTypeface(Typeface.DEFAULT_BOLD);
		add.setTextSize( getContext().getResources().getDimension(R.dimen.sp_03) );
		add.setTextColor(Color.LTGRAY);
		add.setBackgroundColor(Color.TRANSPARENT);
		add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				addColor( mColorPicker.getColor() );
				updateRecordsView();
			}
		});
		row2.addView(add);
		
		mRecordsView.addView(row1);
		mRecordsView.addView(row2);
	}
	
	private void addColor(int color) {
		if ( mColors.size() < NUM_RECORDS ) {
			mColors.add(color);
		}
		else {
			mColors.set(mCursor, color);
		}
		
		// Update cursor
		mCursor++;
		if ( mCursor == NUM_RECORDS ) mCursor = 0;
		
		// Save
		mPrefs.edit().putInt(KEY_COUNT, mColors.size()).commit();
		mPrefs.edit().putInt(KEY_CURSOR, mCursor).commit();
		for ( int i=0;i<mColors.size();i++ ) {
			String key = String.valueOf(i);
			mPrefs.edit().putInt(key, mColors.get(i)).commit();
		}
		
		// Listener
		if ( mListener != null ) mListener.onColorSelected(color);
	}
	
	private void setSelectedColor(int color) {
		mColorPicker.setColor(color);
		if ( mListener != null ) mListener.onColorSelected(color);
	}
	
	private Drawable buildRoundDrawable(int color) {
		int size = (int) getResources().getDimension(com.limbika.material.R.dimen.dp_08);
		
		ShapeDrawable drawable = new ShapeDrawable();
		drawable.setShape( new OvalShape() );
		drawable.getPaint().setColor( color == 0 ? Color.LTGRAY : color );
		drawable.getPaint().setAntiAlias(true);
		drawable.getPaint().setStyle( color ==  0 ? Style.STROKE : Style.FILL );
		drawable.getPaint().setStrokeWidth(1);
		drawable.setIntrinsicHeight(size);
		drawable.setIntrinsicWidth(size);
		
		return drawable;
	}
	
	private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    private static int generateId() {
        for (;;) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }
    
    /**
	 * Call to setBackground method or setBackgroundDrawable. Depends API level
	 * @param d The drawable
	 */
	@SuppressWarnings("deprecation")
	private void setDrawableBackground (View v, Drawable d) {
		if ( Build.VERSION.SDK_INT > 15 ) {
			v.setBackground(d);
		}
		else {
			v.setBackgroundDrawable(d);
		}
	}

}
