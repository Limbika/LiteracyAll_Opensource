package com.literacyall.app.customui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by RAFI on 9/21/2016.
 */

public class CustomView extends View {
    public CustomView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomView(Context context) {
        super(context);
    }

    boolean drawGlow = false;
    //this is the pixel coordinates of the screen
    float glowX = 0;
    float glowY = 0;
    //this is the radius of the circle we are drawing
    float radius = 200;
    //this is the paint object which specifies the color and alpha level
    //of the circle we draw
    Paint paint = new Paint();

    {
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setAlpha(50);
    }

    ;

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (drawGlow)
            canvas.drawCircle(glowX, glowY, radius, paint);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                drawGlow = false;
                invalidate();
            }
        }, 800);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            drawGlow = true;
        } /*else if (event.getAction() == MotionEvent.ACTION_UP)
            drawGlow = false;*/

        glowX = event.getX();
        glowY = event.getY();
        this.invalidate();
        return false;
    }
}