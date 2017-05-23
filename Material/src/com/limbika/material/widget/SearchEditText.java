package com.limbika.material.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.limbika.material.R;

public class SearchEditText extends EditText
{
	private static Bitmap sMagnifier = null;
	
	private class OnTouchListenerHandler implements View.OnTouchListener
	{
		public View.OnTouchListener mOtherEvent = null;
		private EditText mOwner;
		
		public OnTouchListenerHandler(EditText owner) {
			this.mOwner = owner;
		}
		
		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			if ( event.getX() > mOwner.getWidth()-getMagnifier().getWidth()-10 )
			{
				if (event.getAction() == MotionEvent.ACTION_DOWN)
					mOwner.onEditorAction(EditorInfo.IME_ACTION_SEARCH);
				return true;
			}

			if ( mOtherEvent != null )
				return mOtherEvent.onTouch(v, event);
			else
				return false;
		}
	}
	private OnTouchListenerHandler mOnTouchListener = new OnTouchListenerHandler ( this );
	
	public SearchEditText(Context context)
	{
		this(context, null);
	}
	public SearchEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		sMagnifier = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_search);
		super.setOnTouchListener(mOnTouchListener);
		InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow( getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS );
	}
	
	@Override
	public void setOnTouchListener ( View.OnTouchListener listener )
	{
		mOnTouchListener.mOtherEvent = listener;
	}
	
	@Override
	public void onDraw ( Canvas canvas )
	{
		super.onDraw( canvas );
		int width = getMagnifier().getWidth();
		int height = getMagnifier().getHeight();
		canvas.drawBitmap(getMagnifier(), getWidth()-width-10.0f, (getHeight()-height-10.0f)/2.0f, null);
	}

	@Override
	public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
	    InputConnection connection = super.onCreateInputConnection(outAttrs);
	    int imeActions = outAttrs.imeOptions&EditorInfo.IME_MASK_ACTION;
	    if ((imeActions&EditorInfo.IME_ACTION_DONE) != 0) {
	        // clear the existing action
	        outAttrs.imeOptions ^= imeActions;
	        // set the DONE action
	        outAttrs.imeOptions |= EditorInfo.IME_ACTION_SEARCH;
	    }
	    if ((outAttrs.imeOptions&EditorInfo.IME_FLAG_NO_ENTER_ACTION) != 0) {
	        outAttrs.imeOptions &= ~EditorInfo.IME_FLAG_NO_ENTER_ACTION;
	    }
	    return connection;
	}
	
	private Bitmap getMagnifier() {
		if ( sMagnifier == null )
			sMagnifier = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_search);
		return sMagnifier;
	}
}
