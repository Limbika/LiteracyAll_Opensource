package com.literacyall.app.customui;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;


/**
 * Created by ibrar on 5/24/2016.
 */
public class CustomAnimation {

    public static void clear(View view) {
        view.setAnimation(null);
    }

    // ShakeAnimation by Rokan
    public static void shake(View view) {

        RotateAnimation rotateAnimation = new RotateAnimation(-30, 60, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setRepeatMode(2);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        view.setAnimation(rotateAnimation);

    }

    // BlinkAnimation by Rokan
    public static void blink(final View view) {

        final AlphaAnimation toAlpha = new AlphaAnimation(1, 0);
        final AlphaAnimation fromAlpha = new AlphaAnimation(0, 1);

        toAlpha.setDuration(1000);
        toAlpha.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                view.startAnimation(fromAlpha);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        fromAlpha.setDuration(1000);
        fromAlpha.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.startAnimation(toAlpha);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        view.startAnimation(toAlpha);

    }

    // Slide UP To Down Animation by Rokan
    public static void topToBottom(View view) {

        TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, 0.0f, -400.0f, 0.0f);
        translateAnimation.setDuration(1000);
        view.setVisibility(View.VISIBLE);
        view.startAnimation(translateAnimation);

    }


}
