package com.limbika.material.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;

import com.limbika.material.R;
import com.limbika.material.dialog.MaterialAlertDialog;

/**
 * Styled spinner.
 */
public class MaterialSpinner extends Button {
	
	private String					mTitle = "";
	private Object[]				mItems;
	private int						mSelection;
	private OnItemSelectedListener	mListener;
	
	public MaterialSpinner ( Context context) {
		this(context, null);
	}
	
	public MaterialSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
		Drawable icon = getContext().getResources().getDrawable(R.drawable.ic_styled_spinner);
		icon.setBounds(10, 0, 34, 24);
		setCompoundDrawables(icon, null,null, null);
		setTextColor(Color.WHITE);
	}
	
	public void setTitle ( String title ) {
		mTitle = title;
	}
	
	public void setTitle ( int stringResourceId ) {
		setTitle(getContext().getResources().getString(stringResourceId));
	}
	
	public void setItems(Object[] items, int selection) {
		mItems = items;
		mSelection = selection;
		setText(items[selection].toString());
		invalidate();
	}
	
	public int getSelectedItemPosition() {
		return mSelection;
	}
	
	public Object getSelectedItem() {
		return mItems[mSelection];
	}
	
	public void setOnItemSelectedListener(OnItemSelectedListener listener) {
		mListener = listener;
	}
	
    @Override
    public boolean performClick()
    {
        final MaterialAlertDialog.Builder builder = MaterialAlertDialog.createBuilder(this.getRootView().getContext(), mTitle);
        builder.setSingleChoiceItems(mItems, mSelection, new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if ( mListener != null)
					mListener.onItemSelected(parent, view, position, id);
				mSelection = position;
				setText(mItems[mSelection].toString());
			}
		});
        
        Dialog dialog = builder.create();
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();

        return true;
    }
}