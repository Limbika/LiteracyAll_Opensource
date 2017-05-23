package com.limbika.material.dialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import com.limbika.material.R;

class FileAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<File> mItems;
	private File mDirectory;
	private boolean mHideFiles;
	private String mMimetype;
	private int mCurrentThread = 0;
	
	public FileAdapter(Context context, boolean hideFiles) {
		mContext = context;
		mDirectory = Environment.getExternalStorageDirectory();
		mHideFiles = hideFiles;
		mItems = new ArrayList<File>();
		mMimetype = null;
		
		loadItems();
	}
	
	public File getPathFile() {
		return mDirectory;
	}
	
	public String getPathName() {
		return mDirectory.getAbsolutePath();
	}
	
	public boolean isRoot() {
		return mDirectory.getParentFile() == null;
	}
	
	public void setMimetype(String mimetype) {
		mMimetype = mimetype;
		loadItems();
	}
	
	public void setDir(File dir) {
		mDirectory = dir;
		loadItems();
	}

	public void upParent() {
		mDirectory = isRoot() ? mDirectory : mDirectory.getParentFile();
		mItems = new ArrayList<File>();
		loadItems();
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public File getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CheckedTextView view = (CheckedTextView) convertView;
		if (view == null) {
			view = new CheckedTextView(mContext);
			view.setLayoutParams( new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT) );
			view.setTextSize( mContext.getResources().getDimension(R.dimen.sp_02) );
			view.setTextColor(Color.BLACK);
			view.setSingleLine();
			view.setGravity(Gravity.CENTER);
			view.setPadding(4, 4, 4, 4);
			view.setBackgroundResource( R.drawable.btn_neutral_rect_transparent );
		}
		
		// Security check because the continuous notifyDataSetChanged()
        // causes java.lang.ArrayIndexOutOfBoundsException: length=0; index=0
        // in getItem(position)
        if ( getCount() == 0 ) return view;

		File file = getItem(position);
		int image = file.isDirectory() ? R.drawable.ic_folder : R.drawable.ic_file;
		
		view.setText( file.getName() );
		view.setCompoundDrawablePadding(8);
		view.setCompoundDrawablesWithIntrinsicBounds(image, 0, 0, 0);

		return view;
	}
	
	private void loadItems() {
		mCurrentThread = mCurrentThread + 1;
		final int current = mCurrentThread;
		final Handler handler = new Handler();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				if ( mHideFiles ) {
					mItems.clear();
					File[] files = mDirectory.listFiles();
					if ( current != mCurrentThread ) return;
					
					if ( files != null ) {
						for ( File file : files ) {
							if ( file.isDirectory() ) {
								mItems.add(file);
							}
						}
					}
				}
				else if ( mMimetype != null ) {
					mItems.clear();
					File[] files = mDirectory.listFiles();
					if ( current != mCurrentThread ) return;
					
					for (File file : files) {
						if ( file.isDirectory() ) {
							mItems.add(file);
						}
						else {
							String mimeType = getMimeType( file.getAbsolutePath() );
							if ( mimeType != null && mimeType.startsWith(mMimetype) ) {
								mItems.add(file);
							}
						}
					}
				}
				else {
					File[] files = mDirectory.listFiles();
					if ( mCurrentThread != current ) return;
					mItems = files != null ? new ArrayList<File>(Arrays.asList(files)) : new ArrayList<File>();
				}
				
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						notifyDataSetChanged();
					}
				});
			}
		}).start();
	}
	
	private static String getMimeType(String url) {
	    String type = null;
	    String extension = MimeTypeMap.getFileExtensionFromUrl(url);
	    if (extension != null) {
	        type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
	    }
	    return type;
	}
	
}
