package com.limbika.material.dialog;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.limbika.material.R;

/**
 * Media chooser dialog.
 */
public class MediaDialog extends SelectorDialog {
	
	private static final String TAG = "MediaDialog";
	
	private Activity mActivity;
	private ArrayList<File> mList;
	private int mResMusic;
	private int mResVideo;

	/**
	 * MediaDialog constructor
	 * @param context The context
	 * @param resMusic Music text resource for button. "Music"
	 * @param resVideo Video text resource for button. "Video"
 	 */
	public MediaDialog(Activity context, int resMusic, int resVideo) {
		super(context);
		setCancelable(false);
		mActivity = context;
		mResMusic = resMusic;
		mResVideo = resVideo;
		
		music();
		final BaseAdapter adapter = new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView _convertView = (TextView) convertView;
				if ( _convertView == null ) {
					_convertView = new TextView(getContext());
					_convertView.setLayoutParams( new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT) );
					_convertView.setTextColor(Color.BLACK);
					_convertView.setTextSize( getContext().getResources().getDimension(R.dimen.sp_02) );
					_convertView.setBackgroundResource( R.drawable.btn_neutral_round_transparent );
				}
				_convertView.setText( getItem(position).getName() );
				_convertView.setTag( getItem(position) );
				return _convertView;
			}
			
			@Override
			public long getItemId(int position) {
				return 1;
			}
			
			@Override
			public File getItem(int position) {
				return mList.get(position);
			}
			
			@Override
			public int getCount() {
				return mList.size();
			}
		};
		
		int padding = context.getResources().getDimensionPixelOffset(R.dimen.dp_03);

		// Magic width trick
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(metrics);
		
		android.view.WindowManager.LayoutParams magic = new android.view.WindowManager.LayoutParams();
		magic.width = metrics.widthPixels/2;
		magic.height = LayoutParams.WRAP_CONTENT; //(int) (SessionManager.getInstance().getDisplayHeight() / 1.5);
		
		LinearLayout root = new LinearLayout(context);
		root.setOrientation(LinearLayout.VERTICAL);
		root.setLayoutParams(magic);
		root.setBackgroundResource(R.drawable.bg_dialog);
//		root.getLayoutParams().height = (int) (SessionManager.getInstance().getDisplayHeight() / 1.5);
	
		LinearLayout tabs = new LinearLayout(context);
		tabs.setOrientation(LinearLayout.HORIZONTAL);
		tabs.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		
		LayoutParams lp = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
		lp.weight = 1;
		
		final Button music = new Button(context);
		final Button video = new Button(context);
		
		music.setId(android.R.id.button1);
		music.setBackgroundResource(R.drawable.btn_material);
		music.setSelected(true);
		music.setText(mResMusic);
		music.setTextSize( getContext().getResources().getDimension(R.dimen.sp_03) );
		music.setLayoutParams(lp);
		music.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				music.setSelected(true);
				video.setSelected(false);
				music();
				adapter.notifyDataSetChanged();
			}
		});
		
		video.setId(android.R.id.button2);
		video.setBackgroundResource(R.drawable.btn_material);
		video.setText(mResVideo); 
		video.setTextSize( getContext().getResources().getDimension(R.dimen.sp_03) );
		video.setLayoutParams(lp);
		video.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				music.setSelected(false);
				video.setSelected(true);
				video();
				adapter.notifyDataSetChanged();
			}
		});
		
		ListView lv = new ListView(context);
		lv.setPadding(padding, padding, padding, padding);
		lv.setLayoutParams( new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT) );
		lv.getLayoutParams().height = (int) (metrics.heightPixels / 1.5);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				File file = (File) view.getTag();
				onSelect( file.getAbsolutePath() );
				dismiss();
			}
		});
		
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, context.getResources().getDimensionPixelOffset(R.dimen.dp_08));
		
		Button cancel = new Button(context);
		cancel.setLayoutParams(params);
		cancel.setBackgroundResource(R.drawable.btn_material);
		cancel.setText(android.R.string.cancel);
		cancel.setTextSize( getContext().getResources().getDimension(R.dimen.sp_02) );
		cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();				
			}
		});
		
		tabs.addView(music);
		tabs.addView(video);
		
		root.addView(tabs);
		root.addView(lv);
		root.addView(cancel);
		
		setContentView(root);
	}
	
	public void setMusicSelectorMode() {
		// Dirty hack to put the dialog
		// in music mode only.
		findViewById(android.R.id.button2).setVisibility(View.GONE);
	}
	
	/**
	 * Populate the list with music.
	 */
	private void music() {
		String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

		String[] projection = {
		        MediaStore.Audio.Media._ID,
		        MediaStore.Audio.Media.ARTIST,
		        MediaStore.Audio.Media.TITLE,
		        MediaStore.Audio.Media.DATA,
		        MediaStore.Audio.Media.DISPLAY_NAME,
		        MediaStore.Audio.Media.DURATION
		};
		
		@SuppressWarnings("deprecation")
		Cursor cursor = mActivity.managedQuery(
		        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
		        projection,
		        selection,
		        null,
		        null);

		mList = new ArrayList<File>();
		while(cursor.moveToNext()){
			String song = cursor.getString(0) + "||" + cursor.getString(1) + "||" +   cursor.getString(2) + "||" +   cursor.getString(3) + "||" +  cursor.getString(4) + "||" +  cursor.getString(5);
		    Log.d(TAG, "song=" + song);
		    mList.add( new File(cursor.getString(3)) );
		}
	}
	
	/**
	 * Populate the list with video.
	 */
	private void video() {
		String[] projection = {
		        MediaStore.Video.Media._ID,
		        MediaStore.Video.Media.ARTIST,
		        MediaStore.Video.Media.TITLE,
		        MediaStore.Video.Media.DATA,
		        MediaStore.Video.Media.DISPLAY_NAME,
		        MediaStore.Video.Media.DURATION
		};
		
		@SuppressWarnings("deprecation")
		Cursor cursor = mActivity.managedQuery(
		        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
		        projection,
		        null,
		        null,
		        null);

		mList = new ArrayList<File>();
		while(cursor.moveToNext()){
			String video = cursor.getString(0) + "||" + cursor.getString(1) + "||" +   cursor.getString(2) + "||" +   cursor.getString(3) + "||" +  cursor.getString(4) + "||" +  cursor.getString(5);
		    Log.d(TAG, "video=" + video);
		    mList.add( new File(cursor.getString(3)) );
		}
	}
}
