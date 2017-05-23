package com.limbika.material.dialog;

import android.content.Context;

import com.limbika.material.widget.LoadingView;

/** Simple dialog to show progressbar */
public class LoadingDialog extends BaseDialog {

	public LoadingDialog(Context context) {
		super(context);
		setContentView(new LoadingView(context));
		setCancelable(false);
	}

}
