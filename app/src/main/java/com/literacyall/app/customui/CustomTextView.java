package com.literacyall.app.customui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by ibrar on 10/26/2016.
 */

public class CustomTextView extends TextView {
    Context context;

    public CustomTextView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }


    public void init() {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "font/segoe_script.ttf");
        setTextColor(Color.BLACK);
        setTypeface(typeface);
    }
    
}
