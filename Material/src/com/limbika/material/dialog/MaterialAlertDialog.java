package com.limbika.material.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.limbika.material.R;

/**
 * Material alert cool dialog.
 */
public class MaterialAlertDialog extends BaseDialog {
	
	/**
	 * Builder to create StyledAlertDialog.
	 */
	public static class Builder {
		
		private MaterialAlertDialog mDialog;
		
		/*package*/ Builder(Context context) {
			mDialog = new MaterialAlertDialog(context);
		}
		
		/*package*/ Builder(Context context, int theme) {
			mDialog = new MaterialAlertDialog(context, theme);
		}
		
		public Builder setPositiveButton(int resource, OnClickListener listener) {
			mDialog.setPositiveButton(resource, listener);
			return this;
		}
		
		public Builder setPositiveButton(String msg, OnClickListener listener) {
			mDialog.setPositiveButton(msg, listener);
			return this;
		}
		
		public Builder setNegativeButton(int resource, OnClickListener listener) {
			mDialog.setNegativeButton(resource, listener);
			return this;
		}
		
		public Builder setNegativeButton(String msg, OnClickListener listener) {
			mDialog.setNegativeButton(msg, listener);
			return this;
		}
		
		public Builder setNeutralButton(int resource, OnClickListener listener) {
			mDialog.setNeutralButton(resource, listener);
			return this;
		}
		
		public Builder setNeutralButton(String msg, OnClickListener listener) {
			mDialog.setNeutralButton(msg, listener);
			return this;
		}
		
		public Dialog create() {
			return mDialog;
		}
		
		public void show() {
			mDialog.show();
		}
		
		public Builder setView(View view) {
			return this;
		}
		
		public Builder setSingleChoiceItems(Object[] list, int checkedItem, OnItemClickListener listener) {
			mDialog.setListView(list, checkedItem, null, listener);
			return this;
		}
		
		public Builder setSingleChoiceItems(int checkedItem, BaseAdapter adapter, OnItemClickListener listener) {
			mDialog.setListView(null, checkedItem, adapter, listener);
			return this;
		}
		
		public Builder setCancelable(boolean cancelable) {
			mDialog.setCancelable(cancelable);
			return this;
		}
		
		public void setText(String msg) {
			mDialog.setText(msg);
		}
		
	}
	
	/**
	 * @param context The application context.
	 * @param textResource
	 * @param style
	 * @return
	 */
	public static MaterialAlertDialog.Builder createBuilder ( Context context, int textResource)  {
		String text = context.getResources().getString(textResource);
		return createBuilder(context, text);
	}

	public static MaterialAlertDialog.Builder createBuilder ( Context context, int textResource, int theme)  {
		String text = context.getResources().getString(textResource);
		return createBuilder(context, text, theme);
	}
	
	/**
	 * @param context The application context.
	 * @param text
	 * @param style
	 * @return
	 */
	public static MaterialAlertDialog.Builder createBuilder ( Context context, String text) {
		Builder builder = new Builder(context);
		builder.setText(text);
		return builder;
	}
	
	public static MaterialAlertDialog.Builder createBuilder ( Context context, String text, int theme) {
		Builder builder = new Builder(context, theme);
		builder.setText(text);
		return builder;
	}
	
	// -------------------------------------------------------------------------
	// Members
	
	private Context mContext;
	private Button mPositiveButton;
	private Button mNegativeButton;
	private Button mNeutralButton;
	private TextView mTextView;
	private Object[] mList;
	private int mSelection;
	
	/*package*/ MaterialAlertDialog(Context context) {
		super(context);
		mContext = context;
		init();
	}

	/*package*/ MaterialAlertDialog(Context context, int theme) {
		super(context, theme);
		mContext = context;
		init();
	}
	
	private void init() {
		setContentView(R.layout.dialog_alert);
		
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(metrics);
		
		View content = findViewById(R.id.content);
		content.setMinimumWidth( metrics.widthPixels/3 );
		
		mPositiveButton = (Button) findViewById(R.id.btn_positive);
		mNegativeButton = (Button) findViewById(R.id.btn_negative);
		mNeutralButton = (Button) findViewById(R.id.btn_neutral);
		mTextView = (TextView) findViewById(R.id.tv_content);
		mTextView.setCompoundDrawablesWithIntrinsicBounds(getContext().getApplicationInfo().icon, 0, 0, 0);
	}
	
