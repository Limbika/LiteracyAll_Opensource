package com.limbika.material.dialog;

import android.content.Context;

/**
 * Generic dialog that select something.
 */
public abstract class SelectorDialog extends BaseDialog {
	
	/**
	 * Interface definition for a callback to be invoked when a dialog option is selected.
	 */
	public interface OnSelectListener {
		
		/**
		 * Called when a dialog option has been selected.
		 * @param data The data selected.
		 */
		public void onSelect(String data);
		
	}
	
	// -------------------------------------------------------------------------
	// Members
	private OnSelectListener mListener;
	
	/**
	 * Build a selector dialog.
	 * @param context The application context.
	 * @param style The style of the dialog, if it is null the style is {@link Style#NEUTRAL}.
	 */
	public SelectorDialog(Context context) {
		super(context);
	}
	
	/**
	 * Register a callback to be invoked when the path is selected.
	 * @param listener The callback that will run. 
	 */
	public void setOnSelectListener(OnSelectListener listener) {
		mListener = listener;
	}
	
	/**
	 * Called when a selection is done.
	 * @param data The data selected.
	 */
	protected void onSelect(String data) {
		if ( mListener != null ) mListener.onSelect(data);
	}
	
}
