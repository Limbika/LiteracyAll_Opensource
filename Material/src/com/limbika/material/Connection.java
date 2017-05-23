package com.limbika.material;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.provider.Settings;

import com.limbika.material.dialog.MaterialAlertDialog;
import com.limbika.shared.Util;

public class Connection {
	
	private Activity mActivity;
	private Runnable mCheckRunnable;
	private Runnable mCancelRunnable;
	private int mRequestCode;
	private int mMessageRes;
	
	public Connection(Activity activity, int res) {
		mActivity = activity;
		mMessageRes = res;
	}
	
	/**
	 * Check if the device is online.
	 * @param requestCode The request code of the activity on result.
	 * @param runnable The runnable that will be executed if 
	 */
	public void check(int requestCode, Runnable runnable) {
		if ( Util.isOnline(mActivity) ) {
			if ( runnable != null) runnable.run();
		}
		else {
			mCheckRunnable = runnable;
			launchWifiSettings();
		}
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ( requestCode == mRequestCode ) {
			check(mRequestCode, mCheckRunnable);
		}
	}
	
	/**
	 * Set callback when there are no conection and user cancel it.
	 * @param runnable Called when the user cancel the conection error.
	 */
	public void setCancelCallback(Runnable runnable) {
		mCancelRunnable = runnable;
	}
	
    /**
     * Starts the wifi settings activity with a previous alert dialog.
     */
    public void launchWifiSettings() {
    	MaterialAlertDialog.Builder builder = MaterialAlertDialog.createBuilder(mActivity, mMessageRes);
    	builder.setCancelable(false);
    	builder.setPositiveButton(android.R.string.ok, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
		    	mActivity.startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), mRequestCode);
			}
		});
    	builder.setNegativeButton(android.R.string.cancel, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mActivity.finish();
				if ( mCheckRunnable != null && mCancelRunnable != null ) mCancelRunnable.run();
			}
		});
    	builder.show();
    }

}