	public int getSelection() {
		return mSelection;
	}
	
	private void setText(String msg) {
		mTextView.setText(msg);
	}
	
	private void setPositiveButton(int resource, OnClickListener listener) {
		String msg = getContext().getResources().getString(resource);
		setPositiveButton(msg, listener);
	}
	
	private void setPositiveButton(String msg, OnClickListener listener) {
		mPositiveButton.setText(msg);
		mPositiveButton.setOnClickListener(new ButtonClickListener(this, listener, 1));
		mPositiveButton.setVisibility(View.VISIBLE);
	}
	
	private void setNegativeButton(int resource, OnClickListener listener) {
		String msg = getContext().getResources().getString(resource);
		setNegativeButton(msg, listener);
	}
	
	private void setNegativeButton(String msg, OnClickListener listener) {
		mNegativeButton.setText(msg);
		mNegativeButton.setOnClickListener(new ButtonClickListener(this, listener, 2));
		mNegativeButton.setVisibility(View.VISIBLE);
	}
	
	private void setNeutralButton(int resource, OnClickListener listener) {
		String msg = getContext().getResources().getString(resource);
		setNeutralButton(msg, listener);
	}
	
	private void setNeutralButton(String msg, OnClickListener listener) {
		mNeutralButton.setText(msg);
		mNeutralButton.setOnClickListener(new ButtonClickListener(this, listener, 3));
		mNeutralButton.setVisibility(View.VISIBLE);
	}
	
	private class ButtonClickListener implements View.OnClickListener {
		
		private OnClickListener mListener;
		private Dialog mDialog;
		private int mWhich;
		
		ButtonClickListener(Dialog dialog, OnClickListener listener, int which) {
			mDialog = dialog;
			mListener = listener;
			mWhich = which;
		}

		@Override
		public void onClick(View v) {
			if ( mListener != null ) mListener.onClick(mDialog, mWhich);
			mDialog.dismiss();
		}
	}
	
	private void setListView(Object[] list, int checkedItem, BaseAdapter adapter, final OnItemClickListener listener) {
		mList = list;
		mSelection = checkedItem;
		
		ListView view = new ListView(getContext());
		view.setCacheColorHint(0);
		view.setSelector(android.R.color.transparent);
		view.setAdapter( adapter == null ? new ListViewAdapter() : adapter );
		view.setCacheColorHint(0);
		view.setDivider(getContext().getResources().getDrawable(android.R.color.transparent));
		view.setDividerHeight(getContext().getResources().getDimensionPixelOffset(R.dimen.dp_01));
		view.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				dismiss();
				mSelection = position;
				if ( listener != null ) 
					listener.onItemClick(parent, view, position, id);
			}
		});
		
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		lp.setMargins(0, 0, 0, (int) mContext.getResources().getDimension(R.dimen.dp_04));
		
		LinearLayout layout = (LinearLayout) findViewById(R.id.content);
		layout.removeViewAt(1);
		layout.addView(view, lp);
	}
	
	private class ListViewAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mList == null ? 0 : mList.length;
		}

		@Override
		public String getItem(int position) {
			return mList[position].toString();
		}

		@Override
		public long getItemId(int position) {
			return 1;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView view = (TextView) convertView;
			if ( view == null ) {
				view = new TextView(getContext());
			}
			
			int padding = getContext().getResources().getDimensionPixelOffset(R.dimen.dp_02);
			
			if ( Build.VERSION.SDK_INT > 10 ) {
				view.setAlpha( mSelection == position ? 1F : 0.5F);
			}
			else {
				view.setSelected( mSelection == position );
			}
			
			view.setGravity(Gravity.CENTER);
			view.setTextSize(getContext().getResources().getDimension(R.dimen.sp_02));
			view.setText( getItem(position) );
			view.setTextColor(Color.BLACK);
			view.setBackgroundResource( R.drawable.btn_neutral_round_transparent );
			view.setPadding(padding, padding, padding, padding);
			view.setLayoutParams( new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT) );
			
			return view;
		}
		
	}
	
}
