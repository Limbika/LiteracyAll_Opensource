package com.limbika.material.dialog;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.limbika.material.R;

/**
 * App adapter with all the installed applications.
 */
public class AppApapter extends BaseAdapter {
	
	/**
	 * Interface definition for a callback to be invoked when a App is clicked.
	 */
	public interface OnAppClickListener {
		public void onClick(ApplicationInfo info); 
	}
	
	private List<ApplicationInfo> 	mApps = null;
	private Context 				mContext;
	private PackageManager			mPackageManager;
	private OnAppClickListener		mListener;

	public AppApapter(Context context) {
		mContext = context;
		mPackageManager = context.getPackageManager();
		mApps = getApps();
	}
	
	@Override
	public int getCount() {
		return mApps.size();
	}

	@Override
	public Object getItem(int position) {
		return mApps.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mApps.get(position).hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if ( convertView == null ) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_app_item, parent, false);
		}
		
		final ApplicationInfo appInfo = mApps.get(position);
		
		ImageView icon = (ImageView) convertView.findViewById(R.id.app_icon);
		TextView label = (TextView)  convertView.findViewById(R.id.app_label);
		
		icon.setImageDrawable(appInfo.loadIcon(mPackageManager));
		label.setText(mPackageManager.getText(appInfo.packageName, appInfo.labelRes, appInfo).toString());
		convertView.setTag(appInfo.packageName);
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		convertView.setLayoutParams(lp);
		convertView.setBackgroundResource( R.drawable.btn_neutral_round_transparent );
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if ( mListener != null )	
					mListener.onClick(appInfo);
			}
		});

		return convertView;
	}
	
	/**
	 * Register a callback to be invoked when this App is clicked.
	 * @param listener The callback that will run.
	 */
	public void setListener(OnAppClickListener listener) {
		mListener = listener;
	}
	
	private ArrayList<ApplicationInfo> getApps()
	{
        List<ApplicationInfo> allPackages = mPackageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        ArrayList<ApplicationInfo> packages = new ArrayList<ApplicationInfo>();
        
        for (ApplicationInfo info : allPackages) {
         	if ( info.labelRes !=0 && info.icon !=0 && mPackageManager.getLaunchIntentForPackage(info.packageName) != null) 
        		packages.add(info);		
        }
        return packages;
	}

}
