package com.literacyall.app.customui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

/**
 * Created by ibrar on 10/25/2016.
 */

public class CustomButton extends Button {

    Context context;

    public CustomButton(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public CustomButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

   /* @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setTextColor(Color.parseColor("#efefef"));

                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                setTextColor(Color.BLACK);

                break;
        }
        return true;
    }*/


    public void init() {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "font/segoe_script.ttf");
        setTextColor(Color.BLACK);
        setTypeface(typeface);
        setBackgroundColor(Color.TRANSPARENT);
    }

}
