package com.limbika.material.dialog;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.limbika.material.R;

/**
 * Input dialog with a EditText to insert a text and accept and cancel buttons.
 */
public class InputDialog extends SelectorDialog {
	
	private EditText mText;

	public InputDialog(Context context) {
		super(context);
		setContentView(R.layout.dialog_input);
		setCancelable(false);
		
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(metrics);
		
		LinearLayout ll = (LinearLayout) findViewById(R.id.llBackground);
		ll.setMinimumWidth( metrics.widthPixels/3 );
		ll.setBackgroundResource(R.drawable.bg_dialog);
		ll.post(new Runnable() {
			
			@Override
			public void run() {
				hideKeyboard();
			}
		});
		
		mText = (EditText) findViewById(R.id.text);
		
		Button buttonOk = (Button) findViewById(R.id.btn_ok);
		buttonOk.setBackgroundResource(R.drawable.btn_material);
		buttonOk.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onSelect(getText());
				dismiss();
			}
		});
		
		Button buttonCancel = (Button) findViewById(R.id.btn_cancel);
		buttonCancel.setBackgroundResource(R.drawable.btn_material);
		buttonCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}
	
	public void setText(String text) {
		mText.setText(text);
	}
	
	private String getText() {
		return mText.getText().toString();
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
		imm.hideSoftInputFromWindow( mText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS );
	}

}
