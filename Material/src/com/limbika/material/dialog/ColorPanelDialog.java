package com.limbika.material.dialog;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.limbika.material.R;
import com.limbika.material.widget.ColorPanel;
import com.limbika.material.widget.ColorPickerView;

/**
 * Color selector dialog that use {@link ColorPickerView}.
 */
public class ColorPanelDialog extends SelectorDialog {

	public ColorPanelDialog(Context context) {
		super(context);

		final int BUTTONS_HEIGHT = context.getResources().getDimensionPixelOffset(R.dimen.dp_08);
		
		// Magic width trick
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(metrics);
		
		android.view.WindowManager.LayoutParams magic = new android.view.WindowManager.LayoutParams();
		magic.width = metrics.widthPixels/2;
		magic.height = metrics.widthPixels/2;
		
		LinearLayout root = new LinearLayout(context);
		root.setOrientation(LinearLayout.VERTICAL);
		root.setLayoutParams(magic);
		root.setBackgroundResource(R.drawable.bg_dialog);
		
		LinearLayout.LayoutParams panelParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, metrics.widthPixels/2-BUTTONS_HEIGHT);
		panelParams.leftMargin = context.getResources().getDimensionPixelSize(R.dimen.dp_05);
		panelParams.rightMargin = context.getResources().getDimensionPixelSize(R.dimen.dp_05);
		
		final ColorPanel panel = new ColorPanel(context);
		panel.setLayoutParams(panelParams);
		
		LinearLayout.LayoutParams okParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		okParams.weight = 1;
		
		Button ok = new Button(context);
		ok.setLayoutParams(okParams);
		ok.setBackgroundResource(R.drawable.btn_material);
		ok.setText(android.R.string.ok);
		ok.setTextSize( getContext().getResources().getDimension(R.dimen.sp_02) );
		ok.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
				
				int color = panel.getColor();
				onSelect( String.format("#%06X", 0xFFFFFF & color) );
			}
		});
		
		LinearLayout.LayoutParams cancelParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		cancelParams.weight = 1;
		
		Button cancel = new Button(context);
		cancel.setLayoutParams(cancelParams);
		cancel.setBackgroundResource(R.drawable.btn_material);
		cancel.setText(android.R.string.cancel);
		cancel.setTextSize( getContext().getResources().getDimension(R.dimen.sp_02) );
		cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();				
			}
		});
		
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, BUTTONS_HEIGHT);
		
		LinearLayout buttons = new LinearLayout(getContext());
		buttons.setOrientation(LinearLayout.HORIZONTAL);
		buttons.setLayoutParams(params);
		buttons.addView(ok);
		buttons.addView(cancel);
		
		root.addView(panel);
		root.addView(buttons);
		
		setContentView(root);
	}

}
