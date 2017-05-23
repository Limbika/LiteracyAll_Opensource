package com.limbika.material.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

public abstract class BaseDialog extends Dialog {

	public BaseDialog(Context context) {
		super(context);
		init();
	}
	
	public BaseDialog(Context context, int theme) {
		super(context, theme);
		init();
	}
	
	private void init() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);
	}
	
}
