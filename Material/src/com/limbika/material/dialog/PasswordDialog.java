package com.limbika.material.dialog;

import java.lang.ref.WeakReference;
import java.util.Random;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.limbika.material.R;

/**
 * Dialog to manage the access of something with a PIN based/like password.
 */
public class PasswordDialog extends BaseDialog implements Runnable {
	
	/**
	 * Interface definition for a callback to be invoked when the correct password is inserted.
	 */
	public interface OnPasswordCorrectListener {
		/**
		 * Called when the correct password is inserted.
		 */
		public void onCorrect();
	}
	
	private static final String			TAG = "PasswordDialog";

	private int[]						mPassword;
	private OnPasswordCorrectListener 	mListener;
	private int							mIndex;
	private long						mTime = -1L;
	private int							mTimeCount;

	public PasswordDialog(Context context) {
		super(context);
		setCancelable(true);
		
		/* Random sum to set password */
		final Random random = new Random();
		String msg = initPassword(random);
		
		/* UI */
		int text 	= context.getResources().getDimensionPixelOffset(R.dimen.sp_03);
		int size 	= context.getResources().getDimensionPixelOffset(R.dimen.dp_08);
		int paddng2 = context.getResources().getDimensionPixelOffset(R.dimen.dp_02);
		int paddng3 = context.getResources().getDimensionPixelOffset(R.dimen.dp_03);
		LayoutParams lpWrap  = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		LinearLayout[] linears = {	
				new LinearLayout(context), 
				new LinearLayout(context), 
				new LinearLayout(context),
				new LinearLayout(context)
		};
		
		LinearLayout root = new LinearLayout(context);
		root.setLayoutParams(lpWrap);
		root.setOrientation(LinearLayout.VERTICAL);
		root.setGravity(Gravity.CENTER);
		root.setBackgroundResource(R.drawable.bg_password_dialog);
		root.setPadding(paddng3, paddng2, paddng3, paddng2);
		
		LayoutParams lpSeparator = new LayoutParams(LayoutParams.MATCH_PARENT, 1);
		lpSeparator.setMargins(paddng3, paddng2, paddng3, paddng2);
		View separator = new View(context);
		separator.setLayoutParams(lpSeparator);
		separator.setBackgroundColor(Color.WHITE);
		
		final TextView question = new TextView(context);
		question.setTextSize(text);
		question.setText(msg);
		question.setTextColor(Color.WHITE);
		question.setLayoutParams(lpWrap);
		
		Button exit = new Button(context);
		exit.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, size));
		exit.setBackgroundResource(R.drawable.btn_white_rect_transparent);
		exit.setText( android.R.string.cancel );
		exit.setTextSize( getContext().getResources().getDimension(R.dimen.sp_01) );
		exit.setTextColor(Color.WHITE);
		exit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		
		linears[3].setGravity(Gravity.RIGHT);
		linears[3].addView(exit);
		
		for ( LinearLayout ll : linears ) {
			ll.setLayoutParams(lpWrap);
			ll.setOrientation(LinearLayout.HORIZONTAL);
		}
		
		for ( int i=0;i<10;i++) {
			Button b = new Button(context);
			b.setLayoutParams(new LayoutParams(size, size));
			b.setBackgroundResource(R.drawable.btn_white_oval_transparent);
			b.setTag(i);
			b.setTextSize(text);
			b.setText(String.valueOf(i));
			b.setTextColor(Color.WHITE);
			b.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					mTimeCount = 1;
					int tag = (Integer) v.getTag();
					if ( tag == mPassword[mIndex] ) {
						mIndex++;
						if ( mIndex == mPassword.length ) {
							/* Password correct */
							if (mListener != null) mListener.onCorrect();
							dismiss();
							String msg = initPassword(random);
							question.setText(msg);
						}
					}
					else {
						mIndex = 0;
					}
				}
			});
			if ( i == 0 )	linears[3].addView(b);
			else			linears[(i-1)/3].addView(b);
		}
		
		root.addView(question);
		root.addView(separator);
		for ( LinearLayout ll : linears )
			root.addView(ll);
		setContentView(root);
	}
	
	/**
	 * Register a callback to be invoked when this password is corectly introduced.
	 * @param listener The callback that will run.
	 */
	public void setOnCorrectListener(OnPasswordCorrectListener listener) {
		mListener = listener;
	}
	
	/**
	 * Set the a slap time to dismiss automatically after it is clicked.
	 * This method must be used <strong>before</strong> {@link Dialog#show()} call.
	 * @param time The time in milliseconds.
	 */
	public void setDismissTime(long time) {
		mTime = time;
	}
	
	/**
	 * Clear the dissmiss time, not dismiss automatically.
	 * This method must be used <strong>before</strong> {@link Dialog#show()} call.
	 * @see PasswordDialog#setDismissTime(long)
	 */
	public void clearDissmissTime() {
		setDismissTime(-1);
	}
	
	/**
	 * Inialize the password and the index.
	 * @param random Random generator.
	 * @return The question of the sum.
	 */
	private String initPassword(Random random) {
		int x = random.nextInt(99) + 10;
		int y = random.nextInt(9) + 1;
		mPassword = getArray(x+y);
		mIndex = 0;
		return x + " + " + y + " = ?";
	}
	
	private int[] getArray(int number) {
		String temp = Integer.toString(number);
		int[] out = new int[temp.length()];
		for (int i = 0; i < temp.length(); i++)
		{
		    out[i] = temp.charAt(i) - '0';
		}
		return out;
	}
	
	@Override
	public void show() {
		if ( mTime != -1 )
			new Thread(this).start();
		super.show();
	}

	@Override
	public void run() {
		try {
			mTimeCount = 1;
			while ( mTimeCount > 0 ) {
				mTimeCount--;
				Thread.sleep(mTime);
			}
		} catch (Exception e) {}
		finally {
			mHandler.sendEmptyMessage(0);
		}
	}
	
	//-------------------------------------------------------------------------
	// Handler
	private DialogHandler mHandler = new DialogHandler(this);
	private static class DialogHandler extends Handler {
		
		private final WeakReference<Dialog> mDialog;
		
		public DialogHandler(Dialog dialog) {
			mDialog = new WeakReference<Dialog>(dialog);
		}
		
		@Override
		public void handleMessage(Message msg) {
			try {
				mDialog.get().dismiss();
			}
			catch (Exception e) {
				Log.e(TAG, "Error handling the dissmiss action!", e);
			}
		}
		
	}

}
