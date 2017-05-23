package com.limbika.material.dialog;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;

import com.limbika.material.R;
import com.limbika.material.dialog.AppApapter.OnAppClickListener;

/**
 * Apps selector dialog.
 */
public class AppDialog extends SelectorDialog {
	
	public AppDialog(Context context) {
		super(context);
		setContentView(R.layout.dialog_app);
		setCancelable(false);
		
		// Magic width trick & Style
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(metrics);
		
		View content = findViewById(R.id.content);
		content.setBackgroundResource(R.drawable.bg_dialog);
		content.getLayoutParams().width = metrics.widthPixels/2;
		
		AppApapter appadapter = new AppApapter(getContext());
		appadapter.setListener(new OnAppClickListener() {
			
			@Override
			public void onClick(ApplicationInfo info) {
				onSelect(info.packageName);
				dismiss();
			}
		});
		
		GridView gv = (GridView) findViewById(R.id.gridview_app);
		gv.getLayoutParams().height = (int) (metrics.heightPixels / 1.5);	// Magic
		gv.setAdapter(appadapter);
		
		Button cancel = (Button) findViewById(R.id.btn_cancel);
		cancel.setBackgroundResource(R.drawable.btn_material);
		cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}
	
}
