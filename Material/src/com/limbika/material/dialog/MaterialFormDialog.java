package com.limbika.material.dialog;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.limbika.material.Animanation;
import com.limbika.material.R;

/**
 * Material form dialog.
 * <br>
 * Usage:
 * <pre>
 * MaterialFormDialog dialog = new MaterialFormDialog(this);
 * dialog.setFields( new String[]{ "Field1", "Field2", "Field3" } );
 * dialog.setValidations( new boolean[]{ true, false, false } ); // First field is necessary
 * dialog.setOnFormCompletedListener(new OnFormCompletedListener() { @Override
 *	public void onCompleted(String[] values) {
 *		Log.d(TAG, "count=" + values.length);
 *	}
 * dialog.show();
 * 
 * </pre>
 */
public class MaterialFormDialog extends BaseDialog {
	
	/**
	 * 
	 */
	public interface OnFormCompletedListener {
		/**
		 * Called when the form is completed
		 * @param values
		 */
		public void onCompleted(String[] values);
	}

	// -------------------------------------------------------------------------
	// Members
	private OnFormCompletedListener mListener;
	private EditText[] mEditTexts;
	private boolean[] mValidations;
	private LinearLayout mForm;
	private TextView mTitle;
	
	public MaterialFormDialog(Context context) {
		super(context);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); // Hide keyboard
		setContentView(R.layout.dialog_form);
		setCancelable(false);
		
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(metrics);
		
		View content = findViewById(R.id.content);
		content.setMinimumWidth( metrics.widthPixels/2 );
		
		findViewById(R.id.btn_positive).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String[] values = getValues();
				// Validate
				boolean validate = true;
				int count = values.length;
				for ( int i=0;i<count;i++ ) {
					if ( mValidations[i] ) {
						String text = values[i];
						if ( text == null || text.isEmpty() ) {
							validate = false;
							Animanation.blink(mEditTexts[i]);
							break;
						}
					}
				}
				if ( validate ) {
					if ( mListener != null ) mListener.onCompleted(values);
					dismiss();
				}
			}
		});
		
		findViewById(R.id.btn_negative).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		
		mForm = (LinearLayout) findViewById(R.id.form);
		mTitle = (TextView) findViewById(R.id.title);
		mTitle.setCompoundDrawablesWithIntrinsicBounds(getContext().getApplicationInfo().icon, 0, 0, 0);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if ( ev.getAction() == MotionEvent.ACTION_DOWN ) {
			hideKeyboard();
		}
		return super.dispatchTouchEvent(ev);
	}
	
	private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		for (TextView i : mEditTexts ) {
			imm.hideSoftInputFromWindow( i.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS );
		}
	}
	
	@Override
	public void setTitle(int title) {
		mTitle.setText(title);
	}
	
    /**
     * Set the title text for this form.
     * @param title The new text to display in the title.
     */
	public void setTitle(String title) {
		mTitle.setText(title);
	}
	
	/**
	 * Set a callback when the form is completed.
	 * @param listener The callback.
	 */
	public void setOnFormCompletedListener(OnFormCompletedListener listener) {
		mListener = listener;
	}
	
	/**
	 * 
	 * @param validations
	 */
	public void setValidations(boolean[] validations) {
		mValidations = validations;
	}

	/**
	 * 
	 * @param fields
	 */
	public void setFields(String[] fields) {
		int count = fields.length;
		mEditTexts = new EditText[count];
		
		mForm.removeAllViews();
		for ( int i=0;i<count;i++ ) {
			addField(i, fields[i]);
		}
	}
	
	/**
	 * Set the fields of the form.
	 * @param fields List of the field's label.
	 */
	public void setFields(int[] fields) {
		int count = fields.length;
		mEditTexts = new EditText[count];
		
		mForm.removeAllViews();
		for ( int i=0;i<count;i++ ) {
			addField( i, getContext().getResources().getString(fields[i]) );
		}
	}
	
	private String[] getValues() {
		int count = mEditTexts.length;
		String[] values = new String[count];
		for ( int i=0;i<count;i++ ) {
			values[i] = mEditTexts[i].getText().toString();
		}
		return values;
	}
	
	private void addField(int index, String label) {
		TextView textView = new TextView(getContext());
		textView.setLayoutParams( new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1) );
		textView.setText(label);
		textView.setTextColor(Color.BLACK);
		textView.setTextSize( getContext().getResources().getDimension(R.dimen.sp_02) );
		
		mEditTexts[index] = new EditText(getContext());
		mEditTexts[index].setSingleLine();
		mEditTexts[index].setLayoutParams( new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1) );
		mEditTexts[index].setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				v.setAnimation(null);
			}
		});
		
		LinearLayout row = new LinearLayout(getContext());
		row.setOrientation(LinearLayout.HORIZONTAL);
		row.setLayoutParams( new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT) );
		row.addView(textView);
		row.addView(mEditTexts[index]);
		
		mForm.addView(row);
	}
	
}
