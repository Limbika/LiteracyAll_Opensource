package com.literacyall.app.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.literacyall.app.R;
import com.literacyall.app.customui.CustomView;

/**
 * Created by RAFI on 9/21/2016.
 */

public class TestActivity extends BaseActivity {
    RelativeLayout rLayout;
    CustomView customView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abclayout);
        rLayout = (RelativeLayout) findViewById(R.id.rLayout);
        customView = new CustomView(this);
        TextView valueTV = new TextView(this);
        valueTV.setText("hallo hallo");

        valueTV.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        valueTV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                }
                return false;

            }
        });

        rLayout.addView(customView);
        rLayout.addView(valueTV);


    }
}
