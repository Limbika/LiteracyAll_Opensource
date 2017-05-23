package com.limbika.material.dialog;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.limbika.material.R;
import com.limbika.material.widget.LoadingBarView;

public class MaterialProgressDialog extends BaseDialog {
	
	private TextView		mTitle;
	private TextView		mMessage;
	private LoadingBarView	mProgressBar;
	private String			mTitleText;
	private String			mMessageText;
	
	public static enum ProgressStrategy {
		
		HORIZONTAL 	(R.layout.progressdialog_horizontal),
		LARGE		(R.layout.progressdialog_large);
		
		private int mLayoutResource;
		private ProgressStrategy (int layoutResource) {
			mLayoutResource = layoutResource;
		}
		
		public int getLayoutResource () {
			return mLayoutResource;
		}
	}
	
	public MaterialProgressDialog (Context context, ProgressStrategy strategy) {
		super (context);
		init(strategy);
	}
		
	private void init(ProgressStrategy strategy) {
		setContentView(strategy.getLayoutResource());
		setCancelable(false);
		
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(metrics);
		
		View content = findViewById(R.id.content);
		content.setMinimumWidth( metrics.widthPixels/3 );
		
		mTitle = (TextView) findViewById(R.id.title);
		mMessage = (TextView) findViewById(R.id.message);
		
		if ( strategy == ProgressStrategy.HORIZONTAL ) {
			mProgressBar 	= (LoadingBarView) findViewById(R.id.progressBar);
		}
		
		mTitle.setCompoundDrawablesWithIntrinsicBounds(getContext().getApplicationInfo().icon, 0, 0, 0);
	}
	
	public void setMessage (String msg) {
		mMessage.setText(msg);
		mMessageText = msg;
	}
	
	public void setMessage (int resId) {
		mMessage.setText(resId);
		mMessageText = getContext().getResources().getString(resId);
	}
	
	public void setTitle (String title) {
		mTitle.setText(title);
		mTitleText = title;
	}
	
	@Override
	public void setTitle (int resId) {
		mTitle.setText(resId);
		mTitleText = getContext().getResources().getString(resId);
	}
	
	public LoadingBarView getProgressbar() {
		return mProgressBar;
	}
	
	public void setStrategy(ProgressStrategy strategy) {
		init(strategy);
		setTitle(mTitleText);
		setMessage(mMessageText);
	}
}
