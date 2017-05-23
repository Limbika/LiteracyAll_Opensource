package com.limbika.material.dialog;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.limbika.material.R;

@SuppressLint("InlinedApi")
public class FileDialog extends SelectorDialog {
	
	public enum Strategy {
		FILE,
		DIR
	}
	
	private FileAdapter mAdapter;
	private File mSelectedFile = null;
	private Strategy mStrategy;
	
	public FileDialog(Context context, Strategy strategy) {
		super(context);
		setCancelable(false);
		setContentView(R.layout.dialog_file);
		mStrategy = strategy;

		// Magic width trick & Style
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(metrics);
		
		View content = findViewById(R.id.content);
		content.setBackgroundResource(R.drawable.bg_dialog);
		content.getLayoutParams().width = metrics.widthPixels/2;
		content.getLayoutParams().height = (int) (metrics.heightPixels / 1.5);

		final View ok = findViewById(R.id.btn_ok);
		final View cancel = findViewById(R.id.btn_cancel);
		final View up = findViewById(R.id.btn_up);
		final GridView grid = (GridView) findViewById(R.id.grid);
		final TextView pathView = (TextView) findViewById(R.id.tv_path);
		
		pathView.setText( Environment.getExternalStorageDirectory().getAbsolutePath() );

		ok.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if ( mStrategy == Strategy.FILE && mSelectedFile == null ) return;
				onSelect( mStrategy == Strategy.FILE ? mSelectedFile.getAbsolutePath() : mAdapter.getPathFile().getAbsolutePath() );
				dismiss();
			}
		});
		
		cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		
		
		up.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mAdapter.upParent();
				File file = mAdapter.getPathFile();
				pathView.setText( file.getAbsolutePath() );
				
				ok.setVisibility( mStrategy == Strategy.DIR ? View.VISIBLE : View.GONE );
				up.setVisibility( mAdapter.isRoot() ? View.GONE : View.VISIBLE );
				
				if ( mStrategy == Strategy.FILE && Build.VERSION.SDK_INT > 10 ) grid.clearChoices();
			}
		});
		
		mAdapter = new FileAdapter(context, strategy == Strategy.DIR);
		grid.setAdapter(mAdapter);
		grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
				File file = mAdapter.getItem(position);
				
				if ( file.isDirectory() ) {
					mAdapter.setDir(file);
					mSelectedFile = null;
					pathView.setText( file.getAbsolutePath() );
					if ( mStrategy == Strategy.FILE && Build.VERSION.SDK_INT > 10 ) grid.clearChoices();
				}
				else {
					mSelectedFile = file;
					if ( mStrategy == Strategy.FILE ) {
						if ( Build.VERSION.SDK_INT > 10 ) 	grid.setItemChecked(position, true);
						else								view.setSelected(true);
					}
				}
				
				ok.setVisibility( mSelectedFile != null || mStrategy == Strategy.DIR ? View.VISIBLE : View.GONE );
				up.setVisibility( mAdapter.isRoot() ? View.GONE : View.VISIBLE );
			}
		});
		if ( Build.VERSION.SDK_INT > 10) {
			grid.setChoiceMode (mStrategy == Strategy.FILE ? GridView.CHOICE_MODE_SINGLE : GridView.CHOICE_MODE_NONE );
		}
	}

	public void setPath(String path) {
		if ( path == null || path.isEmpty() ) return;
		setPath( new File(path) );
	}
	
	public void setPath(File path) {
		if ( path == null || !path.exists() ) return;
		mAdapter.setDir(path);
	}
	
	public void setMimetype(String mimetype) {
		mAdapter.setMimetype(mimetype);
	}
	
	
}
